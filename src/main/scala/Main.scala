import java.io.{BufferedWriter, FileInputStream, FileWriter}
import java.util.concurrent.Executors

import domain.CrawlerProperties
import service.{CrawlerService, URLConnector}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor}
import scala.util.Using

object Main extends App {

  Using.Manager { use =>
    val in = use(new FileInputStream("config.properties"))
    val props = CrawlerProperties.fromInputStream(in)

    val threadPool = Executors.newFixedThreadPool(props.threadsCount)
    try {
      implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(threadPool)

      val urlConnector = URLConnector()
      val writer = use(new BufferedWriter(new FileWriter(props.outputFileName)))
      val service = CrawlerService(writer, urlConnector, props)
      val s = System.currentTimeMillis()
      val fResults = service.runDataProcess(props.inputFileName)

      Await.ready(fResults, Duration.Inf)

      println(System.currentTimeMillis() - s)
    } finally {
      threadPool.shutdown()
    }
  }
}
