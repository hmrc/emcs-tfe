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
    transportDetails: Seq[TransportDetailsModel]
)

object GetMovementResponse extends JsonUtils with XmlReaderUtils {

  lazy val currentMovement: XPath                                 = XPath \\ "currentMovement"
  lazy val eadStatus: XPath                                       = currentMovement \ "status"
  lazy val EADESADContainer: XPath                                = currentMovement \ "IE801" \ "Body" \ "EADESADContainer"
  lazy val arc: XPath                                             = EADESADContainer \ "ExciseMovement" \ "AdministrativeReferenceCode"
  lazy val sequenceNumber: XPath                                  = EADESADContainer \ "HeaderEadEsad" \ "SequenceNumber"
  lazy val destinationTypeCode: XPath                             = EADESADContainer \ "HeaderEadEsad" \ "DestinationTypeCode"
  lazy val memberStateCode: XPath                                 = EADESADContainer \ "ComplementConsigneeTrader" \ "MemberStateCode"
  lazy val localReferenceNumber: XPath                            = EADESADContainer \ "EadEsad" \ "LocalReferenceNumber"
  lazy val dateOfDispatch: XPath                                  = EADESADContainer \ "EadEsad" \ "DateOfDispatch"
  lazy val dateAndTimeOfValidationOfEadEsad: XPath                = EADESADContainer \ "ExciseMovement" \ "DateAndTimeOfValidationOfEadEsad"
  lazy val journeyTime: XPath                                     = EADESADContainer \ "HeaderEadEsad" \ "JourneyTime"
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
  lazy val numberOfItems: XPath                                   = EADESADContainer \\ "BodyEadEsad" \\ "CnCode"
  lazy val serialNumberOfCertificateOfExemption: XPath            = EADESADContainer \ "ComplementConsigneeTrader" \ "SerialNumberOfCertificateOfExemption"
  lazy val transportDetails: XPath                                = EADESADContainer \ "TransportDetails"

