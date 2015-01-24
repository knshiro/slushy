package me.ugo.slushy.crawler

import scala.collection.mutable.ArrayBuffer

/**
 * Created by Ugo Bataillard on 1/24/15.
 */
trait EmailCrawler {
  this: Crawler =>

  val searchPattern = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+".r

  /**
   * Triggered every time a link is found
   */
  private var onEmailFoundCallbacks = ArrayBuffer[String => Any]()
  def onEmailFound(f: String => Any) = onEmailFoundCallbacks += f

  onReceivedPage { page =>
    searchPattern.findAllIn(page.doc.body().text) foreach { email =>
      onEmailFoundCallbacks.foreach(_(email))
    }
  }
}
