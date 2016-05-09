package models.openbot

import slick.lifted.Tag
import slick.driver.MySQLDriver.api._

import slick.lifted.TableQuery
import utils.{ ExtMapper, Slick}

import scala.concurrent.ExecutionContext.Implicits.global

case class UserKeywordTo(id: Option[Int], userKey: String, chatKey: String)

class UserKeywordTos(tag: Tag) extends Table[UserKeywordTo](tag, "user_keyword_to") with ExtMapper {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def userKey = column[String]("user_key")

    def chatKey = column[String]("chat_key")

    def * = (id.?, userKey, chatKey) <>(UserKeywordTo.tupled, UserKeywordTo.unapply)
    def idx = index("uk_u_k_f", (userKey), unique = true)
}
object UserKeywordToAction extends TableQuery(new UserKeywordTos(_)) with Slick {
    override val db = Slick("castor").db
    def create(userKeyword:UserKeywordTo): Unit = db.run(this insertOrUpdate userKeyword)
}