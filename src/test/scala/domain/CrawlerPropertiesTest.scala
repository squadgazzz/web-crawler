package domain

import java.io.FileInputStream

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Using

/**
 * Created by i.zhavoronkov on 21/01/2020.
 */
class CrawlerPropertiesTest extends AnyFlatSpec with Matchers {

  it should "ok when valid properties" in {
    val props = Using.resource(new FileInputStream("src/test/resources/config.properties")) {
      resource => CrawlerProperties.fromInputStream(resource)
    }
    val expectedProps = CrawlerProperties("yandex.txt", "src/test/resources/result.csv", 4, 50, 500, ";", 5000)

    assertResult(expectedProps)(props)
  }

  it should "fail when non valid properties" in {
    intercept[NumberFormatException] {
      Using.resource(new FileInputStream("src/test/resources/wrongconfig.properties")) {
        resource => CrawlerProperties.fromInputStream(resource)
      }
    }
  }
}
