/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class AppConfig @Inject()(config: Configuration) {

  val appName: String = config.get[String]("appName")
}
