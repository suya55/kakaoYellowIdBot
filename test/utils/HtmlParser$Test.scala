package utils

import org.specs2.mutable.Specification

/**
  * Created by suya55 on 3/7/16.
  */
class HtmlParser$Test extends Specification {

    "HtmlParser$Test" should {
        "linkSequence" in {
            val doc  = HtmlParser.get("http://m.cook.miznet.daum.net/calendar/list?type=D")
            for( l <- HtmlParser.linkSequence(doc,"cook/recipe/read")){
                println(s"${l.title} / ${l.href} / ${l.desc} / ${l.imageSrc}")
            }
            ok
        }

    }
}
