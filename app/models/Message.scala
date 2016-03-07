package models

import models.KeyboardType.KeyboardType
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, JsPath, Reads}
import utils.EnumUtils

case class Message(text: String, photo: Option[Photo] = None, messageButton: Option[MessageButton] = None)

object Message {
    implicit val messageReads: Reads[Message] = (
      (JsPath \ "text").read[String] and
        (JsPath \ "photo").readNullable[Photo] and
        (JsPath \ "message_button").readNullable[MessageButton]
      ) (Message.apply _)

    implicit val messageWrites: Writes[Message] = (
      (JsPath \ "text").write[String] and
        (JsPath \ "photo").writeNullable[Photo] and
        (JsPath \ "message_button").writeNullable[MessageButton]
      ) (unlift(Message.unapply))
}


case class Photo(url: String, width: Int, height: Int)

object Photo {
    implicit val photoReads: Reads[Photo] = (
      (JsPath \ "url").read[String] and
        (JsPath \ "width").read[Int] and
        (JsPath \ "height").read[Int]
      ) (Photo.apply _)

    implicit val photoWrites: Writes[Photo] = (
      (JsPath \ "url").write[String] and
        (JsPath \ "width").write[Int] and
        (JsPath \ "height").write[Int]
      ) (unlift(Photo.unapply))
}

case class MessageButton(label: String, url: String)

object MessageButton {
    implicit val messageButtonReads: Reads[MessageButton] = (
      (JsPath \ "label").read[String] and
        (JsPath \ "url").read[String]
      ) (MessageButton.apply _)
    implicit val messageButtonWrites: Writes[MessageButton] = (
      (JsPath \ "label").write[String] and
        (JsPath \ "url").write[String]
      ) (unlift(MessageButton.unapply))

}

case class Keyboard(keyboardType: KeyboardType, buttons: Option[Array[String]]) {
    val isError: Boolean = keyboardType.equals(KeyboardType.error)
}

object Keyboard {
    val DEFAULT = Keyboard(KeyboardType.text, None)
    val ERROR = Keyboard(KeyboardType.error, None)

    implicit val keyboardReads: Reads[Keyboard] = (
      (JsPath \ "type").read[KeyboardType] and
        (JsPath \ "buttons").readNullable[Array[String]]
      ) (Keyboard.apply _)

    implicit val keyboardWrites: Writes[Keyboard] = (
      (JsPath \ "type").write[KeyboardType] and
        (JsPath \ "buttons").writeNullable[Array[String]]
      ) (unlift(Keyboard.unapply))
}

object KeyboardType extends Enumeration {
    type KeyboardType = Value
    val buttons, text, error = Value
    implicit val keyboardTypeReads: Reads[KeyboardType.Value] = EnumUtils.enumReads(KeyboardType)
}

case class UserMessage(userKey: String, messageType: String, content: String)

object UserMessage {
    implicit val userMessageReads: Reads[UserMessage] = (
      (JsPath \ "user_key").read[String] and
        (JsPath \ "type").read[String] and
        (JsPath \ "content").read[String]
      ) (UserMessage.apply _)
}
