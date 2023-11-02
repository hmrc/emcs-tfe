import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption


ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.8"

lazy val microservice = Project("emcs-tfe", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    dependencyOverrides ++= AppDependencies.overrides,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    routesImport += "uk.gov.hmrc.emcstfe.models.request.GetMovementListSearchOptions",
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    resolvers += Resolver.jcenterRepo,
    CodeCoverageSettings.settings,
    PlayKeys.playDefaultPort := 8311
  )

lazy val it = (project in file("it"))
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(
    DefaultBuildSettings.itSettings,
    Test / fork := true,
    addTestReportOption(Test, "int-test-reports"),
    Test / javaOptions += "-Dlogger.resource=logback-test.xml",
    Test / parallelExecution := false
  )

