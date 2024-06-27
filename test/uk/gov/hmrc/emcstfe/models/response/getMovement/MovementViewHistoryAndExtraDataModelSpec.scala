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

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess}
import uk.gov.hmrc.emcstfe.fixtures.GetMovementFixture
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionReasonType.{EADNotConcernRecipient, Other, ProductDoesNotMatchOrder, QuantityDoesNotMatchOrder}
import uk.gov.hmrc.emcstfe.models.alertOrRejection.AlertOrRejectionType.{Alert, Rejection}
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.{CancellationReasonModel, CancellationReasonType}
import uk.gov.hmrc.emcstfe.models.common.AcceptMovement.Satisfactory
import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement.{Excess, Shortage}
import uk.gov.hmrc.emcstfe.models.common._
import uk.gov.hmrc.emcstfe.models.explainDelay.DelayReasonType
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.BodyAnalysisModel
import uk.gov.hmrc.emcstfe.models.interruptionOfMovement.{InterruptionReasonModel, InterruptionReasonType}
import uk.gov.hmrc.emcstfe.models.reportOfReceipt.{ReceiptedItemsModel, SubmitReportOfReceiptModel, UnsatisfactoryModel}
import uk.gov.hmrc.emcstfe.models.response.getMovement.CustomsRejectionDiagnosisCodeType.DestinationTypeIsNotExport
import uk.gov.hmrc.emcstfe.models.response.getMovement.CustomsRejectionReasonCodeType.ExportDataNotFound
import uk.gov.hmrc.emcstfe.models.response.getMovement.NotificationOfDivertedMovementType.SplitMovement
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.time.{LocalDate, LocalDateTime}
import scala.xml.XML

