import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val boostrapVersion = "7.12.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % boostrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.74.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % boostrapVersion % "test, it",
    "org.scalamock" %% "scalamock" % "5.2.0" % "test, it"
  )
}
