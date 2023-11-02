import sbt._

object AppDependencies {

  val playSuffix        = s"-play-28"

  val boostrapVersion   =  "7.22.0"
  val xtractVersion     =  "2.3.0"
  val hmrcMongoVersion  =  "1.3.0"
  val scalamockVersion  =  "5.2.0"
  val catsCoreVersion   =  "2.9.0"
  val flexmarkVersion   =  "0.62.2"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"           %% s"bootstrap-backend$playSuffix"  % boostrapVersion,
    "uk.gov.hmrc.mongo"     %% s"hmrc-mongo$playSuffix"         % hmrcMongoVersion,
    "com.lucidchart"        %%  "xtract"                        % xtractVersion,
    "org.typelevel"         %%  "cats-core"                     % catsCoreVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"           %% s"bootstrap-test$playSuffix"     % boostrapVersion         % Test,
    "org.scalamock"         %%  "scalamock"                     % scalamockVersion        % Test,
    "com.lucidchart"        %%  "xtract-testing"                % xtractVersion           % Test,
    "uk.gov.hmrc.mongo"     %% s"hmrc-mongo-test$playSuffix"    % hmrcMongoVersion        % Test
  )

  val overrides: Seq[ModuleID] = Seq(
    "com.google.inject"      % "guice"          % "5.1.0",
    "org.scala-lang.modules" % "scala-xml_2.13" % "2.1.0",
  )
}
