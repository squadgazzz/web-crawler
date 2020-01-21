package service

import java.io.BufferedWriter

import domain.{CSVRow, CrawlerProperties}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Using

/**
 * Created by i.zhavoronkov on 20/01/2020.
 */
case class CrawlerService(private val bufferedWriter: BufferedWriter,
                          private val urlConnector: URLConnector,
                          private val props: CrawlerProperties)(implicit val executionContext: ExecutionContext) {

  def runDataProcess(inputFileName: String): Future[List[Unit]] =
    Using.resource(Source.fromResource(inputFileName)) {
      resource => {
        Future.traverse(resource.getLines().grouped(props.batchSize).toList) {
          stringsPart =>
            Future(convertToCSV(stringsPart))
              .flatMap(csvLines => Future(writeToFile(csvLines)))
        }
      }
    }

  private def convertToCSV(part: Seq[String]): Seq[CSVRow] =
    part.filter(_.nonEmpty).map { url =>
      try {
        val connection = urlConnector.connectTo(s"https://$url")
        connection.setConnectTimeout(props.connectionTimeout)
        connection.setReadTimeout(props.connectionTimeout)
        Using.resource(Source.fromInputStream(connection.getInputStream)) {
          resource =>
            val str = resource.take(props.pageSizeLimit).mkString
            CSVRow.fromHTML(str, url, props.separator)
        }
      } catch {
        case e: Exception => CSVRow(url, error = e.toString)
      }
    }

  private def writeToFile(part: Seq[CSVRow]): Unit =
    bufferedWriter.write(part.map(_.toCSVString(props.separator)).mkString("\r\n"))
}
