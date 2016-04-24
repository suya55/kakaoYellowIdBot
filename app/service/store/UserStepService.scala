package service.store

import exceptions.BadRequestException
import models.store._
import models.{Photo, Keyboard, Message}
import play.api.Logger

import scala.util.matching.Regex

object UserStepService {
    def processStep(userStep: UserStep, inputMessage: String): (Message, Keyboard) = {
        val oldStep = StepAction.findById(userStep.stepId).setUserKey(userStep.userKey)

        val reg = new Regex(oldStep.inputMessages)
        if (reg.findFirstIn(inputMessage).isEmpty) {
            UserStepAction.update(userStep.userKey, oldStep.errorStep.get, inputMessage)
            throw new BadRequestException(oldStep.errorMessage.getOrElse("잘못된 입력입니다."), StepAction.findById(oldStep.errorStep.get).setUserKey(userStep.userKey).getKeyboard)
        }
        val nextStep: Step = StepAction.getNextStep(oldStep, inputMessage).setUserKey(userStep.userKey)
        if (nextStep.actionMethod.isDefined && !nextStep.actionMethod.get.isEmpty) {
            val am = nextStep.actionMethod.get.split(",")
            if (am.length > 1) {
                ActionMethodCaller.callMethod(am.head.split("\\.").head, am.head.split("\\.").last, am.last, inputMessage, userStep.userKey)
            } else {
                ActionMethodCaller.callMethod(am.head.split("\\.").head, am.head.split("\\.").last, null, inputMessage, userStep.userKey)
            }
        }
        UserStepAction.update(userStep.userKey, nextStep.id.get, inputMessage)
        val msg = StepAction.convertMessage(userStep.userKey, inputMessage, nextStep.message)
        val imgPattern = "\\<img\\>(.+)\\<\\/img\\>".r
        val matches = imgPattern.findAllMatchIn(msg)
        Logger.debug("message : "+msg)
        val message = if (matches.isEmpty) {
            Message(msg)
        } else {
            Message(imgPattern.replaceAllIn(msg, ""), Option(Photo(matches.next().group(1), 300, 300)))
        }
        (message, nextStep.getKeyboard)
    }

}
