package service.store

import exceptions.ServiceException
import models.store._
import play.api.Logger
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient
import play.api.mvc.Results
import utils.{Slick, Utils}

import scala.concurrent.Await

object OrderService {
    def createOrder(userKey: String, productName: String): Int = {
        val product = ProductAction.findByName(productName)
        val id = OrdersAction.createOrder(userKey, product.id.get)
        sendGoogleDocs(OrdersAction.findById(id))
        id
    }

    def completeOrder(userKey: String): Unit = {
        val order = OrdersAction.findOrderingByUserKey(userKey).get
        val product = ProductAction.findById(order.productId)
        OrdersAction.updateStatus(userKey,OrdersAction.STATUS_COMPLETE)
        sendGoogleDocs(OrdersAction.findById(order.id.get))
    }

    def requestPaymentCheck(userKey: String): Unit = {
        OrdersAction.updateStatus(userKey,OrdersAction.STATUS_CHECK_PAYMENT)
        sendGoogleDocs(OrdersAction.findOrderingByUserKey(userKey).get)
    }

    def updateOrderCount(userKey: String, cnt: String): Unit = {
        OrdersAction.updateField(userKey, "order_count", cnt.toInt)
        sendGoogleDocs(OrdersAction.findOrderingByUserKey(userKey).get)
    }

    def updateReceiverName(userKey: String, name: String): Unit = {
        OrdersAction.updateField(userKey, "receiver_name", name)
        sendGoogleDocs(OrdersAction.findOrderingByUserKey(userKey).get)
    }

    def updateReceiverPhone(userKey: String, phone: String): Unit = {
        OrdersAction.updateField(userKey, "receiver_phone", phone)
        sendGoogleDocs(OrdersAction.findOrderingByUserKey(userKey).get)
    }

    def updateReceiverAddress(userKey: String, address: String): Unit = {
        OrdersAction.updateField(userKey, "receiver_address", address)
        sendGoogleDocs(OrdersAction.findOrderingByUserKey(userKey).get)
    }

    def updateDeliverMessage(userKey: String, message: String): Unit = {
        OrdersAction.updateField(userKey, "deliver_message", message)
        sendGoogleDocs(OrdersAction.findOrderingByUserKey(userKey).get)
    }

    def updatePayerName(userKey: String, name: String): Unit = {
        OrdersAction.updateField(userKey, "payer", name)
        sendGoogleDocs(OrdersAction.findOrderingByUserKey(userKey).get)
    }

//    def getCancelMessage(userKey:String, input:String):String ={
//        OrdersAction.updateField(userKey, "status",OrdersAction.STATUS_CANCEL)
//    }

    def getOrderDetail(userKey:String, input:String):String ={
        val order:Order = OrdersAction.findById(input.toInt)
        val product :Product = ProductAction.findById(order.productId)
        s"""
           |상품명 : ${product.name}
           |수량 : ${order.orderCount.get}개
           |단가 : ${product.price}
           |총 주문금액 : ${Utils.currencyFormat(order.totalPrice.get)}원
           |배송지 : ${order.receiverAddress.get}
           |받으시는분 : ${order.receiverName.get}
           |받으시는분 연락처 : ${order.receiverPhone.get}
           |배송요청사항 : ${order.deliverMessage.get}
           |주문자명 : ${order.payer.get}
         """.stripMargin
    }

    def getOrderButtons(userKey:String, input:String) : String ={
        val orderList : List[Order] = OrdersAction.findCompleteByUserKey(userKey)
        if(orderList.isEmpty){
            ""
        }else{
            orderList.map(o => s"${o.id.get}").mkString("|")
        }
    }
    def getOrderCheckMessage(userKey:String,inputMessage:String):String={
        val orderList : List[Order] = OrdersAction.findCompleteByUserKey(userKey)
        if(orderList.isEmpty){
            "주문내역이 없습니다."
        }else{
            s"총 ${orderList.size}건의 주문이 있습니다. 상세 주문내역을 확인하시려면 아래 주문번호를 눌러주세요."
        }
    }

    def getOrderResult(userKey:String, inputMessage:String) : String = {
        val order = OrdersAction.findOrderingByUserKey(userKey)
        if(order.isEmpty) {
            throw new ServiceException("잘못된 주문번호입니다.")
        }
        val totalPrice = (order.get.product.price*order.get.orderCount.get)
        Await.result(OrdersAction.updateField(userKey, "total_price", totalPrice),Slick.duration)
        s"""
          |주문번호는 [${order.get.id.get}]입니다.
          |총주문금액은 ${Utils.currencyFormat(totalPrice)}원입니다.
          |주문을 완료하시겠습니까?
        """.stripMargin
    }

    def sendGoogleDocs( order:Order) ={
        import scala.concurrent.ExecutionContext.Implicits.global
        val url ="https://script.google.com/macros/s/AKfycbyPaUljoxxKBCS-0ljoPsh5i2BgpCMqsni5M2r0D-ao6Fl6ZQg/exec"
        val wsClient = NingWSClient()
        wsClient.url(url).withRequestTimeout(10000).withQueryString(("주문번호",order.id.get.toString)
            ,("상품명",order.product.name)
            ,("이름",order.receiverName.getOrElse(""))
            ,("주소",order.receiverAddress.getOrElse(""))
            ,("전화번호",order.receiverPhone.getOrElse(""))
            ,("배송요청사항",order.deliverMessage.getOrElse(""))
            ,("수량",order.orderCount.getOrElse(0).toString)
            ,("주문금액",order.totalPrice.getOrElse(0).toString)
            ,("입금자명",order.payer.getOrElse(""))
            ,("상태",OrdersAction.getReadableStatus(order.status))
            ,("user_key",order.userKey)).get().map{ res =>
//            Logger.info("GoogleDocs call : "+res.body.toString)
            res.body
        }

    }

}

