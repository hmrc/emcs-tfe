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

package uk.gov.hmrc.emcstfe.models.request

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.config.Constants
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, MovementType}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.util.Base64
import scala.xml.Utility.trim
import scala.xml.XML

class SubmitCreateMovementRequestSpec extends TestBaseSpec with CreateMovementFixtures {

  val consigneeTraderCountryCode = "AA"
  val placeOfDispatchTraderCountryCode = "BB"
  val complementConsigneeTraderCountryCode = "CC"
  val deliveryPlaceTraderCountryCode = "DD"
  val deliveryPlaceCustomsOfficeCountryCode = "EE"
  val consignorTraderCountryCode = "FF"

  implicit val request: SubmitCreateMovementRequest = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = false, isChRISSubmission = false)

  s".messageRecipientCountryCode()" when {

    "movementType is UKtoUK" should {

      s"return country code $deliveryPlaceTraderCountryCode from deliveryPlaceTrader" in {

        requestWithMovement(MovementType.UKtoUK).messageRecipientCountryCode() shouldBe deliveryPlaceTraderCountryCode
      }
    }

    "movementType is ImportUk" should {

      s"return country code $deliveryPlaceTraderCountryCode from deliveryPlaceTrader" in {

        requestWithMovement(MovementType.ImportUK).messageRecipientCountryCode() shouldBe deliveryPlaceTraderCountryCode
      }
    }

    "movementType is UKtoEU" when {

      "destinationType is ExemptedOrganisations" should {

        s"return country code $complementConsigneeTraderCountryCode from complementConsigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.ExemptedOrganisations).messageRecipientCountryCode() shouldBe complementConsigneeTraderCountryCode
        }
      }

      "destinationType is TaxWarehouse" should {

        s"return country code $deliveryPlaceTraderCountryCode from deliveryPlaceTrader" in {

          requestWithMovement(MovementType.UKtoEU).messageRecipientCountryCode() shouldBe deliveryPlaceTraderCountryCode
        }
      }

      "destinationType is RegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.RegisteredConsignee).messageRecipientCountryCode() shouldBe consigneeTraderCountryCode
        }
      }

      "destinationType is TemporaryRegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.TemporaryRegisteredConsignee).messageRecipientCountryCode() shouldBe consigneeTraderCountryCode
        }
      }

      "destinationType is DirectDelivery" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.DirectDelivery).messageRecipientCountryCode() shouldBe consigneeTraderCountryCode
        }
      }

      "destinationType is Export" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.Export).messageRecipientCountryCode() shouldBe consigneeTraderCountryCode
        }
      }

      "destinationType is UnknownDestination" should {

        s"return country code $placeOfDispatchTraderCountryCode from placeOfDispatchTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.UnknownDestination).messageRecipientCountryCode() shouldBe placeOfDispatchTraderCountryCode
        }
      }
    }

    "movementType is DirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.DirectExport).messageRecipientCountryCode() shouldBe deliveryPlaceCustomsOfficeCountryCode
      }
    }

    "movementType is IndirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.IndirectExport).messageRecipientCountryCode() shouldBe deliveryPlaceCustomsOfficeCountryCode
      }
    }

    "movementType is ImportDirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.ImportDirectExport).messageRecipientCountryCode() shouldBe deliveryPlaceCustomsOfficeCountryCode
      }
    }

    "movementType is ImportIndirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.ImportIndirectExport).messageRecipientCountryCode() shouldBe deliveryPlaceCustomsOfficeCountryCode
      }
    }

    "movementType is ImportEU" when {

      "destinationType is TaxWarehouse" should {

        s"return country code $deliveryPlaceTraderCountryCode from deliveryPlaceTrader " in {

          requestWithMovement(MovementType.ImportEU).messageRecipientCountryCode() shouldBe deliveryPlaceTraderCountryCode
        }
      }

      "destinationType is ExemptedOrganisations" should {

        s"return country code $complementConsigneeTraderCountryCode from complementConsigneeTrader" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.ExemptedOrganisations).messageRecipientCountryCode() shouldBe complementConsigneeTraderCountryCode
        }
      }

      "destinationType is DirectDelivery" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.DirectDelivery).messageRecipientCountryCode() shouldBe consigneeTraderCountryCode
        }
      }

      "destinationType is RegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTraderCountryCode" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.RegisteredConsignee).messageRecipientCountryCode() shouldBe consigneeTraderCountryCode
        }
      }

      "destinationType is TemporaryRegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.TemporaryRegisteredConsignee).messageRecipientCountryCode() shouldBe consigneeTraderCountryCode
        }
      }
    }

    "movementType is anything else (e.g. ImportUnknownDestination)" should {

      s"return country code GB" in {

        requestWithMovement(MovementType.ImportUnknownDestination).messageRecipientCountryCode() shouldBe Constants.GB
      }
    }
  }

  s".messageSenderCountryCode()" when {

    Seq(MovementType.UKtoUK, MovementType.UKtoEU, MovementType.DirectExport, MovementType.IndirectExport).foreach { movementType =>

      s"movementType is $movementType" when {

        "placeOfDispatch is defined" should {

          s"return country code $placeOfDispatchTraderCountryCode from placeOfDispatchTrader" in {

            requestWithMovement(movementType).messageSenderCountryCode() shouldBe Some(placeOfDispatchTraderCountryCode)
          }
        }

        "placeOfDispatch is NOT defined" should {

          "when consignorERN is defined" should {

            s"return country code of consignor" in {

              requestWithMovement(movementType, hasPlaceOfDispatch = false).messageSenderCountryCode() shouldBe Some(consignorTraderCountryCode)
            }
          }

          "when consignorERN is NOT defined (should never happen)" should {

            s"return country code as None" in {

              requestWithMovement(movementType, hasPlaceOfDispatch = false, hasConsignorErn = false).messageSenderCountryCode() shouldBe None
            }
          }
        }
      }
    }

    "movementType is anything else" should {

      s"return country code $consignorTraderCountryCode from consignorTrader" in {

        requestWithMovement(MovementType.ImportDirectExport).messageSenderCountryCode() shouldBe Some(consignorTraderCountryCode)
      }
    }
  }

  "requestBody" when {

    "useFS41SchemaVersion is enabled" should {

      implicit val request = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = true, isChRISSubmission = true)

      "generate the correct request XML" in {

        val expectedSoapRequest =
          <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
            <soapenv:Header>
              <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
                <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
                <ns:VendorID>1259</ns:VendorID>
                <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
                <ns:ServiceID>1138</ns:ServiceID>
                <ns:ServiceMessageType>HMRC-EMCS-IE815-DIRECT</ns:ServiceMessageType>
              </ns:Info>
              <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
                <CredentialID>
                  {testCredId}
                </CredentialID>
                <Identifier>
                  {testErn}
                </Identifier>
              </MetaData>
            </soapenv:Header>
            <soapenv:Body>
              <urn:IE815 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE815:V3.13">
                <urn:Header>
                  <urn1:MessageSender>
                    {request.messageSender}
                  </urn1:MessageSender>
                  <urn1:MessageRecipient>
                    {request.messageRecipient}
                  </urn1:MessageRecipient>
                  <urn1:DateOfPreparation>
                    {request.preparedDate.toString}
                  </urn1:DateOfPreparation>
                  <urn1:TimeOfPreparation>
                    {request.preparedTime.toString}
                  </urn1:TimeOfPreparation>
                  <urn1:MessageIdentifier>
                    {request.messageUUID}
                  </urn1:MessageIdentifier>
                  <urn1:CorrelationIdentifier>
                    {request.legacyCorrelationUUID}
                  </urn1:CorrelationIdentifier>
                </urn:Header>
                <urn:Body>
                  {CreateMovementFixtures.createMovementXmlMax}
                </urn:Body>
              </urn:IE815>
            </soapenv:Body>
          </soapenv:Envelope>

        trim(XML.loadString(request.requestBody)).toString shouldBe trim(expectedSoapRequest).toString
      }
    }

    "useFS41SchemaVersion is disabled" should {

      "generate the correct request XML" in {

        val expectedSoapRequest =
          <soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
            <soapenv:Header>
              <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1">
                <ns:VendorName>EMCS_PORTAL_TFE</ns:VendorName>
                <ns:VendorID>1259</ns:VendorID>
                <ns:VendorProduct Version="2.0">HMRC Portal</ns:VendorProduct>
                <ns:ServiceID>1138</ns:ServiceID>
                <ns:ServiceMessageType>HMRC-EMCS-IE815-DIRECT</ns:ServiceMessageType>
              </ns:Info>
              <MetaData xmlns="http://www.hmrc.gov.uk/ChRIS/SOAP/MetaData/1">
                <CredentialID>
                  {testCredId}
                </CredentialID>
                <Identifier>
                  {testErn}
                </Identifier>
              </MetaData>
            </soapenv:Header>
            <soapenv:Body>
              <urn:IE815 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE815:V3.01">
                <urn:Header>
                  <urn1:MessageSender>
                    {request.messageSender}
                  </urn1:MessageSender>
                  <urn1:MessageRecipient>
                    {request.messageRecipient}
                  </urn1:MessageRecipient>
                  <urn1:DateOfPreparation>
                    {request.preparedDate.toString}
                  </urn1:DateOfPreparation>
                  <urn1:TimeOfPreparation>
                    {request.preparedTime.toString}
                  </urn1:TimeOfPreparation>
                  <urn1:MessageIdentifier>
                    {request.messageUUID}
                  </urn1:MessageIdentifier>
                  <urn1:CorrelationIdentifier>
                    {request.legacyCorrelationUUID}
                  </urn1:CorrelationIdentifier>
                </urn:Header>
                <urn:Body>
                  {CreateMovementFixtures.createMovementXmlMax}
                </urn:Body>
              </urn:IE815>
            </soapenv:Body>
          </soapenv:Envelope>

        trim(XML.loadString(request.requestBody)).toString shouldBe trim(expectedSoapRequest).toString
      }
    }
  }


  ".action" should {

    "be correct" in {

      request.action shouldBe "http://www.hmrc.gov.uk/emcs/submitdraftmovementportal"
    }
  }

  ".shouldExtractFromSoap" should {

    "be correct" in {

      request.shouldExtractFromSoap shouldBe false
    }
  }

  ".exciseRegistrationNumber" should {

    "be correct" in {

      request.exciseRegistrationNumber shouldBe testErn
    }
  }

  ".eisXMLBody" should {

    "useFS41SchemaVersion is enabled" should {

      implicit val request = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax, testDraftId, useFS41SchemaVersion = true, isChRISSubmission = false)

      "generate the correct XML body" in {

        val expectedRequest = {
          wrapInControlDoc(
            <urn:IE815 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.13" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE815:V3.13">
              <urn:Header>
                <urn1:MessageSender>
                  {request.messageSender}
                </urn1:MessageSender>
                <urn1:MessageRecipient>
                  {request.messageRecipient}
                </urn1:MessageRecipient>
                <urn1:DateOfPreparation>
                  {request.preparedDate.toString}
                </urn1:DateOfPreparation>
                <urn1:TimeOfPreparation>
                  {request.preparedTime.toString}
                </urn1:TimeOfPreparation>
                <urn1:MessageIdentifier>
                  {request.messageUUID}
                </urn1:MessageIdentifier>
                <urn1:CorrelationIdentifier>
                  {testDraftId}
                </urn1:CorrelationIdentifier>
              </urn:Header>
              <urn:Body>
                {CreateMovementFixtures.createMovementXmlMax}
              </urn:Body>
            </urn:IE815>)
        }


        val requestXml = XML.loadString(request.eisXMLBody())
        val expectedXml = trim(expectedRequest)

        requestXml shouldBe expectedXml
      }
    }

    "useFS41SchemaVersion is disabled" should {

      "generate the correct XML body" in {

        val expectedRequest = {
          wrapInControlDoc(
            <urn:IE815 xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01" xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE815:V3.01">
              <urn:Header>
                <urn1:MessageSender>
                  {request.messageSender}
                </urn1:MessageSender>
                <urn1:MessageRecipient>
                  {request.messageRecipient}
                </urn1:MessageRecipient>
                <urn1:DateOfPreparation>
                  {request.preparedDate.toString}
                </urn1:DateOfPreparation>
                <urn1:TimeOfPreparation>
                  {request.preparedTime.toString}
                </urn1:TimeOfPreparation>
                <urn1:MessageIdentifier>
                  {request.messageUUID}
                </urn1:MessageIdentifier>
                <urn1:CorrelationIdentifier>
                  {testDraftId}
                </urn1:CorrelationIdentifier>
              </urn:Header>
              <urn:Body>
                {CreateMovementFixtures.createMovementXmlMax}
              </urn:Body>
            </urn:IE815>)
        }


        val requestXml = XML.loadString(request.eisXMLBody())
        val expectedXml = trim(expectedRequest)

        requestXml shouldBe expectedXml
      }
    }
  }

  ".toJson" should {
    "create the correct JSON body" in {
      val expectedResult = Json.obj(
        "user" -> testErn,
        "messageType" -> "IE815",
        "message" -> Base64.getEncoder.encodeToString(XML.loadString(request.eisXMLBody()).toString().getBytes)
      )
      request.toJson shouldBe expectedResult
    }
  }

  def requestWithMovement(movement: MovementType,
                          destinationType: DestinationType = DestinationType.TaxWarehouse,
                          hasPlaceOfDispatch: Boolean = true,
                          hasConsignorErn: Boolean = true): SubmitCreateMovementRequest = {

    val request =
      CreateMovementFixtures.createMovementModelMax
        .copy(movementType = movement)
        .copy(headerEadEsad = HeaderEadEsadFixtures.headerEadEsadModel.copy(
          destinationType = destinationType
        ))
        .copy(consigneeTrader = CreateMovementFixtures.createMovementModelMax.consigneeTrader.map(
          _.copy(traderExciseNumber = Some(consigneeTraderCountryCode))
        ))
        .copy(complementConsigneeTrader = CreateMovementFixtures.createMovementModelMax.complementConsigneeTrader.map(
          _.copy(memberStateCode = complementConsigneeTraderCountryCode)
        ))
        .copy(deliveryPlaceTrader = CreateMovementFixtures.createMovementModelMax.deliveryPlaceTrader.map(
          _.copy(traderExciseNumber = Some(deliveryPlaceTraderCountryCode))
        ))
        .copy(deliveryPlaceCustomsOffice = CreateMovementFixtures.createMovementModelMax.deliveryPlaceCustomsOffice.map(
          _.copy(referenceNumber = deliveryPlaceCustomsOfficeCountryCode)
        ))
        .copy(consignorTrader = CreateMovementFixtures.createMovementModelMax.consignorTrader
          .copy(traderExciseNumber = Option.when(hasConsignorErn)(consignorTraderCountryCode))
        )

    SubmitCreateMovementRequest(
      if(hasPlaceOfDispatch) {
        request.copy(placeOfDispatchTrader = CreateMovementFixtures.createMovementModelMax.placeOfDispatchTrader.map(
          _.copy(traderExciseNumber = Some(placeOfDispatchTraderCountryCode))
        ))
      } else {
        request.copy(placeOfDispatchTrader = None)
      },
      draftId = testDraftId,
      useFS41SchemaVersion = false,
      isChRISSubmission = false
    )
  }
}
