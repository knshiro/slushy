package me.ugo.slushy.crawler

import com.netaporter.uri.Uri
import me.ugo.slushy.model.HtmlPage
import me.ugo.slushy.scraper.{FixedNumberConcurrentRequestScraper, Frequency, ThrottledHttpScraper, Scraper}
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Ugo Bataillard on 1/24/15.
 */
trait Crawler {
  self: UrlStorage =>

  def scraper: Scraper

  def startUrls: Seq[Uri]

  lazy val allowedDomains: Set[String] = {
    startUrls.flatMap(_.host).toSet
  }

  /**
   * Triggered just before the spider starts searching
   *
   * Additional start urls can be returned by the method
   */
  private var onStartCallbacks = ArrayBuffer[() => Any]()
  protected def onStart(body: => Any) = {
    val el = (() => body)
    onStartCallbacks += el
  }

  /**
   * Triggered when a page has been downloaded
   */
  private var onReceivedPageCallbacks = ArrayBuffer[HtmlPage => Any]()
  protected def onReceivedPage(f: HtmlPage => Any) = onReceivedPageCallbacks += f


  /**
   * Triggered every time a link is found
   */
  private var onLinkFoundCallbacks = ArrayBuffer[Uri => Any]()
  protected def onLinkFound(f: Uri => Any) = onLinkFoundCallbacks += f


  def isToBeCraled(url: Uri): Boolean = {
    url.host.exists(allowedDomains.contains(_)) && !isCrawled(url)
  }

  /**
   * Scrape a single page
   *
   * @param url url of page to scrape
   */
  def scrapePage(url: Uri)(implicit ec: ExecutionContext): Future[Unit] = {
    if (isToBeCraled(url)) {
      crawled(url)
      scraper.scrape(url) flatMap { page =>
        onReceivedPageCallbacks.foreach(_(page))
        Future.fold{page.links.map { link =>
          onLinkFoundCallbacks.foreach(_(link))
          scrapePage(link)
        }}(()){(_,_) =>()}
      }
    } else Future.successful()
  }

  /**
   * Start running the spider
   */
  def crawl()(implicit ec: ExecutionContext): Future[Unit] = {
    Future.fold(startUrls map scrapePage)(()){(_,_)=>()}
  }

}

object Crawler {

  def apply(url:Uri, concurrentRequests: Int)(f:HtmlPage => Any)(implicit ec:ExecutionContext):Crawler = apply(Seq(url),concurrentRequests)(f)
  def apply(urls:Seq[Uri], concurrentRequests:Int)(f:HtmlPage => Any)(implicit ec:ExecutionContext):Crawler = new Crawler with UrlStorageBloomFilter{
    override val scraper: Scraper = new FixedNumberConcurrentRequestScraper(concurrentRequests,ec)
    override def startUrls: Seq[Uri] = urls
    onReceivedPage(f)
  }

  def apply(url:Uri, frequency: Frequency)(f:HtmlPage => Any)(implicit ec:ExecutionContext):Crawler = apply(Seq(url),frequency)(f)
  def apply(urls:Seq[Uri], frequency: Frequency)(f:HtmlPage => Any)(implicit ec:ExecutionContext):Crawler = new Crawler with UrlStorageBloomFilter{
    override val scraper: Scraper = new ThrottledHttpScraper(frequency,ec)
    override def startUrls: Seq[Uri] = urls
    onReceivedPage(f)
  }

}

