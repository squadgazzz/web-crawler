package service

import java.io.{BufferedWriter, FileInputStream, FileWriter}
import java.net.{URLConnection, UnknownHostException}
import java.util.concurrent.Executors

import domain.CrawlerProperties
import org.mockito.scalatest.MockitoSugar
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.io.Source
import scala.util.Using

/**
 * Created by i.zhavoronkov on 21/01/2020.
 */
class CrawlerServiceTest extends AnyFlatSpec with Matchers with MockitoSugar {

  it should "work when valid url" in {
    Using.Manager { use =>
      val in = use(new FileInputStream("src/test/resources/config.properties"))
      val props = CrawlerProperties.fromInputStream(in)

      val threadPool = Executors.newFixedThreadPool(props.threadsCount)
      try {
        implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(threadPool)

        val urlConnector = mock[URLConnector]
        val urlConnection = mock[URLConnection]
        val yandexInputStream = new FileInputStream("src/test/resources/yandex.html")

        when(urlConnector.connectTo(any[String])).thenReturn(urlConnection)
        when(urlConnection.getInputStream).thenReturn(yandexInputStream)
        doNothing.when(urlConnection).setConnectTimeout(anyInt)
        doNothing.when(urlConnection).setReadTimeout(anyInt)

        assertResult(urlConnection)(urlConnector.connectTo("yandex.ru"))

        val writer = use(new BufferedWriter(new FileWriter(props.outputFileName)))
        val service = CrawlerService(writer, urlConnector, props)
        val fResults = service.runDataProcess(props.inputFileName)

        Await.ready(fResults, Duration.Inf)

        val resultSource = use(Source.fromFile("src/test/resources/result.csv"))

        val resultLines = resultSource.getLines().toList
        assertResult(1)(resultLines.size)
        assertResult("yandex.ru;Яндекс;main page;ya, yandex, hi;")(resultLines.head)
      } finally {
        threadPool.shutdown()
      }
    }
  }

  it should "work when non valid url" in {
    Using.Manager { use =>
      val in = use(new FileInputStream("src/test/resources/config.properties"))
      val props = CrawlerProperties.fromInputStream(in)

      val threadPool = Executors.newFixedThreadPool(props.threadsCount)
      try {
        implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(threadPool)

        val urlConnector = mock[URLConnector]
        val urlConnection = mock[URLConnection]

        when(urlConnector.connectTo(any[String])).thenReturn(urlConnection)
        doThrow(new UnknownHostException("my exception message")).when(urlConnection).getInputStream
        doNothing.when(urlConnection).setConnectTimeout(anyInt)
        doNothing.when(urlConnection).setReadTimeout(anyInt)

        assertResult(urlConnection)(urlConnector.connectTo("yandex.ru"))

        val writer = use(new BufferedWriter(new FileWriter(props.outputFileName)))
        val service = CrawlerService(writer, urlConnector, props)
        val fResults = service.runDataProcess(props.inputFileName)

        Await.ready(fResults, Duration.Inf)

        val resultSource = use(Source.fromFile("src/test/resources/result.csv"))

        val resultLines = resultSource.getLines().toList
        assertResult(1)(resultLines.size)
        assertResult("yandex.ru;;;;my exception message")(resultLines.head)
      } finally {
        threadPool.shutdown()
      }
    }
  }
}
