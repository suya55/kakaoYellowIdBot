package utils

import models.cook.DaumCook
import models.store._
import org.specs2.mutable.Specification
import slick.lifted.TableQuery
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import play.api.test.{FakeApplication, WithApplication}
import org.specs2.execute._
import slick.driver.MySQLDriver.api._
/**
  * Created by suya55 on 3/7/16.
  */
class HtmlParser$Test extends Specification with Slick{
    val app = {
        val application = FakeApplication()
        play.api.Play.start(application)
        application
    }
    "HtmlParser$Test" should {
        "linkSequence" in {
            val doc  = HtmlParser.get("http://m.cook.miznet.daum.net/ranking/hitrecipe/rankingRecipeList?rankType=2")
            for( l <- HtmlParser.linkSequence(doc,"cook/recipe/read")){
                println(s"${l.title} / ${l.href} / ${l.desc} / ${l.imageSrc}")
            }

//            println(DaumCook.recommendRecipe())
//            println(LastUserOrdersAction.schema.toString)
//            db.run(DBIO.seq(
//                LastUserOrdersAction.schema.create
//            ))
            ok
        }
    }


}
