# Slushy a web crawler written in Scala

## Get started

Clone and publish the project locally
```
git clone git@github.com:knshiro/slushy.git
sbt publish-local
```

Add dependency to build.sbt
```
libraryDependencies ++= "me.ugo" %% "slushy" % "0.0.1-SNAPSHOT"
```

Use it!
```scala
import me.ugo.slushy.crawler._
import com.netaporter.uri.dsl._

val crawler = Crawler("http://employment.en-japan.com/", 10) { page =>
  println("Parsed: " + page.url)
}

Await.ready(crawler.crawl(), Duration.Inf)
```

## Go further
```scala
import me.ugo.slushy.crawler._
import me.ugo.slushy.scraper._
import com.netaporter.uri.dsl._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

val crawler = new Crawler with UrlStorageBloomFilter with SitemapCrawler with EmailCrawler {

  override def startUrls: Seq[Uri] = List("http://employment.en-japan.com/")
  override val scraper: Scraper = new ThrottledHttpScraper(Frequency(3,100.millis))

  onStart {
    println("Crawling started! ")
  }

  onLinkFound { link =>
    println("Found link: " + link)
  }
  
  onReceivedPage { page =>
    println("Received page: " + page.url)
    println(page.doc.body().text)
  }

  onEmailFound { email =>
    println("Found email:" + email)
  }

  onReceivedPage { page =>
    println("I'm not just going to print the body of this page")
    println("The title is: " + page.doc.title())
  }

}
Await.ready(crawler.crawl(), Duration.Inf)
}
```
