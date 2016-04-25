package utils
import java.net.{MalformedURLException, URL}

import models.{MessageButton, Photo, Message}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

//import org.jsoup.nodes.Element

import scala.collection.JavaConversions._
import scala.util.control.Exception._

sealed case class Link(title: String, href: String, imageSrc:String, desc:String) {
    override def toString(): String ={
        s"title : $title, href : $href, imageSrc : $imageSrc, desc : $desc"
    }
    def toMessage:Message ={
        Message(s"[$title]\n$desc",Option(Photo(imageSrc,300,250)), Option(MessageButton("자세히보기",href)))
    }
}


object HtmlParser {

    type JDoc = org.jsoup.nodes.Document

    def get(url: String): JDoc = Jsoup.connect(url).get()

    def titleText(doc: JDoc): String = doc.select("title").text

    def bodyText(doc: JDoc): String = doc.select("body").text

    def linkSequence(doc: JDoc, containStr : String): Seq[Link] = {
        val links = doc.select(s"a[href*=$containStr]").iterator.toList
        links.map { l => Link(l.text, l.attr("href"), l.select("img[src]").attr("src"), l.select("[class*=desc]").text) }
    }


    def safeURL(url: String): Option[String] = {
        val result = catching(classOf[MalformedURLException]) opt new URL(url)
        result match {
            case Some(v) => Some(v.toString)
            case None => None
        }
    }
}