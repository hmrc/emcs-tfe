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

package uk.gov.hmrc.emcstfe.models.cancellationOfMovement

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel

import scala.xml.{Elem, NodeSeq}

case class CancellationReasonModel(reason: CancellationReasonType,
                                   complementaryInformation: Option[String]) extends XmlBaseModel {
  def toXml(implicit request: UserRequest[_]): Elem = <urn:Cancellation>
    <urn:CancellationReasonCode>{reason}</urn:CancellationReasonCode>
    {complementaryInformation.map{
      info =>
        <urn:ComplementaryInformation language="en">{info}</urn:ComplementaryInformation>
    }.getOrElse(NodeSeq.Empty)}
  </urn:Cancellation>
}

object CancellationReasonModel {
  implicit val fmt: OFormat[CancellationReasonModel] = Json.format
  private lazy val reasonCode = __ \ "CancellationReasonCode"
  private lazy val ComplementaryInformation = __ \ "ComplementaryInformation"

  implicit val xmlReads: XmlReader[CancellationReasonModel] = (
    reasonCode.read[CancellationReasonType](CancellationReasonType.xmlReads("CancellationReasonCode")(CancellationReasonType.enumerable)),
    ComplementaryInformation.read[String].optional
  ).mapN(CancellationReasonModel.apply)

}