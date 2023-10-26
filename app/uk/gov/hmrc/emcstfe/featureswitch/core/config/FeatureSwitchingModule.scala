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

  val switches: Seq[FeatureSwitch] = Seq(UseDownstreamStub, SendToEIS)

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[FeatureSwitchRegistry].to(this).eagerly()
    )
  }
}

case object UseDownstreamStub extends FeatureSwitch {
  override val configName: String = "features.downstreamStub"
  override val displayName: String = "enables downstream stub (for ChRIS / EIS calls)"
}

case object SendToEIS extends FeatureSwitch {
  override val configName: String = "features.sendToEIS"
  override val displayName: String = "Sends message to EIS instead of ChRIS"
}

