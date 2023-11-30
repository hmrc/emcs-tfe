import uk.gov.hmrc.DefaultBuildSettings.{addTestReportOption, integrationTestSettings}
import scoverage.ScoverageKeys

lazy val ItTest = config("it") extend Test

lazy val microservice = Project("emcs-tfe", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    majorVersion        := 0,
    scalaVersion        := "2.13.8",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    dependencyOverrides ++= AppDependencies.overrides,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    routesImport += "uk.gov.hmrc.emcstfe.models.request.GetMovementListSearchOptions",
    ScoverageKeys.coverageMinimumStmtTotal := 95,
)
  .configs(ItTest)
  .settings(inConfig(ItTest)(Defaults.itSettings ++ headerSettings(ItTest) ++ automateHeaderSettings(ItTest)): _*)
  .settings(
    ItTest / fork := true,
    ItTest / unmanagedSourceDirectories := Seq((ItTest / baseDirectory).value / "it"),
    ItTest / unmanagedClasspath += baseDirectory.value / "resources",
    Runtime / unmanagedClasspath += baseDirectory.value / "resources",
    ItTest / javaOptions += "-Dlogger.resource=logback-test.xml",
    ItTest / parallelExecution := false,
    addTestReportOption(ItTest, "int-test-reports")
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(PlayKeys.playDefaultPort := 8311)

  // https://github.com/hmrc/bootstrap-play#mdc-logging
  //.enablePlugins(PlayNettyServer)
  //.settings(PlayKeys.devSettings += "play.server.provider" -> "play.core.server.NettyServerProvider")