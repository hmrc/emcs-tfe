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

import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, MovementType}

class SubmitCreateMovementRequestSpec extends UnitSpec with CreateMovementFixtures {

  lazy implicit val userRequest: UserRequest[_] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)

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

        requestWithMovement(MovementType.UKtoUK).messageRecipientCountryCode() shouldBe Some(deliveryPlaceTraderCountryCode)
      }
    }

    "movementType is ImportUk" should {

      s"return country code $deliveryPlaceTraderCountryCode from deliveryPlaceTrader" in {

        requestWithMovement(MovementType.ImportUK).messageRecipientCountryCode() shouldBe Some(deliveryPlaceTraderCountryCode)
      }
    }

    "movementType is UKtoEU" when {

      "destinationType is ExemptedOrganisations" should {

        s"return country code $complementConsigneeTraderCountryCode from complementConsigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.ExemptedOrganisations).messageRecipientCountryCode() shouldBe Some(complementConsigneeTraderCountryCode)
        }
      }

      "destinationType is TaxWarehouse" should {

        s"return country code $deliveryPlaceTraderCountryCode from deliveryPlaceTrader" in {

          requestWithMovement(MovementType.UKtoEU).messageRecipientCountryCode() shouldBe Some(deliveryPlaceTraderCountryCode)
        }
      }

      "destinationType is RegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.RegisteredConsignee).messageRecipientCountryCode() shouldBe Some(consigneeTraderCountryCode)
        }
      }

      "destinationType is TemporaryRegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.TemporaryRegisteredConsignee).messageRecipientCountryCode() shouldBe Some(consigneeTraderCountryCode)
        }
      }

      "destinationType is DirectDelivery" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.DirectDelivery).messageRecipientCountryCode() shouldBe Some(consigneeTraderCountryCode)
        }
      }

      "destinationType is Export" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.Export).messageRecipientCountryCode() shouldBe Some(consigneeTraderCountryCode)
        }
      }

      "destinationType is UnknownDestination" should {

        s"return country code $placeOfDispatchTraderCountryCode from placeOfDispatchTrader" in {

          requestWithMovement(MovementType.UKtoEU, DestinationType.UnknownDestination).messageRecipientCountryCode() shouldBe Some(placeOfDispatchTraderCountryCode)
        }
      }
    }

    "movementType is DirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.DirectExport).messageRecipientCountryCode() shouldBe Some(deliveryPlaceCustomsOfficeCountryCode)
      }
    }

    "movementType is IndirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.IndirectExport).messageRecipientCountryCode() shouldBe Some(deliveryPlaceCustomsOfficeCountryCode)
      }
    }

    "movementType is ImportDirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.ImportDirectExport).messageRecipientCountryCode() shouldBe Some(deliveryPlaceCustomsOfficeCountryCode)
      }
    }

    "movementType is ImportIndirectExport" should {

      s"return country code $deliveryPlaceCustomsOfficeCountryCode from deliveryPlaceCustomsOffice" in {

        requestWithMovement(MovementType.ImportIndirectExport).messageRecipientCountryCode() shouldBe Some(deliveryPlaceCustomsOfficeCountryCode)
      }
    }

    "movementType is ImportEU" when {

      "destinationType is TaxWarehouse" should {

        s"return country code $deliveryPlaceTraderCountryCode from deliveryPlaceTrader " in {

          requestWithMovement(MovementType.ImportEU).messageRecipientCountryCode() shouldBe Some(deliveryPlaceTraderCountryCode)
        }
      }

      "destinationType is ExemptedOrganisations" should {

        s"return country code $complementConsigneeTraderCountryCode from complementConsigneeTrader" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.ExemptedOrganisations).messageRecipientCountryCode() shouldBe Some(complementConsigneeTraderCountryCode)
        }
      }

      "destinationType is DirectDelivery" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.DirectDelivery).messageRecipientCountryCode() shouldBe Some(consigneeTraderCountryCode)
        }
      }

      "destinationType is RegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTraderCountryCode" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.RegisteredConsignee).messageRecipientCountryCode() shouldBe Some(consigneeTraderCountryCode)
        }
      }

      "destinationType is TemporaryRegisteredConsignee" should {

        s"return country code $consigneeTraderCountryCode from consigneeTrader" in {

          requestWithMovement(MovementType.ImportEU, DestinationType.TemporaryRegisteredConsignee).messageRecipientCountryCode() shouldBe Some(consigneeTraderCountryCode)
        }
      }
    }
  }

  s".messageSenderCountryCode()" when {

    Seq(MovementType.UKtoUK, MovementType.UKtoEU, MovementType.DirectExport, MovementType.IndirectExport).foreach { movementType =>

      s"movementType is $movementType" should {

        s"return country code $placeOfDispatchTraderCountryCode from placeOfDispatchTrader" in {

          requestWithMovement(movementType).messageSenderCountryCode() shouldBe Some(placeOfDispatchTraderCountryCode)
        }
      }
    }

    "movementType is anything else" should {

      s"return country code $consignorTraderCountryCode from consignorTrader" in {

        requestWithMovement(MovementType.ImportDirectExport).messageSenderCountryCode() shouldBe Some(consignorTraderCountryCode)
      }
    }
  }


  "action" should {

    "be correct" in {

      request.action shouldBe "http://www.hmrc.gov.uk/emcs/submitdraftmovementportal"
    }
  }

  "shouldExtractFromSoap" should {

    "be correct" in {

      request.shouldExtractFromSoap shouldBe false
    }
  }

  def requestWithMovement(movement: MovementType,
                          destinationType: DestinationType = DestinationType.TaxWarehouse): SubmitCreateMovementRequest =
    SubmitCreateMovementRequest(
      CreateMovementFixtures.createMovementModelMax
        .copy(movementType = movement)
        .copy(headerEadEsad = HeaderEadEsadFixtures.headerEadEsadModel.copy(
          destinationType = destinationType
        ))
        .copy(consigneeTrader = CreateMovementFixtures.createMovementModelMax.consigneeTrader.map(
          _.copy(traderId = Some(consigneeTraderCountryCode))
        ))
        .copy(placeOfDispatchTrader = CreateMovementFixtures.createMovementModelMax.placeOfDispatchTrader.map(
          _.copy(traderId = Some(placeOfDispatchTraderCountryCode))
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
    )
}
