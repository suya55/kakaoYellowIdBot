package utils

import base.SpecBase
import models.store.{ProductAction, Product}
import service.store.ProductService

import scala.Product
import scala.concurrent.Await

/**
  * Created by suya55 on 4/6/16.
  */
class ObjectCallerTest extends SpecBase {

    "ObjectCallerTest" should {
        "callMethod" in {
            val product = Product(None,"test",None,"test desc", 10, 1000, ProductAction.STATUS_ON_SALE,None,None)
            Await.result(ProductAction.create(product),duration)
            val product2 = Product(None,"test2",None,"test desc", 10, 1000, ProductAction.STATUS_ON_SALE,None,None)
            Await.result(ProductAction.create(product2),duration)
            ObjectCaller.callMethod[String]("ProductService","getAllName") shouldEqual ProductService.getAllName("","")
        }

    }
}
