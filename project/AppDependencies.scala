import sbt._

object AppDependencies {

  val playSuffix        = s"-play-28"

  val boostrapVersion   =  "7.20.0"
  val xtractVersion     =  "2.2.1"
  val hmrcMongoVersion  =  "0.74.0"
  val scalamockVersion  =  "5.2.0"
  val catsCoreVersion   =  "2.3.1"
  val scalatestVersion  =  "3.2.15"
  val flexmarkVersion   =  "0.62.2"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"           %% s"bootstrap-backend$playSuffix"  % boostrapVersion,
    "uk.gov.hmrc.mongo"     %% s"hmrc-mongo$playSuffix"         % hmrcMongoVersion,
    "com.lucidchart"        %%  "xtract"                        % xtractVersion,
    "org.typelevel"         %%  "cats-core"                     % catsCoreVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"           %% s"bootstrap-test$playSuffix"     % boostrapVersion         % "test, it",
    "org.scalamock"         %%  "scalamock"                     % scalamockVersion        % "test, it",
    "com.lucidchart"        %%  "xtract-testing"                % xtractVersion           % "test, it",
    "org.scalatest"         %%  "scalatest"                     % scalatestVersion        % "test, it",
    "org.scalatestplus"     %%  "mockito-4-6"                   % s"$scalatestVersion.0"  % "test",
    "com.vladsch.flexmark"  %   "flexmark-all"                  % flexmarkVersion         % "test",
    "uk.gov.hmrc.mongo"     %% s"hmrc-mongo-test$playSuffix"    % hmrcMongoVersion        % "it"
  )

  val overrides: Seq[ModuleID] = Seq(
    "com.google.inject" %   "guice" % "5.1.0"
  )
}
