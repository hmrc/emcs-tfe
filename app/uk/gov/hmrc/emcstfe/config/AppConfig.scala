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

package uk.gov.hmrc.emcstfe.config

import play.api.Configuration
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{FeatureSwitching, UseChrisStub}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  override lazy val config: AppConfig = this

  def chrisUrl: String = servicesConfig.baseUrl("chris")
  def chrisStubUrl: String = servicesConfig.baseUrl("chris-stub")

  def createMovementUserAnswersTTL(): Duration = Duration(configuration.get[String]("mongodb.createMovementUserAnswers.TTL"))
  def createMovementUserAnswersReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.createMovementUserAnswers.replaceIndexes")

  def reportReceiptUserAnswersTTL(): Duration = Duration(configuration.get[String]("mongodb.reportReceiptUserAnswers.TTL"))
  def reportReceiptUserAnswersReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.reportReceiptUserAnswers.replaceIndexes")

  def explainDelayUserAnswersTTL(): Duration = Duration(configuration.get[String]("mongodb.explainDelayUserAnswers.TTL"))
  def explainDelayUserAnswersReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.explainDelayUserAnswers.replaceIndexes")

  def explainShortageOrExcessUserAnswersTTL(): Duration = Duration(configuration.get[String]("mongodb.explainShortageOrExcessUserAnswers.TTL"))
  def explainShortageOrExcessUserAnswersReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.explainShortageOrExcessUserAnswers.replaceIndexes")

  def cancelAMovementUserAnswersTTL(): Duration = Duration(configuration.get[String]("mongodb.cancelAMovementUserAnswers.TTL"))
  def cancelAMovementUserAnswersReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.cancelAMovementUserAnswers.replaceIndexes")

  def changeDestinationUserAnswersTTL(): Duration = Duration(configuration.get[String]("mongodb.changeDestinationUserAnswers.TTL"))
  def changeDestinationUserAnswersReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.changeDestinationUserAnswers.replaceIndexes")

  def alertRejectionUserAnswersTTL(): Duration = Duration(configuration.get[String]("mongodb.alertRejectionUserAnswers.TTL"))
  def alertRejectionUserAnswersReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.alertRejectionUserAnswers.replaceIndexes")

  def getMovementTTL(): Duration = Duration(configuration.get[String]("mongodb.getMovement.TTL"))
  def getMovementReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.getMovement.replaceIndexes")

  // user-allow-list config
  private def userAllowListService: String = servicesConfig.baseUrl("user-allow-list")
  def userAllowListBaseUrl: String = s"$userAllowListService/user-allow-list"
  def allowListEnabled: Boolean = configuration.get[Boolean]("features.allowListEnabled")
  def internalAuthToken: String = configuration.get[String]("internal-auth.token")

  def clearDownDuplicatesOnStartup: Boolean = configuration.get[Boolean]("features.clearDownDuplicatesOnStartup")


  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  def chrisBaseUrl: String = if(isEnabled(UseChrisStub)) chrisStubUrl else chrisUrl

  def urlEMCSApplicationService(): String =
    chrisBaseUrl + "/ChRISOSB/EMCS/EMCSApplicationService/2"

  def urlSubmitCreateMovement(): String =
    chrisBaseUrl + "/ChRIS/EMCS/SubmitDraftMovementPortal/3"

  def urlSubmitReportOfReceipt(): String =
    chrisBaseUrl + "/ChRIS/EMCS/SubmitReportofReceiptPortal/4"

  def urlSubmitExplainDelay(): String =
    chrisBaseUrl + "/ChRIS/EMCS/SubmitExplainDelayToDeliveryPortal/4"

  def urlSubmitChangeDestination(): String =
    chrisBaseUrl + "/ChRIS/EMCS/SubmitChangeOfDestinationPortal/3"

  def urlSubmitExplainShortageExcess(): String =
    chrisBaseUrl + "/ChRIS/EMCS/SubmitReasonForShortagePortal/2"

  def urlSubmitAlertOrRejection(): String =
    chrisBaseUrl + "/ChRIS/EMCS/SubmitAlertOrRejectionMovementPortal/2"

  def urlCancellationOfMovement(): String =
    chrisBaseUrl + "/ChRIS/EMCS/SubmitCancellationPortal/3"
}
