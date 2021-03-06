package models.store

import java.util.Date
import slick.driver.MySQLDriver.api._
import utils.{Slick, ExtMapper}
import scala.concurrent.Await

case class Order(id: Option[Int] = None,
                 userKey: String,
                 productId: Int,
                 orderCount: Option[Int] = None,
                 receiverName: Option[String] = None,
                 receiverPhone: Option[String] = None,
                 receiverAddress: Option[String] = None,
                 deliverMessage: Option[String] = None,
                 payer: Option[String] = None,
                 totalPrice: Option[Int] = None,
                 status: String = OrdersAction.STATUS_ORDERING,
                 createdAt: Option[Date] = None,
                 updatedAt: Option[Date] = Some(new Date())
                ) {
    lazy val product = ProductAction.findById(productId)
}

class Orders(tag: Tag) extends Table[Order](tag, "order") with ExtMapper {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def userKey = column[String]("user_key")

    def productId = column[Int]("product_id")

    def orderCount = column[Int]("order_count")

    def receiverName = column[String]("receiver_name")

    def receiverPhone = column[String]("receiver_phone")

    def receiverAddress = column[String]("receiver_address")

    def deliverMessage = column[String]("deliver_message")

    def payer = column[String]("payer")

    def totalPrice = column[Int]("total_price")

    def status = column[String]("status", O.Default(OrdersAction.STATUS_ORDERING))

    def createdAt = column[Date]("created_at")

    def updatedAt = column[Date]("updated_at", O.Default(new Date(new java.util.Date().getTime)))

    def * = (id.?, userKey, productId, orderCount.?, receiverName.?, receiverPhone.?, receiverAddress.?, deliverMessage.?, payer.?, totalPrice.?, status, createdAt.?, updatedAt.?) <>(Order.tupled, Order.unapply)
}

object OrdersAction extends TableQuery(new Orders(_)) with Slick {
    val PAY_STATUS_BEFORE = "before"
    val PAY_STATUS_CHECK = "check"
    val PAY_STATUS_COMPLETE = "complete"

    val STATUS_ORDERING = "ordering"
    val STATUS_COMPLETE = "complete"
    val STATUS_CHECK_PAYMENT = "check_payment"
    val STATUS_PREPARE_PRODUCT = "prepare_product"
    val STATUS_DELIVER = "deliver"
    val STATUS_FINISH = "finish"
    val STATUS_CANCEL = "cancel"

    def createOrder(userKey: String, productId: Int): Int = {
        val order = findOrderingByUserKey(userKey)
        if (order.isEmpty) {
            val order = Order(userKey = userKey, productId = productId, createdAt = Option(new Date))
            Await.result(db.run((this returning this.map(_.id)) += order), duration)
        } else {
            order.get.id.get
        }
    }

    def updateField(userKey: String, field: String, value: Any) = {
        db.run(sqlu"UPDATE `order` SET #$field = ${value.toString} WHERE user_key = $userKey AND status = 'ordering'")
    }

    def findByUserKey(userKey: String): List[Order] = {
        Await.result(db.run(this.filter(_.userKey === userKey).result), duration).toList
    }

    def findCompleteByUserKey(userKey: String): List[Order] = {
        Await.result(db.run(this.filter(_.userKey === userKey).filterNot(_.status === STATUS_ORDERING).result), duration).toList
    }

    def findOrderingByUserKey(userKey: String): Option[Order] = {
        Await.result(db.run(this.filter(_.userKey === userKey).filter(_.status === STATUS_ORDERING).result.headOption), duration)
    }

    def findById(id: Int): Order = Await.result(db.run(this.filter(_.id === id).result.head), duration)


    def updateStatus(userKey: String, status: String): Unit = {
        db.run(this.filter(_.userKey === userKey).map(o => o.status).update(status))
    }

    def getReadableStatus(status:String):String ={
        status match {
            case STATUS_ORDERING => "주문중"
            case STATUS_CANCEL => "주문취소"
            case STATUS_COMPLETE => "주문완료"
            case STATUS_DELIVER => "배달중"
            case _ => ""
        }
    }
}