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
import me.ugo.slushy.Crawler
import com.netaporter.uri.dsl._

val crawler = Crawler("http://employment.en-japan.com/", 10) { page =>
  println("Parsed: " + page.url)
}

Await.ready(crawler.crawl(), Duration.Inf)
```

