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

package uk.gov.hmrc.emcstfe.models.explainDelay

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.SubmitterType
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class SubmitExplainDelayModel(arc: String,
                                   sequenceNumber: Int,
                                   submitterType: SubmitterType,
                                   delayType: DelayType,
                                   delayReasonType: DelayReasonType,
                                   additionalInformation: Option[String]) extends XmlWriterUtils {

  def toXml(implicit request: UserRequest[_]): Elem =
    <urn:ExplanationOnDelayForDelivery>
      <urn:Attributes>
        <urn:SubmitterIdentification>{request.ern}</urn:SubmitterIdentification>
        <urn:SubmitterType>{submitterType.toString}</urn:SubmitterType>
        <urn:ExplanationCode>{delayReasonType.toString}</urn:ExplanationCode>
        {additionalInformation.mapNodeSeq(x => <urn:ComplementaryInformation language="en">{x}</urn:ComplementaryInformation>)}
        <urn:MessageRole>{delayType.toString}</urn:MessageRole>
      </urn:Attributes>
      <urn:ExciseMovement>
        <urn:AdministrativeReferenceCode>{arc}</urn:AdministrativeReferenceCode>
        <urn:SequenceNumber>{sequenceNumber}</urn:SequenceNumber>
      </urn:ExciseMovement>
    </urn:ExplanationOnDelayForDelivery>
}

object SubmitExplainDelayModel {
  implicit val fmt: Format[SubmitExplainDelayModel] = Json.format
}
