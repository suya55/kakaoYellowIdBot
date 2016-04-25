package models.flower

import models.Message
import org.jsoup.nodes.Element
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.ws.WS

import play.api.Play.current
import utils.{Link, HtmlParser}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

case class Flower(nameKr: String, nameSc: String, linkDaum: String, prob: Float) {
    val percent = (prob * 100).toInt

    def toMessage: String = {
        s"""|
            |$nameKr($nameSc) - $percent%
            |자세히보기 : $linkDaum
         """.stripMargin
    }
}

object Flower {
    def search(content: String): Message = {
        val url = s"http://100.daum.net/search/entry?q=$content"
        val doc = HtmlParser.get(url)

        doc.getElementsByClass("fst").first() match {
            case element: Element =>
                val imgSrc = element.getElementsByTag("img").attr("src")
                val link = s"http://100.daum.net${element.getElementsByTag("a").first().attr("href")}"
                val title = element.getElementsByTag("strong").first().text()
                val desc = element.getElementsByClass("desc_register").text()
                Link(title,link,imgSrc,desc).toMessage
            case _ =>
                Message("찾으시는 꽃명에 대한 결과가 없습니다.")
        }
    }

    implicit val photoReads: Reads[Flower] = (
      (JsPath \ "name_kr").read[String] and
        (JsPath \ "name_sc").read[String] and
        (JsPath \ "link_daum").read[String] and
        (JsPath \ "prob").read[Float]
      ) (Flower.apply _ )

    def searchImg(url: String):String = {
        val params = Map("image_url" -> Seq(url),"ref" -> Seq("yellowid_bot"),"top_k" -> Seq("2"))
        Await.result(WS.url("http://daisy.kakao.com/classify").post(params).map {
            res =>
                (res.json \ "result").validate[List[Flower]].fold(
                    errors => "시스템 오류",
                    flowers => {
                        if(flowers.isEmpty) {
                            "꽃 사진을 올려주세요.\n꽃 사진인데 인식이 안된다 싶으면 좀 가까이서 화면 가운데로 다시 찍어서 올려주세요.\n잘 분석하도록 노력해볼게요."
                        }else{
                            flowers.map { f =>
                                f.toMessage
                            }.mkString("")
                        }
                    }
                )
        }, Duration.Inf)
    }
}
