/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.mocks.config

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.config.AppConfig

trait MockAppConfig extends MockFactory {
  val mockAppConfig: AppConfig = mock[AppConfig]

  object MockedAppConfig {
    def chrisUrl: CallHandler[String] = ((() => mockAppConfig.chrisUrl): () => String).expects()
    def chrisHeaders: CallHandler[Seq[String]] = ((() => mockAppConfig.chrisHeaders): () => Seq[String]).expects()
  }

}