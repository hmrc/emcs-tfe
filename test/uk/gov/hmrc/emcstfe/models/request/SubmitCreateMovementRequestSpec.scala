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

import uk.gov.hmrc.emcstfe.config.Constants
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, MovementType}
import uk.gov.hmrc.emcstfe.support.UnitSpec

class SubmitCreateMovementRequestSpec extends UnitSpec with CreateMovementFixtures {

  val consigneeTraderCountryCode = "AA"
  val placeOfDispatchTraderCountryCode = "BB"
  val complementConsigneeTraderCountryCode = "CC"
  val deliveryPlaceTraderCountryCode = "DD"
  val deliveryPlaceCustomsOfficeCountryCode = "EE"
  val consignorTraderCountryCode = "FF"

  val request = SubmitCreateMovementRequest(CreateMovementFixtures.createMovementModelMax)

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

            requestWithMovement(movementType).messageSenderCountryCode() shouldBe placeOfDispatchTraderCountryCode
          }
        }

        "placeOfDispatch is NOT defined" should {

          s"return country code GB" in {

            requestWithMovement(movementType, hasPlaceOfDispatch = false).messageSenderCountryCode() shouldBe Constants.GB
          }
        }
      }
    }

    "movementType is anything else" should {

      s"return country code $consignorTraderCountryCode from consignorTrader" in {

        requestWithMovement(MovementType.ImportDirectExport).messageSenderCountryCode() shouldBe consignorTraderCountryCode
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

  def requestWithMovement(movement: MovementType,
                          destinationType: DestinationType = DestinationType.TaxWarehouse,
                          hasPlaceOfDispatch: Boolean = true): SubmitCreateMovementRequest = {

    val request =
      CreateMovementFixtures.createMovementModelMax
        .copy(movementType = movement)
        .copy(headerEadEsad = HeaderEadEsadFixtures.headerEadEsadModel.copy(
          destinationType = destinationType
        ))
        .copy(consigneeTrader = CreateMovementFixtures.createMovementModelMax.consigneeTrader.map(
          _.copy(traderId = Some(consigneeTraderCountryCode))
        ))
        .copy(complementConsigneeTrader = CreateMovementFixtures.createMovementModelMax.complementConsigneeTrader.map(
          _.copy(memberStateCode = complementConsigneeTraderCountryCode)
        ))
        .copy(deliveryPlaceTrader = CreateMovementFixtures.createMovementModelMax.deliveryPlaceTrader.map(
          _.copy(traderId = Some(deliveryPlaceTraderCountryCode))
        ))
        .copy(deliveryPlaceCustomsOffice = CreateMovementFixtures.createMovementModelMax.deliveryPlaceCustomsOffice.map(
          _.copy(referenceNumber = deliveryPlaceCustomsOfficeCountryCode)
        ))
        .copy(consignorTrader = CreateMovementFixtures.createMovementModelMax.consignorTrader
          .copy(traderExciseNumber = consignorTraderCountryCode)
        )

    SubmitCreateMovementRequest(
      if(!hasPlaceOfDispatch) request else {
        request.copy(placeOfDispatchTrader = CreateMovementFixtures.createMovementModelMax.placeOfDispatchTrader.map(
          _.copy(traderId = Some(placeOfDispatchTraderCountryCode))
        ))
      }
    )
  }
}
