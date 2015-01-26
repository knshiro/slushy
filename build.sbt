name := "ScalaCrawler"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.5"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-value-discard",
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code"
)


resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.1",
  "com.netaporter" %% "scala-uri" % "0.4.4",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "net.databinder.dispatch" %% "dispatch-jsoup" % "0.11.2",
  "org.scalanlp" %% "breeze" % "0.8.1",
  "org.scalaz.stream" %% "scalaz-stream" % "0.6a"
)

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")
    
