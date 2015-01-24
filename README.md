# Slushy a web crawler written in Scala

# Get started

```
git clone git@github.com:knshiro/slushy.git
sbt publish-local
```

```scala
import me.ugo.slushy.Crawler
import com.netaporter.uri.dsl._

val crawler = Crawler("http://employment.en-japan.com/", 10) { page =>
  println("Parsed: " + page.url)
}

Await.ready(crawler.crawl(), Duration.Inf)
```

