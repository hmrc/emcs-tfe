/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.config

import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig) {

  def stubUrl: String = s"${servicesConfig.baseUrl("emcs-tfe-chris-stub")}/emcs-tfe-chris-stub"
  def chrisUrl: String = servicesConfig.baseUrl("chris")
}
