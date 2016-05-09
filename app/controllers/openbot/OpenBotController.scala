package controllers.openbot

import controllers.Actions
import models.openbot._
import play.api.Logger
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.dbio.DBIO
import utils.Slick
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class OpenBotController extends Controller with Actions with Slick {
    override val db = Slick("castor").db

    def message = LoggingAction.async(BodyParsers.parse.json) { implicit request =>
        import models.openbot.OpenBotMessage.WebHookReads
        Future {
            try {
                request.body.validate[WebHook].fold(
                    errors => {
                        Logger.debug(s"error has occurred. ${errors.map { e => e._2.map { v => v.message }.mkString("\n") }.mkString("\n")} ")
                    },
                    wh => {
                        Logger.debug(s"type : ${wh.`type`}")
                        wh.`type` match {
                            case "message" => {
                                val msg = wh.message
                                Logger.debug(s"text : ${msg.text}")
                                if (msg.isCommand) {
                                    val a = DBIO.seq()
                                    msg.command.get match {
                                        case "!설정" => {
                                            for (t <- msg.params) {
                                                a andThen UserKeywordFromAction.create(UserKeywordFrom(None, msg.user_key, t, msg.chat_key))
                                            }
                                            UserKeywordFromAction.db.run(a) onSuccess {
                                                case r => Logger.debug("========= inserted!")
                                            }
                                        }
                                        case "!해재" => {
                                            for (t <- msg.params) {
                                                a andThen UserKeywordFromAction.delete(msg.user_key, t, msg.chat_key)
                                            }
                                            UserKeywordFromAction.db.run(a)
                                        }

                                        case "!모두해재" => {
                                            UserKeywordFromAction.deleteAll(msg.user_key, msg.chat_key)
                                        }

                                        case "!여기로" => {
                                            UserKeywordToAction.create(UserKeywordTo(None, msg.user_key, msg.chat_key))
                                        }
                                    }

                                } else {
                                    //                                UserKeywordFromAction.findByKeywordsAndChatKey(msg.text.split(" "), msg.chat_key) onSuccess {
                                    //                                    case list => list.map { u => Logger.debug(s"${List(u.id, u.userKey).mkString(",")}") }
                                    //                                }
                                }
                            }
                            case _ =>
                        }
                    }
                )

                Ok("Ok..")
            } finally db.close()
        }
    }
}
