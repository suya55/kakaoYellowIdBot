package models.cook

import java.net.URLEncoder
import java.util.Calendar


import models.{Photo, MessageButton, Message}
import utils.HtmlParser

import scala.util.Random

object DaumCook {
    val KEY_SEARCH = "\uD83C\uDF7D 요리 찾아보기"
    val KEY_TODAY = "\uD83C\uDF72 오늘 뭐 먹지??"
    val KEY_TIP = "꿀팁을 알려줘"
    type JDoc = org.jsoup.nodes.Document
    def recommendRecipe(): Message ={
        val cal:Calendar = Calendar.getInstance()

        val links = List(
            "http://m.cook.miznet.daum.net/ranking/bestrecipe/rankingRecipeList?rankType=1&datekey=3",
            "http://m.cook.miznet.daum.net/ranking/scraprecipe/rankingRecipeList?rankType=8",
            "http://m.cook.miznet.daum.net/ranking/bestrecipe/rankingRecipeList?rankType=1&datekey=2",
            "http://m.cook.miznet.daum.net/ranking/hitrecipe/rankingRecipeList?rankType=2",
            "http://m.cook.miznet.daum.net/ranking/hitrecipe/rankingRecipeList?rankType=3",
            "http://m.cook.miznet.daum.net/ranking/hitrecipe/rankingRecipeList?rankType=4",
            "http://m.cook.miznet.daum.net/ranking/hitrecipe/rankingRecipeList?rankType=5",
            "http://m.cook.miznet.daum.net/ranking/hitrecipe/rankingRecipeList?rankType=6",
            "http://m.cook.miznet.daum.net/calendar/list?type=D",
            s"http://m.cook.miznet.daum.net/search?q=${cal.get(Calendar.DAY_OF_MONTH)}%EC%9B%94",
            "http://m.cook.miznet.daum.net/ranking/bestrecipe/rankingRecipeList?rankType=1&datekey=1"
        )
        val doc = Random.shuffle(HtmlParser.linkSequence(HtmlParser.get(Random.shuffle(links).last),"cook/recipe/read")).last
        Message(doc.title, Option(Photo(doc.imageSrc,140,101)), Option(MessageButton("레시피 보기",doc.href)))
    }
    def searchRecipe(keyword: String): Message ={
        val link =s"http://m.cook.miznet.daum.net/search?q=${URLEncoder.encode(keyword,"UTF-8")}"
        val text = HtmlParser.linkSequence(HtmlParser.get(link),"cook/recipe/read").map{doc =>
            s"${doc.title} (${doc.desc})\n-${doc.href}"
        }.mkString("\n")
        Message(text)
    }

}

case class DaumCook(title: String, link: String, thumbnail: String)