package utils

import models.cook.DaumCook
import org.specs2.mutable.Specification

import scala.util.matching.Regex

/**
  * Created by suya55 on 3/7/16.
  */
class HtmlParser$Test extends Specification {

    "HtmlParser$Test" should {
        "linkSequence" in {
            val doc  = HtmlParser.get("http://m.cook.miznet.daum.net/ranking/hitrecipe/rankingRecipeList?rankType=2")
            for( l <- HtmlParser.linkSequence(doc,"cook/recipe/read")){
                println(s"${l.title} / ${l.href} / ${l.desc} / ${l.imageSrc}")
            }

            println(DaumCook.recommendRecipe())
            ok
        }

    }
}
