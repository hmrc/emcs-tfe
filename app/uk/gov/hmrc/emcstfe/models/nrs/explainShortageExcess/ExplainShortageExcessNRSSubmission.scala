/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.nrs.explainShortageExcess

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.emcstfe.models.common.{SubmitterType, TraderModel}
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.{BodyAnalysisModel, SubmitExplainShortageExcessModel}
import uk.gov.hmrc.emcstfe.models.nrs.NotableEvent.ExplainShortageOrExcessNotableEvent
import uk.gov.hmrc.emcstfe.models.nrs.{NRSSubmission, NotableEvent}

case class ExplainShortageExcessNRSSubmission(
                                               ern: String,
                                               arc: String,
                                               sequenceNumber: Int,
                                               submitterType: SubmitterType,
                                               consigneeTrader: Option[TraderModel],
                                               consignorTrader: Option[TraderModel],
                                               individualItems: Option[Seq[BodyAnalysisModel]],
                                               dateOfAnalysis: Option[String],
                                               globalExplanation: Option[String]
                                             ) extends NRSSubmission {
  override val notableEvent: NotableEvent = ExplainShortageOrExcessNotableEvent
}

object ExplainShortageExcessNRSSubmission {

  def apply(submission: SubmitExplainShortageExcessModel, ern: String): ExplainShortageExcessNRSSubmission = ExplainShortageExcessNRSSubmission(
    ern = ern,
    arc = submission.exciseMovement.arc,
    sequenceNumber = submission.exciseMovement.sequenceNumber,
    submitterType = submission.attributes.submitterType,
    consigneeTrader = submission.consigneeTrader,
    consignorTrader = submission.consignorTrader,
    individualItems = submission.bodyAnalysis,
    dateOfAnalysis = submission.analysis.map(_.dateOfAnalysis),
    globalExplanation = submission.analysis.map(_.globalExplanation)
  )

  implicit val writes: Writes[ExplainShortageExcessNRSSubmission] = Json.writes[ExplainShortageExcessNRSSubmission]
}
