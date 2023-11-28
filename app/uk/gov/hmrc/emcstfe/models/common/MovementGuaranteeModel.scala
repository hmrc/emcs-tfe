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

package uk.gov.hmrc.emcstfe.models.common

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.utils.{XmlReaderUtils, XmlWriterUtils}

import scala.xml.Elem

case class MovementGuaranteeModel(
    guarantorTypeCode: GuarantorType,
    guarantorTrader: Option[Seq[TraderModel]]
) extends XmlBaseModel
    with XmlWriterUtils {

  def toXml(implicit request: UserRequest[_]): Elem = <urn:MovementGuarantee>
    <urn:GuarantorTypeCode>
      {guarantorTypeCode.toString}
    </urn:GuarantorTypeCode>
    {guarantorTrader.mapNodeSeq(_.map(trader => <urn:GuarantorTrader language="en">{trader.toXml(GuarantorTrader)}</urn:GuarantorTrader>))}
  </urn:MovementGuarantee>

}

object MovementGuaranteeModel extends XmlReaderUtils {

  implicit val xmlReads: XmlReader[MovementGuaranteeModel] = (
    (__ \\ "GuarantorTypeCode").read[GuarantorType](GuarantorType.xmlReads("MovementGuarantee/GuarantorTypeCode")(GuarantorType.enumerable)),
    (__ \\ "GuarantorTrader").read[Seq[TraderModel]](strictReadSeq(TraderModel.xmlReads(GuarantorTrader))).seqToOptionSeq
  ).mapN(MovementGuaranteeModel.apply)

  implicit val fmt: OFormat[MovementGuaranteeModel] = Json.format
}
