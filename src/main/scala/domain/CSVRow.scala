package domain

import org.jsoup.Jsoup

import scala.jdk.CollectionConverters.IteratorHasAsScala

/**
 * Created by i.zhavoronkov on 20/01/2020.
 */
case class CSVRow(url: String, title: String = "", description: String = "", keywords: String = "", error: String = "") {
  def toCSVString(separator: String): String = List(url, title, description, keywords, error).mkString(separator)
}

object CSVRow {
  def fromHTML(html: String, url: String, separator: String): CSVRow = {
    val document = Jsoup.parse(html)
    val title = document.title
    val nameContentMap = document.getElementsByTag("meta").iterator().asScala
      .map(e => (e.attr("name"), e.attr("content"))).toMap
    val keywords = nameContentMap.getOrElse("keywords", "")
    val description = nameContentMap.getOrElse("description", "")
    val attributes = Seq(url, title, description, keywords)
      .map(_.replace("\"", "\"\"").replace(separator, ","))

    attributes match {
      case Seq(url, title, description, keywords) => CSVRow(url, title, description, keywords)
    }
  }
}