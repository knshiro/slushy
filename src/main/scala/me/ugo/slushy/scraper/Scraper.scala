package me.ugo.slushy
package scraper

import com.netaporter.uri.Uri
import me.ugo.slushy.model.HtmlPage
import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by Ugo Bataillard on 1/24/15.
 */
trait Scraper {

  def userAgent = "Mozilla"

  def scrape (pageUrl:Uri)(implicit ec:ExecutionContext):Future[HtmlPage]

}
