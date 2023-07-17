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

import uk.gov.hmrc.emcstfe.models.reportOfReceipt.ConsignorTraderModel

import scala.xml.NodeSeq

trait ConsignorTraderModelFixtures extends BaseFixtures with AddressModelFixtures {

  val mandatoryAddressModelForConsignor = maxAddressModel

  val mandatoryAddressModelXMLForConsignor = maxAddressModelXML


  val consignorTraderModel = ConsignorTraderModel(
    traderExciseNumber = "GB0000000012346",
    traderName = "name",
    address = mandatoryAddressModelForConsignor
  )

  val consignorTraderModelXML =
    NodeSeq.fromSeq(Seq(
      <urn:TraderExciseNumber>GB0000000012346</urn:TraderExciseNumber>,
      <urn:TraderName>name</urn:TraderName>,
      mandatoryAddressModelXMLForConsignor
    ).flatten)

}
