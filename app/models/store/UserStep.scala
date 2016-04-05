package models.store

import java.sql.Date
import slick.driver.MySQLDriver.api._
import utils.{Slick, ExtMapper}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

case class UserStep(id: Option[Int],
                    userKey: String,
                    stepId: Int,
                    inputMessage: Option[String],
                    createdAt: Option[Date] = None,
                    updatedAt: Option[Date] = None
                   ) {
}

class UserSteps(tag: Tag) extends Table[UserStep](tag, "user_step") with ExtMapper {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def userKey = column[String]("user_key")

    def stepId = column[Int]("step_id")

    def inputMessage = column[String]("input_message")

    def createdAt = column[Date]("created_at")

    def updatedAt = column[Date]("updated_at", O.Default(new Date(new java.util.Date().getTime)))

    def * = (id.?, userKey, stepId, inputMessage.?, createdAt.?, updatedAt.?) <>(UserStep.tupled, UserStep.unapply)

    def idx = index("idx_unique_user_steps", userKey, unique = true)
}


object UserStepAction extends TableQuery(new UserSteps(_)) with Slick {
    def findOptByUserKey(userKey: String): Option[UserStep] = Await.result(db.run(this.filter(_.userKey === userKey).result.headOption), duration)

    def findOrCreateByUserKey(userKey: String): UserStep = {
        Await.result(db.run(this.filter(_.userKey === userKey).result.headOption), duration) match {
            case Some(userStep) => userStep
            case _ => {
                val us = UserStep(None, userKey = userKey, stepId = 1, None)
                db.run(this += us)
                us
            }
        }
    }

    def update(userKey: String, step: Int, inputMessage: String) = {
        db.run(this.filter(_.userKey === userKey).map(u => (u.stepId, u.inputMessage)).update((step, inputMessage)))
    }
}
