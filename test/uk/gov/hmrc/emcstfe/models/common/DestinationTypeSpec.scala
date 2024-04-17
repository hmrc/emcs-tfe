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

import uk.gov.hmrc.emcstfe.models.common.DestinationType._
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

class DestinationTypeSpec extends TestBaseSpec {

  "DestinationType" should {

    "have the correct codes" in {
      TaxWarehouse.toString shouldBe "1"
      RegisteredConsignee.toString shouldBe "2"
      TemporaryRegisteredConsignee.toString shouldBe "3"
      DirectDelivery.toString shouldBe "4"
      ExemptedOrganisations.toString shouldBe "5"
      Export.toString shouldBe "6"
      UnknownDestination.toString shouldBe "8"
      CertifiedConsignee.toString shouldBe "9"
      TemporaryCertifiedConsignee.toString shouldBe "10"
      ReturnToThePlaceOfDispatchOfTheConsignor.toString shouldBe "11"
    }

    "have the correct MovementScenarios" in {
      TaxWarehouse.movementScenarios shouldBe Seq("euTaxWarehouse", "gbTaxWarehouse")
      RegisteredConsignee.movementScenarios shouldBe Seq("registeredConsignee")
      TemporaryRegisteredConsignee.movementScenarios shouldBe Seq("temporaryRegisteredConsignee")
      DirectDelivery.movementScenarios shouldBe Seq("directDelivery")
      ExemptedOrganisations.movementScenarios shouldBe Seq("exemptedOrganisation")
      Export.movementScenarios shouldBe Seq("exportWithCustomsDeclarationLodgedInTheUk", "exportWithCustomsDeclarationLodgedInTheEu")
      UnknownDestination.movementScenarios shouldBe Seq("unknownDestination")
      CertifiedConsignee.movementScenarios shouldBe Seq("certifiedConsignee")
      TemporaryCertifiedConsignee.movementScenarios shouldBe Seq("temporaryCertifiedConsignee")
      ReturnToThePlaceOfDispatchOfTheConsignor.movementScenarios shouldBe Seq()
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query param is supplied" in {
        DestinationType.queryStringBinder.bind("destinationType", Map()) shouldBe None
      }

      "valid query param is supplied (single value)" in {
        DestinationType.queryStringBinder.bind("destinationType", Map(
          "destinationType" -> Seq("1")
        )) shouldBe Some(Right(Seq(TaxWarehouse)))
      }

      "fail if binding an invalid query param" in {
        DestinationType.queryStringBinder.bind("destinationType", Map(
          "destinationType" -> Seq("99")
        )) shouldBe Some(Left("Destination code of '99' could not be mapped to a valid Destination Type"))
      }

      "valid query param is supplied (all values)" in {
        DestinationType.queryStringBinder.bind("destinationType", Map(
          "destinationType" -> DestinationType.values.map(_.toString)
        )) shouldBe Some(Right(DestinationType.values))
      }
    }

    "unbind QueryString to URL format" in {
      DestinationType.queryStringBinder.unbind("destinationType", DestinationType.values) shouldBe
        DestinationType.values.map("destinationType=" + _.toString).mkString("&")
    }
  }
}
