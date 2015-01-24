package me.ugo.slushy.crawler

import com.netaporter.uri.Uri
import com.netaporter.uri.dsl._

import scala.util.Try
import scala.xml.XML

/**
 * Created by Ugo Bataillard on 1/24/15.
 */
trait SitemapCrawler extends Crawler { self:UrlStorage =>

  def openSitemap(url: Uri): Seq[Uri] = {
    SiteMapExplorer.exploreSite(url)
  }

  override protected def generatedUrls = {
    super.generatedUrls ++ super.startUrls.flatMap { url =>
      openSitemap(url)
    }
  }

}

object SiteMapExplorer {

  def exploreSite(uri:Uri):Seq[Uri] = {
    val siteMapUrl = uri.copy(pathParts = Nil) / "sitemap.xml"
    exploreSitemap(siteMapUrl.toString())
  }

  def exploreSitemap(url:String):Seq[Uri] = {
    val xml = XML.load(url)
    (xml \\ "loc") flatMap { elem =>
      val location = elem.text
      if (location.endsWith(".xml")) exploreSitemap(location)
      else Try(Uri.parse(location)).toOption
    }
  }
}