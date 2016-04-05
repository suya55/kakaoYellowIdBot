package models.store

import models.{KeyboardType, Keyboard}
import slick.driver.MySQLDriver.api._

import slick.lifted.TableQuery
import utils.{ObjectCaller, ExtMapper, Slick}

import scala.concurrent.Await

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class Step(id: Int,
                actionMethod: Option[String],
                message: String,
                keyboardType: String,
                inputMessage: String,
                targetStepIds: List[String],
                isDefault: Boolean = false,
                errorMessage: Option[String],
                errorStep: Option[Int]
               ) {

    lazy val inputMessages: String = StepAction.convertInputMessage(inputMessage)
    val getKeyboard: Keyboard = {
        Keyboard(KeyboardType.withName(keyboardType), keyboardType match {
            case "text" => None
            case _ => Option(StepAction.convertInputMessage(inputMessage).split("|"))
        })
    }
}

class Steps(tag: Tag) extends Table[Step](tag, "step") with ExtMapper {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def actionMethod = column[String]("action_method")

    def message = column[String]("message")

    def keyboardType = column[String]("keyboard_type")

    def inputMessage = column[String]("input_message")

    def targetStepIds = column[List[String]]("target_step_ids")

    def isDefault = column[Boolean]("is_default")

    def errorMessage = column[String]("error_message")

    def errorStep = column[Int]("error_step")

    def * = (id, actionMethod.?, message, keyboardType, inputMessage, targetStepIds, isDefault, errorMessage.?, errorStep.?) <>(Step.tupled, Step.unapply)
}


object StepAction extends TableQuery(new Steps(_)) with Slick {
    val reg = "\\#\\{(.+)\\.(.+)\\}".r

    def getFirstSteps = Await.result(db.run(this.filter(_.isDefault === true).result.head), duration)

    def findById(id: Int) = Await.result(db.run(this.filter(_.id === id).result.head), duration)

    def convertInputMessage(ip: String): String = {
        if (reg.findFirstIn(ip).isDefined) {
            convertInputMessage(reg.replaceFirstIn(ip, ObjectCaller.callMethod[String]("$1", "$2", null)))
        } else {
            ip
        }
    }

    def getNextStep(step: Step, msg: String): Step = {
        KeyboardType.withName(step.keyboardType) match {
            case KeyboardType.text =>
                findById(step.targetStepIds.head.toInt)
            case _ => {
                val idx = step.inputMessages.split("|").indexOf(msg)
                findById(step.targetStepIds(idx).toInt)
            }
        }
    }
}
