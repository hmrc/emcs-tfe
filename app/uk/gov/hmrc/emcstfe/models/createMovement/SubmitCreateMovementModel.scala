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

package uk.gov.hmrc.emcstfe.models.createMovement

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class SubmitCreateMovementModel(
                                movementType: MovementType,
                                attributes: AttributesModel,
                                consigneeTrader: Option[TraderModel],
                                consignorTrader: TraderModel,
                                placeOfDispatchTrader: Option[TraderModel],
                                dispatchImportOffice: Option[OfficeModel],
                                complementConsigneeTrader: Option[ComplementConsigneeTraderModel],
                                deliveryPlaceTrader: Option[TraderModel],
                                deliveryPlaceCustomsOffice: Option[OfficeModel],
                                competentAuthorityDispatchOffice: OfficeModel,
                                transportArrangerTrader: Option[TraderModel],
                                firstTransporterTrader: Option[TraderModel],
                                documentCertificate: Option[Seq[DocumentCertificateModel]],
                                headerEadEsad: HeaderEadEsadModel,
                                transportMode: TransportModeModel,
                                movementGuarantee: MovementGuaranteeModel,
                                bodyEadEsad: Seq[BodyEadEsadModel],
                                eadEsadDraft: EadEsadDraftModel,
                                transportDetails: Seq[TransportDetailsModel]
                              ) extends XmlBaseModel with XmlWriterUtils {
  def toXml(implicit request: UserRequest[_]): Elem = <urn:SubmittedDraftOfEADESAD>
    {attributes.toXml}
    {consigneeTrader.mapNodeSeq(trader => <urn:ConsigneeTrader language="en">{trader.toXml(ConsigneeTrader)}</urn:ConsigneeTrader>)}
    <urn:ConsignorTrader language="en">{consignorTrader.toXml(ConsignorTrader)}</urn:ConsignorTrader>
    {placeOfDispatchTrader.mapNodeSeq(trader => <urn:PlaceOfDispatchTrader language="en">{trader.toXml(PlaceOfDispatchTrader)}</urn:PlaceOfDispatchTrader>)}
    {dispatchImportOffice.mapNodeSeq(office => <urn:DispatchImportOffice>{office.toXml}</urn:DispatchImportOffice>)}
    {complementConsigneeTrader.mapNodeSeq(_.toXml)}
    {deliveryPlaceTrader.mapNodeSeq(trader => <urn:DeliveryPlaceTrader language="en">{trader.toXml(DeliveryPlaceTrader)}</urn:DeliveryPlaceTrader>)}
    {deliveryPlaceCustomsOffice.mapNodeSeq(office => <urn:DeliveryPlaceCustomsOffice>{office.toXml}</urn:DeliveryPlaceCustomsOffice>)}
    <urn:CompetentAuthorityDispatchOffice>{competentAuthorityDispatchOffice.toXml}</urn:CompetentAuthorityDispatchOffice>
    {transportArrangerTrader.mapNodeSeq(trader => <urn:TransportArrangerTrader language="en">{trader.toXml(TransportTrader)}</urn:TransportArrangerTrader>)}
    {firstTransporterTrader.mapNodeSeq(trader => <urn:FirstTransporterTrader language="en">{trader.toXml(TransportTrader)}</urn:FirstTransporterTrader>)}
    {documentCertificate.mapNodeSeq(_.map(_.toXml))}
    {headerEadEsad.toXml}
    {transportMode.toXml}
    {movementGuarantee.toXml}
    {bodyEadEsad.map(_.toXml)}
    {eadEsadDraft.toXml}
    {transportDetails.map(_.toXml)}
  </urn:SubmittedDraftOfEADESAD>
}

object SubmitCreateMovementModel {
  implicit val fmt: OFormat[SubmitCreateMovementModel] = Json.format
}
