import sbt.*

object AppDependencies {

  val playSuffix        = s"-play-30"

  val boostrapVersion   =  "8.6.0"
  val xtractVersion     =  "2.3.0"
  val hmrcMongoVersion  =  "1.7.0"
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
    "uk.gov.hmrc"           %% s"bootstrap-test$playSuffix"     % boostrapVersion         % "test, it",
    "org.scalamock"         %%  "scalamock"                     % scalamockVersion        % "test, it",
    "com.lucidchart"        %%  "xtract-testing"                % xtractVersion           % "test, it",
    "uk.gov.hmrc.mongo"     %% s"hmrc-mongo-test$playSuffix"    % hmrcMongoVersion        % "it"
  )

  val overrides: Seq[ModuleID] = Seq(
    "com.google.inject"      % "guice"          % "5.1.0",
    "org.scala-lang.modules" % "scala-xml_2.13" % "2.1.0",
  )
}
