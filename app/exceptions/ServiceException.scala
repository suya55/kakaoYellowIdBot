package exceptions

import models.{Keyboard, Message}
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.{Result, Results}

case class ServiceException(message: String = "unknown error", keyboard:Keyboard=Keyboard.ERROR, cause: Throwable = null, code: Int = 0, httpStatus: Int = 500) extends Exception(message, cause) with Results {

    def this(message: String, keyboard:Keyboard, code: Int, httpStatus: Int) = this(message, keyboard, null, code, httpStatus)

    def toDefaultKeyboardResponse : Result= {
        try {
            Ok(Json.obj("message" -> Json.toJson(Message(message)), "keyboard" -> Json.toJson(keyboard)))
        } catch {
            case e:Throwable =>
                Status(httpStatus)(Json.obj("code" -> code, "message" -> message))
        }
    }

    val messageKey = s"error.code.$code"
    val messageKeyForOperator = s"error.code.op.$code"
}

class BadRequestException(override val message: String = "잘못된 요청입니다.", override val keyboard:Keyboard = Keyboard.ERROR) extends ServiceException(message,keyboard, 100, BAD_REQUEST)
