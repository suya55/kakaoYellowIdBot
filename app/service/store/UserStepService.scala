package service.store

import exceptions.BadRequestException
import models.store._
import models.{Keyboard, Message}

import scala.util.matching.Regex

object UserStepService {
    def processStep(userStep:UserStep, inputMessage:String): (Message,Keyboard) = {
        val oldStep = StepAction.findById(userStep.stepId)
        val reg = new Regex(oldStep.inputMessages)
        if(reg.findFirstIn(inputMessage).isEmpty){
            throw new BadRequestException(oldStep.errorMessage.getOrElse("잘못된 입력입니다."), StepAction.findById(oldStep.errorStep.get).getKeyboard)
        }
        val nextStep:Step = StepAction.getNextStep(oldStep,inputMessage)
        if(nextStep.actionMethod.isDefined){
            val am = nextStep.actionMethod.get.split(",")
            if(am.length > 1){
                ActionMethodCaller.callMethod(am.head.split(".").head,am.head.split(".").last,am.last,inputMessage,userStep.userKey )
            }else{
                ActionMethodCaller.callMethod(am.head.split(".").head,am.head.split(".").last,null,inputMessage,userStep.userKey )
            }
        }
        UserStepAction.update(userStep.userKey,nextStep.id,inputMessage)
        (Message(nextStep.message),nextStep.getKeyboard)
    }

}
