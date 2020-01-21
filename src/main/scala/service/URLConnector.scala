package service

import java.net.{URL, URLConnection}

/**
 * Created by i.zhavoronkov on 21/01/2020.
 */
class URLConnector {
  def connectTo(url: String): URLConnection = new URL(url).openConnection
}

object URLConnector {
  def apply(): URLConnector = new URLConnector()
}