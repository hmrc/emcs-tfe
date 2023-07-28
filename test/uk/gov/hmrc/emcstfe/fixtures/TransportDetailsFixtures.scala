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

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.models.common.TransportDetailsModel

import scala.xml.Elem

trait TransportDetailsFixtures extends BaseFixtures with AddressModelFixtures {

  lazy val maxTransportDetailsModel: TransportDetailsModel = TransportDetailsModel(
    transportUnitCode = "code",
    identityOfTransportUnits = Some("units"),
    commercialSealIdentification = Some("id"),
    complementaryInformation = Some("comp info"),
    sealInformation = Some("seal info")
  )

  lazy val maxTransportDetailsXml: Elem = <urn:TransportDetails>
    <urn:TransportUnitCode>code</urn:TransportUnitCode>
    <urn:IdentityOfTransportUnits>units</urn:IdentityOfTransportUnits>
    <urn:CommercialSealIdentification>id</urn:CommercialSealIdentification>
    <urn:ComplementaryInformation language="en">comp info</urn:ComplementaryInformation>
    <urn:SealInformation language="en">seal info</urn:SealInformation>
  </urn:TransportDetails>

  lazy val maxTransportDetailsJson: JsObject = Json.obj(
    "transportUnitCode" -> "code",
    "identityOfTransportUnits" -> "units",
    "commercialSealIdentification" -> "id",
    "complementaryInformation" -> "comp info",
    "sealInformation" -> "seal info"
  )


  lazy val minTransportDetailsModel: TransportDetailsModel = TransportDetailsModel(
    transportUnitCode = "code",
    identityOfTransportUnits = None,
    commercialSealIdentification = None,
    complementaryInformation = None,
    sealInformation = None
  )

  lazy val minTransportDetailsXml: Elem = <urn:TransportDetails>
    <urn:TransportUnitCode>code</urn:TransportUnitCode>
  </urn:TransportDetails>

  lazy val minTransportDetailsJson: JsObject = Json.obj(
    "transportUnitCode" -> "code"
  )

}
