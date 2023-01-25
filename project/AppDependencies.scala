import sbt._

object AppDependencies {

  val playSuffix        = s"-play-28"

  val boostrapVersion   =  "7.12.0"
  val xtractVersion     =  "2.2.1"
  val hmrcMongoVersion  =  "0.74.0"
  val scalamockVersion  =  "5.2.0"
  val catsCoreVersion   =  "2.3.1"

  val compile = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-backend$playSuffix"  % boostrapVersion,
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo$playSuffix"         % hmrcMongoVersion,
    "com.lucidchart"    %%  "xtract"                        % xtractVersion,
    "org.typelevel"     %%  "cats-core"                     % catsCoreVersion
  )

  val test = Seq(
    "uk.gov.hmrc"       %% s"bootstrap-test$playSuffix"     % boostrapVersion   % "test, it",
    "org.scalamock"     %%  "scalamock"                     % scalamockVersion  % "test, it",
    "com.lucidchart"    %%  "xtract-testing"                % xtractVersion     % "test, it"
  )
}
