package service.store

import base.SpecBase
import models.store.{ProductAction, Product, Order}

import scala.concurrent.Await

/**
  * Created by suya55 on 4/7/16.
  */
class OrderService$Test extends SpecBase {

    "OrderService$Test" should {
        "sendGoogleDocs" in {
            val pid :Int= Await.result(ProductAction.create(Product(Option(1),"p1", None,"asdf",1,200,"",None,None)),duration)
            val o = Order(Option(1244),"abc",pid,Option(1),Option("name"))
            Await.result(OrderService.sendGoogleDocs(o),duration)
            ok
        }

    }
}
