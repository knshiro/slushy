package me.ugo.slushy
package scraper

import com.netaporter.uri.Uri
import me.ugo.slushy.model.HtmlPage
import org.jsoup.Jsoup

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
 * Created by Ugo Bataillard on 1/24/15.
 */
class JsoupScraper extends Scraper {

  def scrape (pageUrl: Uri)(implicit ec:ExecutionContext) = Future {
    val doc = Jsoup
      .connect(pageUrl.toString)
      .userAgent(userAgent)
      .followRedirects(true)
      .timeout(0)
      .get
    HtmlPage(pageUrl, doc)
  }

}
