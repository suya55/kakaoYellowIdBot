package models.cook

import java.net.URLEncoder
import java.util.Calendar


import models.{Photo, MessageButton, Message}
import play.api.Logger
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
            s"http://m.cook.miznet.daum.net/search?q=${cal.get(Calendar.MONTH)}%EC%9B%94",
            "http://m.cook.miznet.daum.net/ranking/bestrecipe/rankingRecipeList?rankType=1&datekey=1"
        )
        val url = Random.shuffle(links).last
        val doc = HtmlParser.get(url)
        val list = HtmlParser.linkSequence(doc,"cook/recipe/read")
        Logger.info(s"url : ${url} , result size : ${list.size}")
        val idx:Int = Random.shuffle(0 to list.size-1).toSeq.last
        val images = doc.body.toString.split("\n").filter(_.indexOf("Rank.addUrl") > 0)
        val selected = list(idx)
        val src = images.size match {
            case 0 => selected.imageSrc
            case _ => images(idx).replace("\tRank.addUrl(\"\t\t","").replace("\");","")
        }
        val reg = "^[0-9]+".r
        Message(reg.replaceFirstIn(selected.title,"").trim, Option(Photo(src,140,101)), Option(MessageButton("레시피 보기",selected.href)))
    }
    def searchRecipe(keyword: String): Message ={
        val link =s"http://m.cook.miznet.daum.net/search?q=${URLEncoder.encode(keyword,"UTF-8")}"
        val text:String = HtmlParser.linkSequence(HtmlParser.get(link),"cook/recipe/read").map{doc =>
            s"${doc.title} (${doc.desc})\n-${doc.href}"
        }.mkString("\n")
        Message(text)
    }

}

case class DaumCook(title: String, link: String, thumbnail: String)