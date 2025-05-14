import sbt.*

object AppDependencies {

  val playSuffix        =  "-play-30"

  val hmrcBootstrapVersion   =  "9.11.0"
  val xtractVersion     =  "2.3.0"
  val hmrcMongoVersion  =  "2.6.0"
  val scalamockVersion  =  "5.2.0"
  val catsCoreVersion   =  "2.13.0"
  val flexmarkVersion   =  "0.64.8"

  private val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"           %% s"bootstrap-backend$playSuffix"  % hmrcBootstrapVersion,
    "uk.gov.hmrc.mongo"     %% s"hmrc-mongo$playSuffix"         % hmrcMongoVersion,
    "com.lucidchart"        %%  "xtract"                        % xtractVersion,
    "org.typelevel"         %%  "cats-core"                     % catsCoreVersion
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"           %% s"bootstrap-test$playSuffix"     % hmrcBootstrapVersion,
    "org.scalamock"         %%  "scalamock"                     % scalamockVersion,
    "com.lucidchart"        %%  "xtract-testing"                % xtractVersion,
    "uk.gov.hmrc.mongo"     %% s"hmrc-mongo-test$playSuffix"    % hmrcMongoVersion
  ).map(_ % Test)

  private val overrides: Seq[ModuleID] = Seq(
    "com.google.inject"      % "guice"          % "5.1.0",
    "org.scala-lang.modules" % "scala-xml_2.13" % "2.1.0",
  )

  val it: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test$playSuffix" % hmrcBootstrapVersion % Test
  )

  def apply(): Seq[ModuleID] = compile ++ test ++ overrides

}
