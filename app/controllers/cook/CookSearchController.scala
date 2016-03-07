package controllers.cook

import java.util.concurrent.TimeUnit

import controllers.Actions
import models.cook.DaumCook
import models.{UserMessage, Keyboard, KeyboardType, Message}
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.json.JsArray
import play.api.libs.ws.WS
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration._


class CookSearchController extends Controller with Actions{

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
            val q = "요리 " + userMessage.content.split(" ").mkString("+")
            val result = Await.result((WS.url("https://apis.daum.net/search/blog").withQueryString(("apikey", "2facc253045575ff294bf7c9ab1fdfb9"), ("q", q), ("output", "json"), ("pageno", "1")).get()), FiniteDuration(5000, TimeUnit.MILLISECONDS)).json
            val text = (result \ "channel" \ "item").as[JsArray].value.map { item =>
              s"${(item \ "title").as[String].replaceAll("&lt;b&gt;", "").replaceAll("&lt;/b&gt;", "")} \n${(item \ "link").as[String]}"
            }.mkString("\n")
            (Message(text), getInitKeyboard)
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
