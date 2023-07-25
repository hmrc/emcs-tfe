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

package uk.gov.hmrc.emcstfe.models.alertOrRejection

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel

import scala.xml.{Elem, NodeSeq}

case class AlertOrRejectionReasonModel(reason: AlertOrRejectionReasonType,
                                       additionalInformation: Option[String]) extends XmlBaseModel {
  def toXml: Elem = <urn:AlertOrRejectionOfEadEsadReason>
    <urn:AlertOrRejectionOfMovementReasonCode>{reason}</urn:AlertOrRejectionOfMovementReasonCode>
    {additionalInformation.map(info => <urn:ComplementaryInformation language="en">{info}</urn:ComplementaryInformation>).getOrElse(NodeSeq.Empty)}
  </urn:AlertOrRejectionOfEadEsadReason>
}

object AlertOrRejectionReasonModel {
  implicit val fmt: OFormat[AlertOrRejectionReasonModel] = Json.format
}