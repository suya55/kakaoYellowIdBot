package models.openbot

import slick.lifted.Tag
import slick.driver.MySQLDriver.api._

import slick.lifted.TableQuery
import utils.{ ExtMapper, Slick}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class UserKeywordFrom(id: Option[Int], userKey: String, keyword: String, chatKey: String)

class UserKeywordFroms(tag: Tag) extends Table[UserKeywordFrom](tag, "user_keyword_from") with ExtMapper {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def userKey = column[String]("user_key")

    def keyword = column[String]("keyword")

    def chatKey = column[String]("chat_key")

    def * = (id.?, userKey, keyword, chatKey) <>(UserKeywordFrom.tupled, UserKeywordFrom.unapply)

    def idx = index("uk_u_k_f", (userKey, chatKey,keyword), unique = true)
}
object UserKeywordFromAction extends TableQuery(new UserKeywordFroms(_)) with Slick {
    override val db = Slick("castor").db
    def create(userKeyword:UserKeywordFrom) = this insertOrUpdate userKeyword
    def delete(userKey:String, keyword:String, chatKey:String) = this.filter{u => u.userKey === userKey && u.keyword === keyword && u.chatKey === chatKey}.delete
    def deleteAll(userKey:String, chatKey:String) = db.run(this.filter{u => u.userKey === userKey && u.chatKey === chatKey}.delete)
//    def findByKeywordsAndChatKey(keywords:Array[String], chatKey:String) :Future[List[UserKeywordFrom]]= {
////        db.run(this.filter(_.keyword inSetBind keywords).filter(_.chatKey === chatKey).result)
//    }
}