package models.openbot

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

case class WebHook(`type`: String, message: Message)

case class Message(msg_id: Int, msg_type: String, user_key: String, chat_key: String, text: String) {
    val params: Array[String] = {
        if (text.startsWith("!")) {
            text.split(" ").drop(1).filter(!_.isEmpty)
        } else {
            Array.empty[String]
        }
    }
    val command: Option[String] = {
        if (text.startsWith("!")) {
            text.split(" ").headOption
        } else {
            None
        }
    }

    def isCommand:Boolean = command.isDefined
}

object Message {
    implicit val MessageReads: Reads[Message] = (
        (JsPath \ "msg_id").read[Int] and
            (JsPath \ "msg_type").read[String] and
            (JsPath \ "user_key").read[String] and
            (JsPath \ "chat_key").read[String] and
            (JsPath \ "text").read[String]
        )(Message.apply _)

}

object OpenBotMessage {
    implicit val WebHookReads: Reads[WebHook] = (
        (JsPath \ "type").read[String] and
            (JsPath \ "message").read[Message]
        )(WebHook.apply _)

}

