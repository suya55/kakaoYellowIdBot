package models.store

import base.SpecBase

import scala.concurrent.Await

/**
  * Created by suya55 on 4/5/16.
  */
class OrdersAction$Test extends SpecBase {

    "OrdersAction$Test" should {
        "updateOrder" in {
            OrdersAction.createOrder("abc",1)
            Await.result(OrdersAction.updateField("abc","receiver_name","test"),duration)
            OrdersAction.findByUserKey("abc").head.receiverName.get shouldEqual("test")
            Await.result(OrdersAction.updateField("abc","order_count",10),duration)
            OrdersAction.findByUserKey("abc").head.orderCount.get should_=== 10
        }

        "createOrder" in {
            OrdersAction.createOrder("abc",1)
            OrdersAction.findByUserKey("abc").length must_=== 1
        }

    }
}
