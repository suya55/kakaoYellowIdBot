package models.store

import models.{KeyboardType, Keyboard}
import play.api.Logger
import slick.driver.MySQLDriver.api._

import slick.lifted.TableQuery
import utils.{ObjectCaller, ExtMapper, Slick}

import scala.concurrent.Await

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class Step(id: Option[Int] = None,
                actionMethod: Option[String],
                message: String,
                keyboardType: String,
                inputMessage: String,
                targetStepIds: List[String],
                isDefault: Boolean = false,
                errorMessage: Option[String],
                errorStep: Option[Int]
               ) {
    var userKey:String = null
    def setUserKey(uk:String) : Step = {
        this.userKey = uk
        this
    }
    lazy val inputMessages: String = StepAction.convertInputMessage(userKey,inputMessage)
    lazy val convertedTargetStepIds: List[Int] = {
        var retList: List[Int] = List[Int]()
        inputMessage.split("\\|").zip(targetStepIds).map { m =>
            val retVal = StepAction.convertInputMessage(userKey,m._1)
            if(null != retVal){
                for (tmp <- retVal.split("\\|")) {
                    retList = retList ::: List(m._2.toInt)
                }
            }
        }

        retList
    }
    lazy val getKeyboard: Keyboard = {
        Keyboard(KeyboardType.withName(keyboardType), keyboardType match {
            case "text" => None
            case _ => Option(StepAction.convertInputMessage(userKey,inputMessage).split("\\|").filter(_.nonEmpty))
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

    def * = (id.?, actionMethod.?, message, keyboardType, inputMessage, targetStepIds, isDefault, errorMessage.?, errorStep.?) <>(Step.tupled, Step.unapply)
}


object StepAction extends TableQuery(new Steps(_)) with Slick {
    val reg = "\\#\\{([a-zA-Z]+)\\.([a-zA-Z]+)\\}".r

    def create(step: Step) = db.run((this returning this.map(_.id) += step))

    def getFirstSteps = Await.result(db.run(this.filter(_.isDefault === true).result.head), duration)

    def findById(id: Int) = Await.result(db.run(this.filter(_.id === id).result.head), duration)

    def convertInputMessage(userKey:String, inputMessage: String): String = {
        val findResult = reg.findFirstIn(inputMessage)
        if (findResult.isDefined) {
            val matcher = reg.findAllMatchIn(inputMessage).next()
            val obj = matcher.group(1)
            val method = matcher.group(2)
            Logger.debug(s"######### $userKey $inputMessage $obj $method")
            convertInputMessage(userKey, reg.replaceFirstIn(inputMessage, ObjectCaller.callMethod[String](obj, method,userKey,inputMessage)))
        } else {
            inputMessage
        }
    }

    def convertMessage(userKey: String, inputMessage: String, msg: String): String = {
        val findResult = reg.findFirstIn(msg)
        if (findResult.isDefined) {
            val matcher = reg.findAllMatchIn(msg).next()
            val obj = matcher.group(1)
            val method = matcher.group(2)
            convertMessage(userKey, inputMessage, reg.replaceFirstIn(msg, MessageMethodCaller.callMethod[String](obj, method, inputMessage, userKey).toString))
        } else {
            msg
        }
    }

    def getNextStep(step: Step, msg: String): Step = {
        KeyboardType.withName(step.keyboardType) match {
            case KeyboardType.text =>
                findById(step.targetStepIds.head.toInt)
            case _ => {
                val idx = step.inputMessages.split("\\|").indexOf(msg)

                findById(step.convertedTargetStepIds(idx))
            }
        }
    }
}
