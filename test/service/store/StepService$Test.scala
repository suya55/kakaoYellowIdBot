package service.store

import base.SpecBase
import models.store.{Product, ProductAction, StepAction}
import play.api.libs.json.Json

import scala.concurrent.Await

class StepService$Test extends SpecBase {

    "StepService$Test" should {
        "getInitKeyboard" in {
            val kb = Json.toJson(StepAction.getFirstSteps.getKeyboard)
            println(kb)
            (kb \ "type").as[String] shouldEqual "buttons"
            (kb \ "buttons").as[Array[String]] should_==  StepAction.getFirstSteps.inputMessage.toArray
        }
        "convertInputMessage" in {
            val product = Product(None,"test",None,"test desc", 10, 1000, ProductAction.STATUS_ON_SALE,None,None)
            Await.result(ProductAction.create(product),duration)
            val product2 = Product(None,"test2",None,"test desc", 10, 1000, ProductAction.STATUS_ON_SALE,None,None)
            Await.result(ProductAction.create(product2),duration)
            val str = "가나다라 /#{ProductService.getAllName}/#{ProductService.getAllName}"
            StepAction.convertInputMessage("abc",str) shouldEqual s"가나다라 /${ProductService.getAllName("","")}/${ProductService.getAllName("","")}"
        }

    }
}
