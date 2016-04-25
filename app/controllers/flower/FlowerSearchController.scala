package controllers.flower

import javax.inject.Inject

import controllers.Actions
import models.cook.DaumCook
import models.flower.Flower
import models.{Keyboard, KeyboardType, Message, UserMessage}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._
import utils.Slick

class FlowerSearchController @Inject()(ws: WSClient) extends Controller with Actions with Slick {

    def message = LoggingAction(BodyParsers.parse.json) { implicit request =>
        val (message, keyboard) = request.body.validate[UserMessage].fold(
            errors => {
                (Message("잘못된 입력입니다"), getInitKeyboard)
            },
            userMessage => {
                if (userMessage.messageType.equals("photo")) {
                    (Message(Flower.searchImg(userMessage.content)), getInitKeyboard)
                }
                else {
                    (Flower.search(userMessage.content), getInitKeyboard)
                }
            }
        )

        Ok(Json.obj("message" -> Json.toJson(message), "keyboard" -> Json.toJson(keyboard)))
    }

    def keyboard = LoggingAction {
        Ok(Json.toJson(getInitKeyboard))
    }

    private def getInitKeyboard = Keyboard.DEFAULT
}
