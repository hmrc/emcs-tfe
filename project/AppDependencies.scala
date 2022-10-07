import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % "7.3.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % "0.73.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % "7.3.0"             % "test, it",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % "0.73.0"            % Test,
  )
}