  implicit lazy val xmlReader: XmlReader[GetMovementResponse] =
    for {
      arc                                             <- arc.read[String]
      sequenceNumber                                  <- sequenceNumber.read[Int]
      destinationTypeCode                             <- destinationTypeCode.read[DestinationType](DestinationType.xmlReads(DestinationType.enumerable))
      memberStateCode                                 <- memberStateCode.read[Option[String]]
      serialNumberOfCertificateOfExemption            <- serialNumberOfCertificateOfExemption.read[Option[String]]
      consignorTrader                                 <- consignorTrader.read[TraderModel](TraderModel.xmlReads(ConsignorTrader))
      consigneeTrader                                 <- consigneeTrader.read[Option[TraderModel]](TraderModel.xmlReads(ConsigneeTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model)
      deliveryPlaceTrader                             <- deliveryPlaceTrader.read[Option[TraderModel]](TraderModel.xmlReads(DeliveryPlaceTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model)
      placeOfDispatchTrader                           <- placeOfDispatchTrader.read[Option[TraderModel]](TraderModel.xmlReads(PlaceOfDispatchTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model)
      transportArrangerTrader                         <- transportArrangerTrader.read[Option[TraderModel]](TraderModel.xmlReads(TransportTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model)
      firstTransporterTrader                          <- firstTransporterTrader.read[Option[TraderModel]](TraderModel.xmlReads(TransportTrader).optional).map(model => if (model.exists(_.isEmpty)) None else model)
      dispatchImportOfficeReferenceNumber             <- dispatchImportOfficeReferenceNumber.read[Option[String]]
      deliveryPlaceCustomsOfficeReferenceNumber       <- deliveryPlaceCustomsOfficeReferenceNumber.read[Option[String]]
      competentAuthorityDispatchOfficeReferenceNumber <- competentAuthorityDispatchOfficeReferenceNumber.read[Option[String]]
      localReferenceNumber                            <- localReferenceNumber.read[String]
      eadStatus                                       <- eadStatus.read[String]
      dateAndTimeOfValidationOfEadEsad                <- dateAndTimeOfValidationOfEadEsad.read[String]
      dateOfDispatch                                  <- dateOfDispatch.read[String]
      journeyTime                                     <- journeyTime.read[JourneyTime].map(_.toString)
      documentCertificate                             <- documentCertificate.read[Seq[DocumentCertificateModel]](strictReadSeq(DocumentCertificateModel.xmlReads)).seqToOptionSeq
      eadEsad                                         <- eadEsad.read[EadEsadModel]
      headerEadEsad                                   <- headerEadEsad.read[HeaderEadEsadModel]
      transportMode                                   <- transportMode.read[TransportModeModel]
      movementGuarantee                               <- movementGuarantee.read[MovementGuaranteeModel]
      items                                           <- items.read[Seq[MovementItem]](strictReadSeq)
      numberOfItems                                   <- numberOfItems.read[Seq[String]](strictReadSeq).map(_.length)
      transportDetails                                <- transportDetails.read[Seq[TransportDetailsModel]](strictReadSeq)
    } yield {
      GetMovementResponse(
        arc = arc,
        sequenceNumber = sequenceNumber,
        destinationType = destinationTypeCode,
        memberStateCode = memberStateCode,
        serialNumberOfCertificateOfExemption = serialNumberOfCertificateOfExemption,
        consignorTrader = consignorTrader,
        consigneeTrader = consigneeTrader,
        deliveryPlaceTrader = deliveryPlaceTrader,
        placeOfDispatchTrader = placeOfDispatchTrader,
        transportArrangerTrader = transportArrangerTrader,
        firstTransporterTrader = firstTransporterTrader,
        dispatchImportOfficeReferenceNumber = dispatchImportOfficeReferenceNumber,
        deliveryPlaceCustomsOfficeReferenceNumber = deliveryPlaceCustomsOfficeReferenceNumber,
        competentAuthorityDispatchOfficeReferenceNumber = competentAuthorityDispatchOfficeReferenceNumber,
        localReferenceNumber = localReferenceNumber,
        eadStatus = eadStatus,
        dateAndTimeOfValidationOfEadEsad = dateAndTimeOfValidationOfEadEsad,
        dateOfDispatch = dateOfDispatch,
        journeyTime = journeyTime,
        documentCertificate = documentCertificate,
        eadEsad = eadEsad,
        headerEadEsad = headerEadEsad,
        transportMode = transportMode,
        movementGuarantee = movementGuarantee,
        items = items,
        numberOfItems = numberOfItems,
        transportDetails = transportDetails
      )
    }

  implicit lazy val reads: Reads[GetMovementResponse] = for {
    arc                                             <- (__ \ "arc").read[String]
    sequenceNumber                                  <- (__ \ "sequenceNumber").read[Int]
    destinationType                                 <- (__ \ "destinationType").read[DestinationType]
    memberStateCode                                 <- (__ \ "memberStateCode").readNullable[String]
    serialNumberOfCertificateOfExemption            <- (__ \ "serialNumberOfCertificateOfExemption").readNullable[String]
    consignorTrader                                 <- (__ \ "consignorTrader").read[TraderModel]
    consigneeTrader                                 <- (__ \ "consigneeTrader").readNullable[TraderModel]
    deliveryPlaceTrader                             <- (__ \ "deliveryPlaceTrader").readNullable[TraderModel]
    placeOfDispatchTrader                           <- (__ \ "placeOfDispatchTrader").readNullable[TraderModel]
    transportArrangerTrader                         <- (__ \ "transportArrangerTrader").readNullable[TraderModel]
    firstTransporterTrader                          <- (__ \ "firstTransporterTrader").readNullable[TraderModel]
    dispatchImportOfficeReferenceNumber             <- (__ \ "dispatchImportOfficeReferenceNumber").readNullable[String]
    deliveryPlaceCustomsOfficeReferenceNumber       <- (__ \ "deliveryPlaceCustomsOfficeReferenceNumber").readNullable[String]
    competentAuthorityDispatchOfficeReferenceNumber <- (__ \ "competentAuthorityDispatchOfficeReferenceNumber").readNullable[String]
    localReferenceNumber                            <- (__ \ "localReferenceNumber").read[String]
    eadStatus                                       <- (__ \ "eadStatus").read[String]
    dateAndTimeOfValidationOfEadEsad                <- (__ \ "dateAndTimeOfValidationOfEadEsad").read[String]
    dateOfDispatch                                  <- (__ \ "dateOfDispatch").read[String]
    journeyTime                                     <- (__ \ "journeyTime").read[String]
    documentCertificate                             <- (__ \ "documentCertificate").readNullable[Seq[DocumentCertificateModel]]
    eadEsad                                         <- (__ \ "eadEsad").read[EadEsadModel]
    headerEadEsad                                   <- (__ \ "headerEadEsad").read[HeaderEadEsadModel]
    transportMode                                   <- (__ \ "transportMode").read[TransportModeModel]
    movementGuarantee                               <- (__ \ "movementGuarantee").read[MovementGuaranteeModel]
    items                                           <- (__ \ "items").read[Seq[MovementItem]]
    numberOfItems                                   <- (__ \ "numberOfItems").read[Int]
    transportDetails                                <- (__ \ "transportDetails").read[Seq[TransportDetailsModel]]
  } yield {
    GetMovementResponse(
      arc = arc,
      sequenceNumber = sequenceNumber,
      destinationType = destinationType,
      memberStateCode = memberStateCode,
      serialNumberOfCertificateOfExemption = serialNumberOfCertificateOfExemption,
      consignorTrader = consignorTrader,
      consigneeTrader = consigneeTrader,
      deliveryPlaceTrader = deliveryPlaceTrader,
      placeOfDispatchTrader = placeOfDispatchTrader,
      transportArrangerTrader = transportArrangerTrader,
      firstTransporterTrader = firstTransporterTrader,
      dispatchImportOfficeReferenceNumber = dispatchImportOfficeReferenceNumber,
      deliveryPlaceCustomsOfficeReferenceNumber = deliveryPlaceCustomsOfficeReferenceNumber,
      competentAuthorityDispatchOfficeReferenceNumber = competentAuthorityDispatchOfficeReferenceNumber,
      localReferenceNumber = localReferenceNumber,
      eadStatus = eadStatus,
      dateAndTimeOfValidationOfEadEsad = dateAndTimeOfValidationOfEadEsad,
      dateOfDispatch = dateOfDispatch,
      journeyTime = journeyTime,
      documentCertificate = documentCertificate,
      eadEsad = eadEsad,
      headerEadEsad = headerEadEsad,
      transportMode = transportMode,
      movementGuarantee = movementGuarantee,
      items = items,
      numberOfItems = numberOfItems,
      transportDetails = transportDetails
    )
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
        "localReferenceNumber"                            -> o.localReferenceNumber,
        "eadStatus"                                       -> o.eadStatus,
        "dateAndTimeOfValidationOfEadEsad"                -> o.dateAndTimeOfValidationOfEadEsad,
        "dateOfDispatch"                                  -> o.dateOfDispatch,
        "journeyTime"                                     -> o.journeyTime,
        "documentCertificate"                             -> o.documentCertificate,
        "eadEsad"                                         -> o.eadEsad,
        "headerEadEsad"                                   -> o.headerEadEsad,
        "transportMode"                                   -> o.transportMode,
        "movementGuarantee"                               -> o.movementGuarantee,
        "items"                                           -> o.items,
        "numberOfItems"                                   -> o.numberOfItems,
        "transportDetails"                                -> o.transportDetails
      )
      .removeNullValues()

}
