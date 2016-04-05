package controllers.cook

import controllers.Actions
import models.cook.DaumCook
import models.{Keyboard, KeyboardType, Message, UserMessage}
import play.api.libs.json.Json
import play.api.mvc._
import utils.Slick

class CookSearchController extends Controller with Actions with Slick{

  def message = LoggingAction(BodyParsers.parse.json) { implicit request =>
    val (message,keyboard) = request.body.validate[UserMessage].fold(
      errors => {
        (Message("잘못된 입력입니다"), getInitKeyboard)
      },
      userMessage => {
        userMessage.content match {
          case DaumCook.KEY_SEARCH => (Message("검색 하고 싶은 요리명, 레시피 또는 재료명을 띄어쓰기를 구분자로 넣어주세요.\n예) 백종원 김치찌개\n예)김치 당근 양파 오이\n예)만능된장"), Keyboard.DEFAULT)
          case DaumCook.KEY_TODAY => (DaumCook.recommendRecipe(), getInitKeyboard)
          case _ => {
            (DaumCook.searchRecipe(userMessage.content), getInitKeyboard)
          }
        }
      }
    )

    Ok(Json.obj("message" -> Json.toJson(message), "keyboard" -> Json.toJson(keyboard)))
  }

  def keyboard = LoggingAction {
    Ok(Json.toJson(getInitKeyboard))
  }

  private def getInitKeyboard = Keyboard(KeyboardType.buttons,Option(Array(DaumCook.KEY_SEARCH, DaumCook.KEY_TODAY)))
}