class MovementViewHistoryAndExtraDataModelSpec extends TestBaseSpec with GetMovementFixture {
  "xmlReader" should {

    "successfully read a subset of the movement fields" in {
      MovementViewHistoryAndExtraDataModel.xmlReader.read(XML.loadString(maxGetMovementResponseBody())) shouldBe ParseSuccess(MovementViewHistoryAndExtraDataModel(
        arc = "ExciseMovementArc",
        serialNumberOfCertificateOfExemption = Some("CCTSerialNumber"),
        dispatchImportOfficeReferenceNumber = Some("DispatchImportOfficeErn"),
        deliveryPlaceCustomsOfficeReferenceNumber = Some("DeliveryPlaceCustomsOfficeErn"),
        competentAuthorityDispatchOfficeReferenceNumber = Some("CompetentAuthorityDispatchOfficeErn"),
        eadStatus = "Beans",
        dateAndTimeOfValidationOfEadEsad = "ExciseMovementDateTime",
        numberOfItems = 2,
        reportOfReceipt = Some(SubmitReportOfReceiptModel(
          arc = "21GB00000000000351266",
          sequenceNumber = 2,
          dateAndTimeOfValidationOfReportOfReceiptExport = Some("2021-09-10T11:11:12"),
          consigneeTrader = Some(
            TraderModel(
              traderExciseNumber = Some("XIWK000000206"),
              traderName = Some("SEED TRADER NI"),
              address = Some(
                AddressModel(
                  streetNumber = Some("1"),
                  street = Some("Catherdral"),
                  postcode = Some("BT3 7BF"),
                  city = Some("Salford")
                )),
              vatNumber = None,
              eoriNumber = None
            )
          ),
          deliveryPlaceTrader = Some(
            TraderModel(
              traderExciseNumber = Some("XI00000000207"),
              traderName = Some("SEED TRADER NI 2"),
              address = Some(
                AddressModel(
                  streetNumber = Some("2"),
                  street = Some("Catherdral"),
                  postcode = Some("BT3 7BF"),
                  city = Some("Salford")
                )),
              vatNumber = None,
              eoriNumber = None
            )
          ),
          destinationOffice = "XI004098",
          dateOfArrival = LocalDate.parse("2021-09-08"),
          acceptMovement = Satisfactory,
          otherInformation = Some("some great reason"),
          destinationType = None,
          individualItems = Seq(ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            excessAmount = Some(21),
            shortageAmount = None,
            productCode = "W300",
            refusedAmount = Some(1),
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(Excess, Some("some info")),
              UnsatisfactoryModel(Shortage, None),
            )
          ))
        )),
        notificationOfDivertedMovement = Some(NotificationOfDivertedMovementModel(
          notificationType = SplitMovement,
          notificationDateAndTime = LocalDateTime.of(2024, 6, 5, 0, 0, 1),
          downstreamArcs = Seq(testArc, s"${testArc.dropRight(1)}1")
        )),
        notificationOfAlertOrRejection = Some(Seq(
          NotificationOfAlertOrRejectionModel(
            notificationType = Alert,
            notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 9, 0, 0),
            alertRejectReason = Seq(
              AlertOrRejectionReasonModel(
                reason = ProductDoesNotMatchOrder,
                additionalInformation = Some("Info")
              ),
              AlertOrRejectionReasonModel(
                reason = EADNotConcernRecipient,
                additionalInformation = Some("Info")
              ),
              AlertOrRejectionReasonModel(
                reason = Other,
                additionalInformation = Some("Info")
              ),
              AlertOrRejectionReasonModel(
                reason = QuantityDoesNotMatchOrder,
                additionalInformation = Some("Info")
              )
            )
          ),
          NotificationOfAlertOrRejectionModel(
            notificationType = Alert,
            notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 10, 0, 0),
            alertRejectReason = Seq(AlertOrRejectionReasonModel(
              reason = EADNotConcernRecipient,
              additionalInformation = None
            ))
          ),
          NotificationOfAlertOrRejectionModel(
            notificationType = Rejection,
            notificationDateAndTime = LocalDateTime.of(2023, 12, 19, 9, 0, 0),
            alertRejectReason = Seq(AlertOrRejectionReasonModel(
              reason = QuantityDoesNotMatchOrder,
              additionalInformation = None
            ))
          )
        )),
        notificationOfAcceptedExport = Some(
          NotificationOfAcceptedExportModel(
            customsOfficeNumber = "GB000383",
            dateOfAcceptance = LocalDate.of(2024, 2, 5),
            referenceNumberOfSenderCustomsOffice = "GB000101",
            identificationOfSenderCustomsOfficer = "John Doe",
            documentReferenceNumber = "645564546",
            consigneeTrader = TraderModel(
              traderExciseNumber = Some("BE345345345"),
              traderName = Some("PEAR Supermarket"),
              address = Some(
                AddressModel(
                  streetNumber = None,
                  street = Some("Angels Business Park"),
                  postcode = Some("BD1 3NN"),
                  city = Some("Bradford")
                )),
              vatNumber = None,
              eoriNumber = Some("GB00000578901")
            )
          )
        ),
        notificationOfDelay = Some(Seq(
          NotificationOfDelayModel(
            submitterIdentification = "GBWK001234569",
            submitterType = SubmitterType.Consignor,
            explanationCode = DelayReasonType.Accident,
            complementaryInformation = Some("Lorry crashed off cliff"),
            dateTime = LocalDateTime.parse("2024-06-18T08:11:33")
          ),
          NotificationOfDelayModel(
            submitterIdentification = "GBWK001234569",
            submitterType = SubmitterType.Consignor,
            explanationCode = DelayReasonType.Strikes,
            complementaryInformation = None,
            dateTime = LocalDateTime.parse("2024-06-18T08:18:56")
          )
        )),
        cancelMovement = Some(CancellationReasonModel(CancellationReasonType.Other, Some("some info"))),
        notificationOfCustomsRejection = Some(
          NotificationOfCustomsRejectionModel(
            customsOfficeReferenceNumber = Some("AT001000"),
            rejectionDateAndTime = LocalDateTime.of(2024, 1, 14, 19, 14, 20),
            rejectionReasonCode = ExportDataNotFound,
            localReferenceNumber = Some("1111"),
            documentReferenceNumber = Some("7884"),
            diagnoses = Seq(
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "100",
                diagnosisCode = DestinationTypeIsNotExport
              ),
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "101",
                diagnosisCode = DestinationTypeIsNotExport
              ),
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "102",
                diagnosisCode = DestinationTypeIsNotExport
              ),
              CustomsRejectionDiagnosis(
                bodyRecordUniqueReference = "103",
                diagnosisCode = DestinationTypeIsNotExport
              )
            ),
            consignee = Some(
              TraderModel(
                traderExciseNumber = Some("XIWK000000206"),
                traderName = Some("SEED TRADER NI"),
                address = Some(
                  AddressModel(
                    streetNumber = Some("1"),
                    street = Some("Catherdral"),
                    postcode = Some("BT3 7BF"),
                    city = Some("Salford")
                  )),
                vatNumber = None,
                eoriNumber = None
              ))
          )
        ),
        notificationOfShortageOrExcess = Some(
          NotificationOfShortageOrExcessModel(
            submitterType = SubmitterType.Consignee,
            globalExplanation = None,
            individualItemReasons = Some(Seq(
              BodyAnalysisModel(
                exciseProductCode = "B000",
                bodyRecordUniqueReference = 1,
                explanation = "4 more than I expected",
                actualQuantity = Some(5)
              )
            ))
          )
        ),
        InterruptedMovement = Some(InterruptionReasonModel(InterruptionReasonType.Other, "FR1234", Some("some info")))
      )
      )

    }

    "fail to read a subset of the movement fields when a field is missing" in {
      lazy val movementResponseBodyWithoutARC: String =
        s"""<mov:movementView xsi:schemaLocation="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3 movementView.xsd" xmlns:mov="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementView/3">
           |    <mov:currentMovement>
           |      <mov:status>Accepted</mov:status>
           |      <mov:version_transaction_ref>008</mov:version_transaction_ref>
           |      <body:IE801 xmlns:body="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE801:V3.01">
           |        <body:Header xmlns:head="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE3:TMS:V2.02">
           |          <head:MessageSender>NDEA.FR</head:MessageSender>
           |          <head:MessageRecipient>NDEA.GB</head:MessageRecipient>
           |          <head:DateOfPreparation>2008-09-04</head:DateOfPreparation>
           |          <head:TimeOfPreparation>10:22:50</head:TimeOfPreparation>
           |          <head:MessageIdentifier>Message identifier</head:MessageIdentifier>
           |        </body:Header>
           |        <body:Body>
           |          <body:EADESADContainer>
           |            <body:ConsigneeTrader language="en">
           |              ${maxTraderModelXML(ConsigneeTrader)}
           |            </body:ConsigneeTrader>
           |            <body:ComplementConsigneeTrader>
           |              <MemberStateCode>GB</MemberStateCode>
           |            </body:ComplementConsigneeTrader>
           |            <body:ExciseMovement>
           |              <body:DateAndTimeOfValidationOfEadEsad>2008-09-04T10:22:50</body:DateAndTimeOfValidationOfEadEsad>
           |            </body:ExciseMovement>
           |            <body:ConsignorTrader language="en">
           |              ${maxTraderModelXML(ConsignorTrader)}
           |            </body:ConsignorTrader>
           |            <body:PlaceOfDispatchTrader language="en">
           |              ${maxTraderModelXML(PlaceOfDispatchTrader)}
           |            </body:PlaceOfDispatchTrader>
           |            <body:DeliveryPlaceCustomsOffice>
           |              <body:ReferenceNumber>FR000003</body:ReferenceNumber>
           |            </body:DeliveryPlaceCustomsOffice>
           |            <body:CompetentAuthorityDispatchOffice>
           |              <body:ReferenceNumber>GB000002</body:ReferenceNumber>
           |            </body:CompetentAuthorityDispatchOffice>
           |            <body:FirstTransporterTrader language="en">
           |              ${maxTraderModelXML(TransportTrader)}
           |            </body:FirstTransporterTrader>
           |            <body:DocumentCertificate>
           |              <body:DocumentDescription language="en">Test</body:DocumentDescription>
           |              <body:ReferenceOfDocument language="en">AB123</body:ReferenceOfDocument>
           |            </body:DocumentCertificate>
           |            <body:EadEsad>
           |              <body:LocalReferenceNumber>EN</body:LocalReferenceNumber>
           |              <body:InvoiceNumber>IN777888999</body:InvoiceNumber>
           |              <body:InvoiceDate>2008-09-04</body:InvoiceDate>
           |              <body:OriginTypeCode>1</body:OriginTypeCode>
           |              <body:DateOfDispatch>2008-11-20</body:DateOfDispatch>
           |              <body:TimeOfDispatch>10:00:00</body:TimeOfDispatch>
           |            </body:EadEsad>
           |            <body:HeaderEadEsad>
           |              <body:SequenceNumber>1</body:SequenceNumber>
           |              <body:DateAndTimeOfUpdateValidation>2008-09-04T10:22:50</body:DateAndTimeOfUpdateValidation>
           |              <body:DestinationTypeCode>6</body:DestinationTypeCode>
           |              <body:JourneyTime>D20</body:JourneyTime>
           |              <body:TransportArrangement>1</body:TransportArrangement>
           |            </body:HeaderEadEsad>
           |            <body:TransportMode>
           |              <body:TransportModeCode>1</body:TransportModeCode>
           |            </body:TransportMode>
           |            <body:MovementGuarantee>
           |              <body:GuarantorTypeCode>0</body:GuarantorTypeCode>
           |            </body:MovementGuarantee>
           |            <body:BodyEadEsad>
           |              <body:BodyRecordUniqueReference>1</body:BodyRecordUniqueReference>
           |              <body:ExciseProductCode>W200</body:ExciseProductCode>
           |              <body:CnCode>22041011</body:CnCode>
           |              <body:Quantity>500</body:Quantity>
           |              <body:GrossMass>900</body:GrossMass>
           |              <body:NetMass>375</body:NetMass>
           |              <body:FiscalMark language="en">FM564789 Fiscal Mark</body:FiscalMark>
           |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
           |              <body:DegreePlato>1.2</body:DegreePlato>
           |              <body:MaturationPeriodOrAgeOfProducts language="EN">Maturation Period</body:MaturationPeriodOrAgeOfProducts>
           |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
           |              <body:SizeOfProducer>20000</body:SizeOfProducer>
           |              <body:Density>880</body:Density>
           |              <body:CommercialDescription language="en">Retsina</body:CommercialDescription>
           |              <body:BrandNameOfProducts language="en">MALAMATINA</body:BrandNameOfProducts>
           |              <body:Package>
           |                <body:KindOfPackages>BO</body:KindOfPackages>
           |                <body:NumberOfPackages>125</body:NumberOfPackages>
           |                <body:ShippingMarks>MARKS</body:ShippingMarks>
           |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
           |                <body:SealInformation language="en">Red Strip</body:SealInformation>
           |              </body:Package>
           |              <body:WineProduct>
           |                <body:WineProductCategory>4</body:WineProductCategory>
           |                <body:WineGrowingZoneCode>2</body:WineGrowingZoneCode>
           |                <body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
           |                <body:OtherInformation language="en">Not available</body:OtherInformation>
           |                <body:WineOperation>
           |                  <body:WineOperationCode>4</body:WineOperationCode>
           |                </body:WineOperation>
           |                <body:WineOperation>
           |                  <body:WineOperationCode>5</body:WineOperationCode>
           |                </body:WineOperation>
           |              </body:WineProduct>
           |            </body:BodyEadEsad>
           |            <body:BodyEadEsad>
           |              <body:BodyRecordUniqueReference>2</body:BodyRecordUniqueReference>
           |              <body:ExciseProductCode>W300</body:ExciseProductCode>
           |              <body:CnCode>27111901</body:CnCode>
           |              <body:Quantity>501</body:Quantity>
           |              <body:GrossMass>901</body:GrossMass>
           |              <body:NetMass>475</body:NetMass>
           |              <body:AlcoholicStrengthByVolumeInPercentage>12.7</body:AlcoholicStrengthByVolumeInPercentage>
           |              <body:FiscalMark language="en">FM564790 Fiscal Mark</body:FiscalMark>
           |              <body:FiscalMarkUsedFlag>1</body:FiscalMarkUsedFlag>
           |              <body:DesignationOfOrigin language="en">Designation of Origin</body:DesignationOfOrigin>
           |              <body:SizeOfProducer>20000</body:SizeOfProducer>
           |              <body:CommercialDescription language="en">Retsina</body:CommercialDescription>
           |              <body:BrandNameOfProducts language="en">BrandName</body:BrandNameOfProducts>
           |              <body:Package>
           |                <body:KindOfPackages>BO</body:KindOfPackages>
           |                <body:NumberOfPackages>125</body:NumberOfPackages>
           |                <body:CommercialSealIdentification>SEAL456789321</body:CommercialSealIdentification>
           |                <body:SealInformation language="en">Red Strip</body:SealInformation>
           |              </body:Package>
           |              <body:Package>
           |                <body:KindOfPackages>HG</body:KindOfPackages>
           |                <body:NumberOfPackages>7</body:NumberOfPackages>
           |                <body:CommercialSealIdentification>SEAL77</body:CommercialSealIdentification>
           |                <body:SealInformation language="en">Cork</body:SealInformation>
           |              </body:Package>
           |              <body:WineProduct>
           |                <body:WineProductCategory>3</body:WineProductCategory>
           |                <body:ThirdCountryOfOrigin>FJ</body:ThirdCountryOfOrigin>
           |                <body:OtherInformation language="en">Not available</body:OtherInformation>
           |                <body:WineOperation>
           |                  <body:WineOperationCode>0</body:WineOperationCode>
           |                </body:WineOperation>
           |                <body:WineOperation>
           |                  <body:WineOperationCode>1</body:WineOperationCode>
           |                </body:WineOperation>
           |              </body:WineProduct>
           |            </body:BodyEadEsad>
           |            <body:TransportDetails>
           |              <body:TransportUnitCode>1</body:TransportUnitCode>
           |              <body:IdentityOfTransportUnits>Bottles</body:IdentityOfTransportUnits>
           |              <body:CommercialSealIdentification>SID13245678</body:CommercialSealIdentification>
           |              <body:ComplementaryInformation language="en">Bottles of Restina</body:ComplementaryInformation>
           |              <body:SealInformation language="en">Sealed with red strip</body:SealInformation>
           |            </body:TransportDetails>
           |            <body:TransportDetails>
           |              <body:TransportUnitCode>2</body:TransportUnitCode>
           |              <body:IdentityOfTransportUnits>Cans</body:IdentityOfTransportUnits>
           |              <body:CommercialSealIdentification>SID132987</body:CommercialSealIdentification>
           |              <body:ComplementaryInformation language="en">Cans</body:ComplementaryInformation>
           |              <body:SealInformation language="en">Seal</body:SealInformation>
           |            </body:TransportDetails>
           |          </body:EADESADContainer>
           |        </body:Body>
           |      </body:IE801>
           |    </mov:currentMovement>
           |    <mov:eventHistory>
           |    </mov:eventHistory>
           |  </mov:movementView>""".stripMargin

      GetMovementResponse.xmlReader.read(XML.loadString(movementResponseBodyWithoutARC)) shouldBe ParseFailure(Seq(
        EmptyError(GetMovementResponse.arc)
      ))
    }

  }
}
