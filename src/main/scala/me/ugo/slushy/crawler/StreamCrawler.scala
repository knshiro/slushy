package me.ugo.slushy.crawler

import me.ugo.slushy.model.HtmlPage

import scala.concurrent.ExecutionContext
import scalaz.concurrent.Task
import scalaz.stream._

/**
 * Created by Ugo Bataillard on 1/25/15.
 */
trait StreamCrawler { self:Crawler =>

  private val q = async.unboundedQueue[HtmlPage]

  onReceivedPage { page =>
    println("Enqueue page: " + page.url)
    q.enqueueOne( page ).run
  }

  def stream(implicit executionContext: ExecutionContext):Process[Task,HtmlPage] = {
    // TODO improve this ugly side affect appending
    (Process.eval(Task{crawl();null:HtmlPage}) ++ q.dequeue) drop(1)
  }

}
