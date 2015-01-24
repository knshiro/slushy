package me.ugo.slushy

import java.util.concurrent.TimeUnit

import com.netaporter.uri.Uri
import me.ugo.slushy.scraper.Frequency
import org.specs2.mutable._
import crawler._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, FiniteDuration}

/**
 * Created by Ugo Bataillard on 1/24/15.
 */
class CrawlerTest extends Specification {

  "The main crawler" should {
    "crawl" in {
      import scala.concurrent.ExecutionContext.Implicits._

      val crawler =
        Crawler(Uri.parse("http://employment.en-japan.com/"), 10) { page =>
          println("Parsed: " + page.url)
        }
      Await.ready(crawler.crawl(), Duration.Inf)
      "Hello world" must endWith("world")
    }
  }


}
