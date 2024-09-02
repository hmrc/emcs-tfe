/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.emcstfe.mocks.config

import org.scalamock.handlers.{CallHandler, CallHandler0, CallHandler1}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.featureswitch.core.models.FeatureSwitch

trait MockAppConfig extends MockFactory {
  lazy val mockAppConfig: AppConfig = mock[AppConfig]

  object MockedAppConfig {
    def downstreamStubUrl: CallHandler[String] = ((() => mockAppConfig.downstreamStubUrl): () => String).expects()

    def isEnabled(featureName: String): CallHandler1[String, Boolean] = {
      (mockAppConfig.getFeatureSwitchValue(_: String)).expects(featureName)
    }

    def getFeatureSwitchValue(feature: FeatureSwitch): CallHandler1[String, Boolean] = {
      val featureSwitchName = feature.configName
      (mockAppConfig.getFeatureSwitchValue(_: String)).expects(featureSwitchName)
    }

    def publicBetaTrafficPercentageForService(serviceName: String): CallHandler1[String, Option[Int]] = (mockAppConfig.publicBetaTrafficPercentageForService(_: String)).expects(serviceName)
    def internalAuthToken: CallHandler0[String] = ((() => mockAppConfig.internalAuthToken()): () => String).expects()
    def userAllowListBaseUrl: CallHandler0[String] = ((() => mockAppConfig.userAllowListBaseUrl()): () => String).expects()

    def listOfErnsToExcludeFromPublicBeta: CallHandler0[Seq[String]] = (() => mockAppConfig.listOfErnsToExcludeFromPublicBeta).expects()
  }
}