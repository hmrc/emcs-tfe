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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import cats.implicits.catsSyntaxTuple15Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader}
import play.api.libs.json._
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.utils.{JsonUtils, XmlReaderUtils}

case class GetMovementResponse(
    arc: String,
    sequenceNumber: Int,
    destinationType: DestinationType,
    memberStateCode: Option[String],
    serialNumberOfCertificateOfExemption: Option[String],
    consignorTrader: TraderModel,
    consigneeTrader: Option[TraderModel],
    deliveryPlaceTrader: Option[TraderModel],
    placeOfDispatchTrader: Option[TraderModel],
    transportArrangerTrader: Option[TraderModel],
    firstTransporterTrader: Option[TraderModel],
    dispatchImportOfficeReferenceNumber: Option[String],
    deliveryPlaceCustomsOfficeReferenceNumber: Option[String],
    competentAuthorityDispatchOfficeReferenceNumber: Option[String],
    localReferenceNumber: String,
    eadStatus: String,
    dateAndTimeOfValidationOfEadEsad: String,
    dateOfDispatch: String,
    journeyTime: String,
    documentCertificate: Option[Seq[DocumentCertificateModel]],
    eadEsad: EadEsadModel,
    headerEadEsad: HeaderEadEsadModel,
    transportMode: TransportModeModel,
    movementGuarantee: MovementGuaranteeModel,
    items: Seq[MovementItem],
    numberOfItems: Int,
    transportDetails: Seq[TransportDetailsModel],
    movementViewHistoryAndExtraData: MovementViewHistoryAndExtraDataModel
)

object GetMovementResponse extends JsonUtils with XmlReaderUtils {

  lazy val currentMovement: XPath                                 = XPath \\ "currentMovement"
  lazy val eadStatus: XPath                                       = currentMovement \ "status"
  lazy val EADESADContainer: XPath                                = currentMovement \ "IE801" \ "Body" \ "EADESADContainer"
  lazy val arc: XPath                                             = EADESADContainer \ "ExciseMovement" \ "AdministrativeReferenceCode"
  lazy val dateAndTimeOfValidationOfEadEsad: XPath                = EADESADContainer \ "ExciseMovement" \ "DateAndTimeOfValidationOfEadEsad"
  lazy val memberStateCode: XPath                                 = EADESADContainer \ "ComplementConsigneeTrader" \ "MemberStateCode"
  lazy val serialNumberOfCertificateOfExemption: XPath            = EADESADContainer \ "ComplementConsigneeTrader" \ "SerialNumberOfCertificateOfExemption"
  lazy val consignorTrader: XPath                                 = EADESADContainer \\ "ConsignorTrader"
  lazy val consigneeTrader: XPath                                 = EADESADContainer \\ "ConsigneeTrader"
  lazy val deliveryPlaceTrader: XPath                             = EADESADContainer \\ "DeliveryPlaceTrader"
  lazy val placeOfDispatchTrader: XPath                           = EADESADContainer \\ "PlaceOfDispatchTrader"
  lazy val transportArrangerTrader: XPath                         = EADESADContainer \\ "TransportArrangerTrader"
  lazy val firstTransporterTrader: XPath                          = EADESADContainer \\ "FirstTransporterTrader"
  lazy val dispatchImportOfficeReferenceNumber: XPath             = EADESADContainer \\ "DispatchImportOffice" \ "ReferenceNumber"
  lazy val deliveryPlaceCustomsOfficeReferenceNumber: XPath       = EADESADContainer \\ "DeliveryPlaceCustomsOffice" \ "ReferenceNumber"
  lazy val competentAuthorityDispatchOfficeReferenceNumber: XPath = EADESADContainer \\ "CompetentAuthorityDispatchOffice" \ "ReferenceNumber"
  lazy val documentCertificate: XPath                             = EADESADContainer \\ "DocumentCertificate"
  lazy val eadEsad: XPath                                         = EADESADContainer \ "EadEsad"
  lazy val headerEadEsad: XPath                                   = EADESADContainer \ "HeaderEadEsad"
  lazy val transportMode: XPath                                   = EADESADContainer \ "TransportMode"
  lazy val movementGuarantee: XPath                               = EADESADContainer \ "MovementGuarantee"
  lazy val items: XPath                                           = EADESADContainer \ "BodyEadEsad"
  lazy val numberOfItems: XPath                                   = items \\ "CnCode"
  lazy val transportDetails: XPath                                = EADESADContainer \ "TransportDetails"

