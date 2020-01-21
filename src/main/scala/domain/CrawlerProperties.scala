package domain

import java.io.InputStream
import java.util.Properties

/**
 * Created by i.zhavoronkov on 21/01/2020.
 */
case class CrawlerProperties(inputFileName: String,
                             outputFileName: String,
                             threadsCount: Int,
                             batchSize: Int,
                             connectionTimeout: Int,
                             separator: String,
                             pageSizeLimit: Int)

object CrawlerProperties {
  def fromInputStream(in: InputStream): CrawlerProperties = {
    val prop = new Properties()
    prop.load(in)
    val inputFileName = prop.getProperty("input.file")
    val outputFileName = prop.getProperty("output.file")
    val separator = prop.getProperty("cvs.separator")
    val threadsCount = prop.getProperty("threads.count").toInt
    val batchSize = prop.getProperty("websites.batch.size").toInt
    val downloadLimit = prop.getProperty("web.page.download.limit").toInt
    val connectionTimeOut = prop.getProperty("connection.timeout.ms").toInt

    CrawlerProperties(inputFileName, outputFileName, threadsCount, batchSize, connectionTimeOut, separator, downloadLimit)
  }
}
