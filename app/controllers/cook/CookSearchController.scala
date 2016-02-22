package controllers.cook

import java.util.concurrent.TimeUnit

import play.api.Play.current
import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WS
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration._

class CookSearchController extends Controller {

  def message = Action { implicit request =>
    val body = request.body.asJson match {
      case Some(req) => {
        (req \ "content").asOpt[String] match {
          case Some(content) =>{
            val q = "요리"+content.split(" ").mkString("+")
            val result = Await.result((WS.url("https://apis.daum.net/search/blog").withQueryString(("apikey","2facc253045575ff294bf7c9ab1fdfb9"),("q",q),("output","json"),("pageno","1")).get()),FiniteDuration(5000, TimeUnit.MILLISECONDS)).json
            (result \ "channel" \ "item").as[JsArray].value.map{ item =>
              s"${(item \ "title").as[String].replaceAll("&lt;b&gt;","").replaceAll("&lt;/b&gt;","")} \n${(item \ "link").as[String]}"
            }.mkString("\n")
          }
          case _ => "결과가 없습니다"
        }
      }
      case _ => "잘못된 입력입니다"
    }

    Ok(Json.obj("message" -> Json.obj("text"->body), "keyboard" -> Json.obj("type" -> "text")))
  }

  def keyboard = Action {
    Ok(Json.obj("type" -> "text"))
  }
}