  implicit lazy val xmlReader: XmlReader[GetMovementResponse] = {
    (
      memberStateCode.read[Option[String]],
      consignorTrader.read[TraderModel](TraderModel.xmlReads(ConsignorTrader)),
      consigneeTrader.read[Option[TraderModel]](TraderModel.xmlReads(ConsigneeTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model),
      deliveryPlaceTrader.read[Option[TraderModel]](TraderModel.xmlReads(DeliveryPlaceTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model),
      placeOfDispatchTrader.read[Option[TraderModel]](TraderModel.xmlReads(PlaceOfDispatchTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model),
      transportArrangerTrader.read[Option[TraderModel]](TraderModel.xmlReads(TransportTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model),
      firstTransporterTrader.read[Option[TraderModel]](TraderModel.xmlReads(TransportTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model),
      documentCertificate.read[Seq[DocumentCertificateModel]](strictReadSeq(DocumentCertificateModel.xmlReads)).seqToOptionSeq,
      eadEsad.read[EadEsadModel],
      headerEadEsad.read[HeaderEadEsadModel],
      transportMode.read[TransportModeModel],
      movementGuarantee.read[MovementGuaranteeModel],
      items.read[Seq[MovementItem]](strictReadSeq),
      transportDetails.read[Seq[TransportDetailsModel]](strictReadSeq),
      XPath.read[MovementViewHistoryAndExtraDataModel](MovementViewHistoryAndExtraDataModel.xmlReader)
    ).mapN {
      case (
            memberStateCode,
            consignorTrader,
            consigneeTrader,
            deliveryPlaceTrader,
            placeOfDispatchTrader,
            transportArrangerTrader,
            firstTransporterTrader,
            documentCertificate,
            eadEsad,
            headerEadEsad,
            transportMode,
            movementGuarantee,
            items,
            transportDetails,
            movementViewHistoryAndExtraData) =>
        GetMovementResponse(
          arc = movementViewHistoryAndExtraData.arc,
          sequenceNumber = headerEadEsad.sequenceNumber,
          destinationType = headerEadEsad.destinationType,
          memberStateCode = memberStateCode,
          serialNumberOfCertificateOfExemption = movementViewHistoryAndExtraData.serialNumberOfCertificateOfExemption,
          consignorTrader = consignorTrader,
          consigneeTrader = consigneeTrader,
          deliveryPlaceTrader = deliveryPlaceTrader,
          placeOfDispatchTrader = placeOfDispatchTrader,
          transportArrangerTrader = transportArrangerTrader,
          firstTransporterTrader = firstTransporterTrader,
          dispatchImportOfficeReferenceNumber = movementViewHistoryAndExtraData.dispatchImportOfficeReferenceNumber,
          deliveryPlaceCustomsOfficeReferenceNumber = movementViewHistoryAndExtraData.deliveryPlaceCustomsOfficeReferenceNumber,
          competentAuthorityDispatchOfficeReferenceNumber = movementViewHistoryAndExtraData.competentAuthorityDispatchOfficeReferenceNumber,
          localReferenceNumber = eadEsad.localReferenceNumber,
          eadStatus = movementViewHistoryAndExtraData.eadStatus,
          dateAndTimeOfValidationOfEadEsad = movementViewHistoryAndExtraData.dateAndTimeOfValidationOfEadEsad,
          dateOfDispatch = eadEsad.dateOfDispatch,
          journeyTime = headerEadEsad.journeyTime,
          documentCertificate = documentCertificate,
          eadEsad = eadEsad,
          headerEadEsad = headerEadEsad,
          transportMode = transportMode,
          movementGuarantee = movementGuarantee,
          items = items,
          numberOfItems = movementViewHistoryAndExtraData.numberOfItems,
          transportDetails = transportDetails,
          movementViewHistoryAndExtraData = movementViewHistoryAndExtraData
        )
    }
  }

  implicit lazy val writes: OWrites[GetMovementResponse] = (o: GetMovementResponse) =>
    Json
      .obj(
        "arc"                                             -> o.arc,
        "sequenceNumber"                                  -> o.sequenceNumber,
        "destinationType"                                 -> o.destinationType,
        "memberStateCode"                                 -> o.memberStateCode,
        "serialNumberOfCertificateOfExemption"            -> o.serialNumberOfCertificateOfExemption,
        "consignorTrader"                                 -> o.consignorTrader,
        "consigneeTrader"                                 -> o.consigneeTrader,
        "deliveryPlaceTrader"                             -> o.deliveryPlaceTrader,
        "placeOfDispatchTrader"                           -> o.placeOfDispatchTrader,
        "transportArrangerTrader"                         -> o.transportArrangerTrader,
        "firstTransporterTrader"                          -> o.firstTransporterTrader,
        "dispatchImportOfficeReferenceNumber"             -> o.dispatchImportOfficeReferenceNumber,
        "deliveryPlaceCustomsOfficeReferenceNumber"       -> o.deliveryPlaceCustomsOfficeReferenceNumber,
        "competentAuthorityDispatchOfficeReferenceNumber" -> o.competentAuthorityDispatchOfficeReferenceNumber,
        "localReferenceNumber"                            -> o.eadEsad.localReferenceNumber,
        "eadStatus"                                       -> o.eadStatus,
        "dateAndTimeOfValidationOfEadEsad"                -> o.dateAndTimeOfValidationOfEadEsad,
        "dateOfDispatch"                                  -> o.dateOfDispatch,
        "journeyTime"                                     -> o.headerEadEsad.journeyTime,
        "documentCertificate"                             -> o.documentCertificate,
        "eadEsad"                                         -> o.eadEsad,
        "headerEadEsad"                                   -> o.headerEadEsad,
        "transportMode"                                   -> o.transportMode,
        "movementGuarantee"                               -> o.movementGuarantee,
        "items"                                           -> o.items,
        "numberOfItems"                                   -> o.numberOfItems,
        "transportDetails"                                -> o.transportDetails,
        "reportOfReceipt"                                 -> o.movementViewHistoryAndExtraData.reportOfReceipt,
        "notificationOfDivertedMovement"                  -> o.movementViewHistoryAndExtraData.notificationOfDivertedMovement,
        "notificationOfAlertOrRejection"                  -> o.movementViewHistoryAndExtraData.notificationOfAlertOrRejection,
        "notificationOfAcceptedExport"                    -> o.movementViewHistoryAndExtraData.notificationOfAcceptedExport
      )
      .removeNullValues()

}
