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
import uk.gov.hmrc.emcstfe.models.common.{MovementGuaranteeModel, TraderModel, TransportDetailsModel}

import scala.xml.{Elem, NodeSeq}

case class CreateMovementModel(
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
                                      transportDetails: Seq[TransportDetailsModel],
                                    ) extends CreateMovement {
  def toXml: Elem = <urn:SubmittedDraftOfEADESAD>
    {attributes.toXml}
    {consigneeTrader.map(trader => <urn:ConsigneeTrader language="en">{trader.toXml}</urn:ConsigneeTrader>).getOrElse(NodeSeq.Empty)}
    <urn:ConsignorTrader language="en">{consignorTrader.toXml}</urn:ConsignorTrader>
    {placeOfDispatchTrader.map(trader => <urn:PlaceOfDispatchTrader language="en">{trader.toXml}</urn:PlaceOfDispatchTrader>).getOrElse(NodeSeq.Empty)}
    {dispatchImportOffice.map(office => <urn:DispatchImportOffice>{office.toXml}</urn:DispatchImportOffice>).getOrElse(NodeSeq.Empty)}
    {complementConsigneeTrader.map(_.toXml).getOrElse(NodeSeq.Empty)}
    {deliveryPlaceTrader.map(trader => <urn:DeliveryPlaceTrader language="en">{trader.toXml}</urn:DeliveryPlaceTrader>).getOrElse(NodeSeq.Empty)}
    {deliveryPlaceCustomsOffice.map(office => <urn:DeliveryPlaceCustomsOffice>{office.toXml}</urn:DeliveryPlaceCustomsOffice>).getOrElse(NodeSeq.Empty)}
    <urn:CompetentAuthorityDispatchOffice>{competentAuthorityDispatchOffice.toXml}</urn:CompetentAuthorityDispatchOffice>
    {transportArrangerTrader.map(trader => <urn:TransportArrangerTrader language="en">{trader.toXml}</urn:TransportArrangerTrader>).getOrElse(NodeSeq.Empty)}
    {firstTransporterTrader.map(trader => <urn:FirstTransporterTrader language="en">{trader.toXml}</urn:FirstTransporterTrader>).getOrElse(NodeSeq.Empty)}
    {documentCertificate.map(_.map(_.toXml)).getOrElse(NodeSeq.Empty)}
    {headerEadEsad.toXml}
    {transportMode.toXml}
    {movementGuarantee.toXml}
    {bodyEadEsad.map(_.toXml)}
    {eadEsadDraft.toXml}
    {transportDetails.map(_.toXml)}
  </urn:SubmittedDraftOfEADESAD>
}

object CreateMovementModel {
  implicit val fmt: OFormat[CreateMovementModel] = Json.format
}
