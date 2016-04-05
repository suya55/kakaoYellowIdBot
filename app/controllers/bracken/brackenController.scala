package controllers.bracken

import controllers.ApiController
import models.cook.DaumCook
import models.{KeyboardType, Keyboard, Message, UserMessage}
import play.api.libs.json.Json
import play.api.mvc.BodyParsers
import service.store.{UserStepService, StepService}


object BrackenController extends ApiController{
    lazy val initKeyboard = StepService.getInitKeyboard

    def message = userAction(BodyParsers.parse.json) { implicit request =>
        val (message,keyboard) = request.body.validate[UserMessage].fold(
            errors => {
                (Message("잘못된 입력입니다"), initKeyboard)
            },
            userMessage => {
                UserStepService.processStep(request.userStep, userMessage.content)
            }
        )

        Ok(Json.obj("message" -> Json.toJson(message), "keyboard" -> Json.toJson(keyboard)))
    }

    override def keyboard = userAction {
        Ok(Json.toJson(initKeyboard))
    }
}
