package models.store

import base.SpecBase
import org.specs2.mutable.Specification

import scala.concurrent.Await

class StepAction$Test extends SpecBase{

    "StepAction$Test" should {
        "getFirstSteps" in {
            val step = Step(None,None,null, "button", "상품보기|주문내역확인","2"::"11"::Nil,true,None,None)
            StepAction.create(step)
            println(StepAction.getFirstSteps.message)
            ok
        }

    }

    "Step$Test" should {
        "convertedTargetStepIds" in {
            val product = Product(None,"test",None,"test desc", 10, 1000, ProductAction.STATUS_ON_SALE,None,None)
            Await.result(ProductAction.create(product),duration)
            val product2 = Product(None,"test2",None,"test desc", 10, 1000, ProductAction.STATUS_ON_SALE,None,None)
            Await.result(ProductAction.create(product2),duration)

            val step = Step(None,None,null, "buttons", "주문하기|#{ProductService.getAllName}|처음으로|#{ProductService.getAllName}","1"::"2"::"3"::"4"::Nil,true,None,None)
            StepAction.create(step)
            step.convertedTargetStepIds(0) should_=== 1
            step.convertedTargetStepIds(1) should_=== 2
            step.convertedTargetStepIds(2) should_=== 2
            step.convertedTargetStepIds(3) should_=== 3
            step.convertedTargetStepIds(4) should_=== 4
            step.convertedTargetStepIds(5) should_=== 4
        }
    }
}
