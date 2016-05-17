name := "scala-play"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "commons-httpclient" % "commons-httpclient" % "3.1"
)     

play.Project.playScalaSettings
