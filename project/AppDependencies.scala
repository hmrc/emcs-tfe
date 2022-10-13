import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "7.3.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.73.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % "7.3.0" % "test, it",
    "org.scalamock" %% "scalamock" % "5.2.0" % "test, it",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-28" % "0.73.0" % Test,
    "org.jsoup" % "jsoup" % "1.15.3" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "com.github.tomakehurst" % "wiremock-jre8" % "2.33.2" % "it"
  )
}
