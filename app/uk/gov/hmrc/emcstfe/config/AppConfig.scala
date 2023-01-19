/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.config

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig :ServicesConfig, configuration: Configuration) {

  def chrisUrl: String = servicesConfig.baseUrl("chris")
  def chrisHeaders: Seq[String] = configuration.get[Seq[String]]("microservice.services.chris.environmentHeaders")
}
