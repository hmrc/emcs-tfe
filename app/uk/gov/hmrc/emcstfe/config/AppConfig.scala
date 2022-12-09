/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.config

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) {

  def stubUrl: String = s"${servicesConfig.baseUrl("emcs-tfe-chris-stub")}/emcs-tfe-chris-stub"
  def chrisUrl: String = servicesConfig.baseUrl("chris")
  def chrisHeaders: Seq[String] = configuration.getOptional[Seq[String]]("microservice.services.chris.environmentHeaders").getOrElse(Seq())
}
