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

package uk.gov.hmrc.emcstfe.fixtures

import uk.gov.hmrc.emcstfe.models.request.reportOfReceipt.{AddressModel, TraderModel}

import scala.xml.NodeSeq

trait TraderModelFixtures extends BaseFixtures {

  val maxTraderModel = TraderModel(
    traderId = Some("id"),
    traderName = Some("name"),
    address = Some(AddressModel(
      streetNumber = Some("street"),
      street = Some("streetNumber"),
      postcode = Some("postcode"),
      city = Some("city")
    )),
    eoriNumber = Some("eori")
  )

  val maxTraderModelXML =
    NodeSeq.fromSeq(Seq(
      <Traderid>id</Traderid>,
      <TraderName>name</TraderName>,
      <StreetName>streetNumber</StreetName>,
      <StreetNumber>street</StreetNumber>,
      <Postcode>postcode</Postcode>,
      <City>city</City>,
      <EoriNumber>eori</EoriNumber>
    ))

  val minTraderModel = TraderModel(
    traderId = None,
    traderName = None,
    address = Some(AddressModel(
      streetNumber = None,
      street = None,
      postcode = None,
      city = None
    )),
    eoriNumber = None
  )

  val minTraderModelXML = NodeSeq.Empty

}
