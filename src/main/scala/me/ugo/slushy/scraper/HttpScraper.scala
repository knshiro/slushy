package me.ugo.slushy
package scraper

import java.util.concurrent.{TimeUnit, Executors}
import java.util.concurrent.atomic.AtomicInteger

import breeze.numerics.log
import model.HtmlPage

import com.netaporter.uri.Uri

import scala.collection.mutable.Queue
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Promise, ExecutionContext, Future}
import dispatch._

import scala.util.Try


/**
 * Created by Ugo Bataillard on 1/24/15.
 */
class HttpScraper extends Scraper {

  def scrape(pageUrl: Uri)(implicit ec: ExecutionContext): Future[HtmlPage] = {
    Http(url(pageUrl.toString) > as.jsoup.Document) map (HtmlPage(pageUrl, _))
  }
}

case class Frequency(amount: Int, interval: FiniteDuration) {
  override def toString: String = s"$amount every $interval"
}


trait ConcurrentScraper extends Scraper {

  implicit val executionContext: ExecutionContext
  protected var leftRequestAllowance: Int = 0
  def maxQueueSize: Int
  protected def onScraped():Unit = ()


  private val requestsToServe = Queue[(Uri, Promise[HtmlPage])]()
  private def nextRequest(): Option[(Uri, Promise[HtmlPage])] = {
    Try(requestsToServe.dequeue()).toOption
  }

  def addRequestToServe(uri: Uri): Future[HtmlPage] = {
    val p = Promise[HtmlPage]
    synchronized(requestsToServe.enqueue((uri, p)))
    p.future
  }

  def tryToServeRequest(): Unit = {
    synchronized {
      if ((leftRequestAllowance > 0)) {
        nextRequest() match {
          case Some((u, p)) =>
            leftRequestAllowance -= 1
            doScrape(u, p)
          case None =>
        }
      }
    }
  }

  def doScrape(pageUrl: Uri, p: Promise[HtmlPage]) = {
    super.scrape(pageUrl).onComplete{onScraped();p.complete(_)}
  }

  abstract override def scrape(url: Uri)(implicit ec: ExecutionContext): Future[HtmlPage] = {
    if (maxQueueSize == Int.MaxValue || requestsToServe.size < maxQueueSize) {
      val f = addRequestToServe(url)
      tryToServeRequest()
      f
    } else {
      Future.failed(new Exception(s"Discarding request ${url}. Queue has size greater than allowed of ${maxQueueSize}"))
    }
  }

}

class FixedNumberConcurrentRequestScraper(concurrentRequests: Int, implicit val executionContext: ExecutionContext) extends HttpScraper with ConcurrentScraper {
  val maxQueueSize: Int = Int.MaxValue
  leftRequestAllowance = concurrentRequests
  override protected def onScraped() = synchronized(leftRequestAllowance += 1)

}

class ThrottledHttpScraper(frequencyThreshold: Frequency, implicit val executionContext: ExecutionContext) extends HttpScraper with ConcurrentScraper {

  val maxQueueSize: Int = Int.MaxValue

  leftRequestAllowance = frequencyThreshold.amount
  val pullUrlTask = new Runnable {
    override def run(): Unit = {
      leftRequestAllowance = frequencyThreshold.amount
      tryToServeRequest()
    }
  }
  private val scheduler = Executors.newScheduledThreadPool(1)
  scheduler.scheduleAtFixedRate(pullUrlTask, 0, frequencyThreshold.interval.toMillis, TimeUnit.MILLISECONDS)

}