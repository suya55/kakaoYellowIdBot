package utils

import java.util.Date

import com.fasterxml.jackson.core.JsonParseException
import play.api.Logger
import play.api.db.DB
import play.api.libs.json.{Json, JsObject}
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import play.api.Play.current
object Slick extends Slick {
    def apply(name: String) = new Slick {
        override lazy val DBName = name
    }
}

trait Slick {
    protected lazy val DBName = "default"
    val duration = Duration(5, SECONDS)
    implicit def db = Database.forDataSource(DB.getDataSource(DBName))
}


trait ExtMapper {
    implicit val date2SqlDate = MappedColumnType.base[Date, java.sql.Timestamp](d => new java.sql.Timestamp(d.getTime), d => new java.util.Date(d.getTime))
    implicit val list2String = MappedColumnType.base[List[String], String](array => array.mkString(","), string => string.split(",").toList)
    implicit val jsonObjMapper = MappedColumnType.base[JsObject, String](json => json.toString(), s => try {
        Json.parse(s).as[JsObject]
    } catch {
        case e: JsonParseException => {
            Logger.error(s"JsObjectMapper Error [data:$s]", e)
            Json.obj()
        }
    })

}