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

package uk.gov.hmrc.emcstfe.models.changeDestination

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.TraderModel

import scala.xml.{Elem, NodeSeq}

case class MovementGuaranteeModel(
                                   guarantorTypeCode: String,
                                   guarantorTrader: Option[Seq[GuarantorTraderModel]]
                                 ) extends ChangeDestinationModel {
  def toXml: Elem = <urn:MovementGuarantee>
    <urn:GuarantorTypeCode>{guarantorTypeCode}</urn:GuarantorTypeCode>
    {guarantorTrader.map(_.map(trader =>
      <urn:GuarantorTrader language="en">{trader.toXml}</urn:GuarantorTrader>
    )).getOrElse(NodeSeq.Empty)}
  </urn:MovementGuarantee>
}

object MovementGuaranteeModel {
  implicit val fmt: OFormat[MovementGuaranteeModel] = Json.format
}
