package me.ugo.slushy.crawler

import breeze.util.BloomFilter
import com.netaporter.uri.Uri

import scala.collection.mutable.{Set => MSet}

/**
 * Created by Ugo Bataillard on 1/24/15.
 */

trait UrlStorage {

  def isCrawled(url:Uri):Boolean
  def crawled(url:Uri):Unit

}

trait UrlStorageSet extends UrlStorage {

  private val urls = MSet[String]()

  override def isCrawled(url:Uri):Boolean = urls.contains(url.toString)
  override def crawled(url: Uri): Unit = { synchronized( urls += url.toString); () }
}

trait UrlStorageBloomFilter extends UrlStorage {

  val bf = BloomFilter.optimallySized[String](1000000, 0.01)
  override def isCrawled(url:Uri):Boolean = bf.contains(url.toString)
  override def crawled(url: Uri): Unit = { synchronized( bf += url.toString); () }
}

