name := "login_system"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.specs2" %% "specs2" % "2.3.4" % "test"
)

play.Project.playScalaSettings
