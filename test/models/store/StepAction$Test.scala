package models.store

import base.SpecBase
import org.specs2.mutable.Specification

/**
  * Created by suya55 on 3/28/16.
  */
class StepAction$Test extends SpecBase{

    "StepAction$Test" should {
        "getFirstSteps" in {
            println(StepAction.getFirstSteps.message)
            ok
        }

    }
}
