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

package uk.gov.hmrc.emcstfe.models.response

import cats.implicits.catsSyntaxTuple12Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, JourneyTime, TraderModel}
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.ConsignorTraderModel

case class GetMovementResponse(arc: String,
                               sequenceNumber: Int,
                               destinationType: DestinationType,
                               consigneeTrader: Option[TraderModel],
                               deliveryPlaceTrader: Option[TraderModel],
                               localReferenceNumber: String,
                               eadStatus: String,
                               consignorTrader: ConsignorTraderModel,
                               dateOfDispatch: String,
                               journeyTime: String,
                               items: Seq[MovementItem],
                               numberOfItems: Int
                              )

object GetMovementResponse {

  val currentMovement: XPath = __ \\ "currentMovement"
  val eadStatus: XPath = currentMovement \ "status"
  val EADESADContainer: XPath = currentMovement \ "IE801" \ "Body" \ "EADESADContainer"
  val arc: XPath = EADESADContainer \ "ExciseMovement" \ "AdministrativeReferenceCode"
  val localReferenceNumber: XPath = EADESADContainer \ "EadEsad" \ "LocalReferenceNumber"
  val dateOfDispatch: XPath = EADESADContainer \ "EadEsad" \ "DateOfDispatch"
  val journeyTime: XPath = EADESADContainer \ "HeaderEadEsad" \ "JourneyTime"
  val sequenceNumber: XPath = EADESADContainer \ "HeaderEadEsad" \ "SequenceNumber"
  val destinationTypeCode: XPath = EADESADContainer \ "HeaderEadEsad" \ "DestinationTypeCode"
  val consignorTrader: XPath = EADESADContainer \\ "ConsignorTrader"
  val consigneeTrader: XPath = EADESADContainer \\ "ConsigneeTrader"
  val deliveryPlaceTrader: XPath = EADESADContainer \\ "DeliveryPlaceTrader"
  val items: XPath = EADESADContainer \ "BodyEadEsad"
  val numberOfItems: XPath = EADESADContainer \\ "BodyEadEsad" \\ "CnCode"

  implicit val xmlReader: XmlReader[GetMovementResponse] = (
    arc.read[String],
    sequenceNumber.read[Int],
    destinationTypeCode.read[DestinationType](DestinationType.xmlReads(DestinationType.enumerable)),
    consigneeTrader.read[Option[TraderModel]].map(model => if(model.exists(_.isEmpty)) None else model),
    deliveryPlaceTrader.read[Option[TraderModel]].map(model => if(model.exists(_.isEmpty)) None else model),
    localReferenceNumber.read[String],
    eadStatus.read[String],
    consignorTrader.read[ConsignorTraderModel](ConsignorTraderModel.xmlReads),
    dateOfDispatch.read[String],
    journeyTime.read[JourneyTime].map(_.toString),
    items.read[Seq[MovementItem]](strictReadSeq),
    numberOfItems.read[Seq[String]](strictReadSeq).map(_.length)
  ).mapN(GetMovementResponse.apply)

  implicit val format: OFormat[GetMovementResponse] = Json.format
}
