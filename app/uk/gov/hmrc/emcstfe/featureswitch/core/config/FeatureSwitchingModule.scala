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

package uk.gov.hmrc.emcstfe.featureswitch.core.config

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.emcstfe.featureswitch.core.models.FeatureSwitch

import javax.inject.Singleton

@Singleton
class FeatureSwitchingModule extends Module with FeatureSwitchRegistry {

  val switches: Seq[FeatureSwitch] = Seq(
    UseDownstreamStub,
    DefaultDraftMovementCorrelationId,
    EnablePreValidateViaETDS12,
    EnableKnownFactsViaETDS18
  )

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[FeatureSwitchRegistry].to(this).eagerly()
    )
  }

}

case object UseDownstreamStub extends FeatureSwitch {
  override val configName: String  = "features.downstreamStub"
  override val displayName: String = "enables downstream stub (for EIS calls)"
}

case object DefaultDraftMovementCorrelationId extends FeatureSwitch {
  override val configName: String  = "features.defaultDraftMovementCorrelationId"
  override val displayName: String = "Defaults the draft movement correlation ID (local/staging only)"
}

case object EnablePreValidateViaETDS12 extends FeatureSwitch {
  override val configName: String  = "features.enablePreValidateViaETDS12"
  override val displayName: String = "Enables pre-validation via the ETDS12 API"
}

case object EnableKnownFactsViaETDS18 extends FeatureSwitch {
  override val configName: String  = "features.enableKnownFactsViaETDS18"
  override val displayName: String = "Enables getting known facts via the ETDS18 API"
}
