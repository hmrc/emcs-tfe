/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.fixtures

import uk.gov.hmrc.emcstfe.models.response.TraderKnownFacts

trait TraderKnownFactsFixtures {

  val traderKnownFactsCandEJson: String =
    """
      |{
      |  "traderName": "SEED TRADER 1629",
      |  "addressLine1": "629 High Street",
      |  "addressLine2": "Any Suburb",
      |  "addressLine3": "Any Town",
      |  "addressLine4": "Any County",
      |  "addressLine5": "UK",
      |  "postcode": "SS1 99AA"
      |}
      |""".stripMargin

  val traderKnownFactsETDSJson: String =
    """
      |{
      |  "businessName": "SEED TRADER 1629",
      |  "addressLine1": "629 High Street",
      |  "addressLine2": "Any Suburb",
      |  "addressLine3": "Any Town",
      |  "addressLine4": "Any County",
      |  "addressLine5": "UK",
      |  "postCode": "SS1 99AA"
      |}
      |""".stripMargin

  val testTraderKnownFactsJson: String =
    """
      |{
      |  "traderName": "SEED TRADER 1629",
      |  "addressLine1": "629 High Street",
      |  "addressLine2": "Any Suburb",
      |  "addressLine3": "Any Town",
      |  "addressLine4": "Any County",
      |  "addressLine5": "UK",
      |  "postcode": "SS1 99AA"
      |}
      |""".stripMargin

  val testTraderKnownFactsModel: TraderKnownFacts = TraderKnownFacts(
    traderName = "SEED TRADER 1629",
    addressLine1 = Some("629 High Street"),
    addressLine2 = Some("Any Suburb"),
    addressLine3 = Some("Any Town"),
    addressLine4 = Some("Any County"),
    addressLine5 = Some("UK"),
    postcode = Some("SS1 99AA")
  )
}
