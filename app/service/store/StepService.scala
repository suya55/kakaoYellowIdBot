package service.store

import models.{KeyboardType, Keyboard}
import models.store.StepAction

object StepService {
    def getInitKeyboard = Keyboard(KeyboardType.buttons,Option(StepAction.getFirstSteps.inputMessages.split("|")))
}
