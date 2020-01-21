package domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
 * Created by i.zhavoronkov on 21/01/2020.
 */
class CSVRowTest extends AnyFlatSpec with Matchers {

  private val csvSeparator = ";"

  it should "parse html when html is valid" in {
    val testHtml =
      """
        |<!DOCTYPE html>
        |<html class="i-ua_js_no i-ua_css_standart i-ua_browser_chrome i-ua_browser_desktop font_loaded i-ua_platform_other" lang="ru">
        |   <head xmlns:og="http://ogp.me/ns#">
        |      <meta http-equiv=Content-Type content="text/html""" + csvSeparator +
        """charset=UTF-8">
          |      <meta http-equiv="X-UA-Compatible" content="IE=edge">
          |      <title>Яндекс</title>
          |      <meta name="keywords" content="ya, yandex, hi">
          |      <meta name="description" content="main page">
          |""".stripMargin

    val row = CSVRow.fromHTML(testHtml, "ya.ru", csvSeparator)

    assertResult(CSVRow("ya.ru", "Яндекс", "main page", "ya, yandex, hi"))(row)
    assertResult("ya.ru;Яндекс;main page;ya, yandex, hi;")(row.toCSVString(csvSeparator))
  }

  it should "parse html when html is not valid" in {
    val testHtml =
      """
        | class="i-ua_js_no i-ua_css_standart i-ua_browser_chrome i-ua_browser_desktop font_loaded i-ua_platform_other" lang="ru">
        |   <head xmlns:og="http://ogp.me/ns#">
        |      <meta http-equiv=Content-Type content="text/html""" + csvSeparator +
        """charset=UTF-8">
          |      <meta http-equiv="X-UA-Compatible" content="IE=edge">
          |      <title>Яндекс</title>
          |      meta name="keywords" content="ya, yandex, hi">
          |      <meta ="description" content="main page">
          |""".stripMargin

    val row = CSVRow.fromHTML(testHtml, "ya.ru", csvSeparator)

    assertResult(CSVRow("ya.ru", "Яндекс"))(row)
    assertResult("ya.ru;Яндекс;;;")(row.toCSVString(csvSeparator))
  }
}
