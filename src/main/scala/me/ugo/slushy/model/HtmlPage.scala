package me.ugo.slushy.model

import com.netaporter.uri.Uri
import org.jsoup.nodes.Document

import scala.util.{Success, Try}

/**
 * Created by Ugo Bataillard on 1/24/15.
 */
case class HtmlPage(url:Uri, doc:Document) {

  import scala.collection.JavaConverters._

  def links = {
    doc.select("a").asScala
      .map(el => Try(Uri.parse(el.select("a[href]").attr("abs:href"))))
      .collect{case Success(uri) => uri}
  }

}
