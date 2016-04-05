package service.store

import base.SpecBase
import models.store.StepAction
import play.api.libs.json.Json

/**
  * Created by suya55 on 4/4/16.
  */
class StepService$Test extends SpecBase {

    "StepService$Test" should {
        "getInitKeyboard" in {
            val kb = Json.toJson(StepService.getInitKeyboard)
            println(kb)
            (kb \ "type").as[String] shouldEqual "buttons"
            (kb \ "buttons").as[Array[String]] should_==  StepAction.getFirstSteps.inputMessage.toArray
        }

    }
}
