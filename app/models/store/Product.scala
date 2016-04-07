package models.store

import java.sql.Date
import slick.driver.MySQLDriver.api._
import utils.{Slick, ExtMapper}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

case class Product(id: Option[Int],
                   name: String,
                   imageSrc: Option[String],
                   description: String,
                   remainCount: Int,
                   price: Int,
                   status: String,
                   startAt: Option[Date],
                   endAt: Option[Date],
                   createdAt: Option[Date] = None,
                   updatedAt: Option[Date] = None

                  )

class Products(tag: Tag) extends Table[Product](tag, "product") with ExtMapper {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def imageSrc = column[String]("image_src")

    def description = column[String]("description")

    def remainCount = column[Int]("remain_count")

    def price = column[Int]("price")

    def status = column[String]("status")

    def startAt = column[Date]("start_at")

    def endAt = column[Date]("end_at")

    def createdAt = column[Date]("created_at")

    def updatedAt = column[Date]("update_at")

    def * = (id.?, name, imageSrc.?, description, remainCount, price, status, startAt.?, endAt.?, createdAt.?, updatedAt.?) <>(Product.tupled, Product.unapply)

    def idx = index("idx_product_name", name, unique = false)
}

object ProductAction extends TableQuery(new Products(_)) with Slick {


    val STATUS_PREPARED = "PREPARED"
    val STATUS_ON_SALE = "SALE"
    val STATUS_SOLD_OUT = "SOLD_OUT"
    val STATUS_STOP = "STOP"

    def findByName(name: String): Product = Await.result(db.run(ProductAction.filter(_.name === name).result.head), duration)

    def findById(id: Int): Product = Await.result(db.run(ProductAction.filter(_.id === id).result.head), duration)

    def create(product:Product) = db.run((this returning this.map(_.id)) += product)

    def getProductNames(): Seq[String] = {
        val sql = sql"SELECT `name` FROM product WHERE status = 'SALE' AND now() BETWEEN ifnull(start_at,now()) AND ifnull(end_at,now()) ".as[String]
        Await.result(db.run(sql), duration)
    }
}
