package controllers.bracken

import javax.inject.Inject

import controllers.Actions
import models.store.StepAction
import models.{Message, UserMessage}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{BodyParsers, Controller}
import service.store.UserStepService
import utils.Slick


class BrackenController @Inject() (ws: WSClient)  extends Controller with Actions with Slick{
    def getInitKeyboard = StepAction.getFirstSteps.getKeyboard

    def message = userAction(BodyParsers.parse.json) { implicit request =>
        val (message,keyboard) = request.body.validate[UserMessage].fold(
            errors => {
                (Message("잘못된 입력입니다"), getInitKeyboard)
            },
            userMessage => {
                UserStepService.processStep(request.userStep, userMessage.content)
            }
        )

        Ok(Json.obj("message" -> Json.toJson(message), "keyboard" -> Json.toJson(keyboard)))
    }

    def keyboard = LoggingAction {
        Ok(Json.toJson(getInitKeyboard))
    }
}
