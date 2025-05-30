import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "emcs-tfe"

ThisBuild / scalaVersion := "2.13.16"
ThisBuild / majorVersion := 1

lazy val microservice = (project in file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    name := appName,
    libraryDependencies ++= AppDependencies(),
    PlayKeys.playDefaultPort := 8311,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions ++= Seq(
      "-Wconf:cat=deprecation:w,cat=feature:w,cat=optimizer:w,src=target/.*:s",
    ),
    routesImport += "uk.gov.hmrc.emcstfe.models.request.GetMovementListSearchOptions",
    routesImport += "uk.gov.hmrc.emcstfe.models.request.GetDraftMovementSearchOptions",
    routesImport += "uk.gov.hmrc.emcstfe.models.request.GetDraftMovementSortField",
    routesImport += "uk.gov.hmrc.emcstfe.models.common.SortOrdering",
    routesImport += "uk.gov.hmrc.emcstfe.models.common.DestinationType",
    Runtime / unmanagedClasspath += baseDirectory.value / "resources",
    resolvers += Resolver.jcenterRepo
  )
  .settings(CodeCoverageSettings.settings *)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)
