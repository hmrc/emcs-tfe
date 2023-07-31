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
import uk.gov.hmrc.emcstfe.models.common.GuarantorType.ConsignorOwner
import uk.gov.hmrc.emcstfe.models.common.{GuarantorTrader, MovementGuaranteeModel}

import scala.xml.Elem

trait MovementGuaranteeFixtures extends BaseFixtures with AddressModelFixtures with TraderModelFixtures {

  lazy val maxMovementGuaranteeModel: MovementGuaranteeModel = MovementGuaranteeModel(
    guarantorTypeCode = ConsignorOwner,
    guarantorTrader = Some(Seq(
      maxTraderModel(GuarantorTrader),
      maxTraderModel(GuarantorTrader)
    ))
  )

  lazy val maxMovementGuaranteeXml: Elem = <urn:MovementGuarantee>
    <urn:GuarantorTypeCode>{ConsignorOwner.toString}</urn:GuarantorTypeCode>
    <urn:GuarantorTrader language="en">
      {maxTraderModelXML(GuarantorTrader)}
    </urn:GuarantorTrader>
    <urn:GuarantorTrader language="en">
      {maxTraderModelXML(GuarantorTrader)}
    </urn:GuarantorTrader>
  </urn:MovementGuarantee>

  lazy val maxMovementGuaranteeJson: JsObject = Json.obj(
    "guarantorTypeCode" -> ConsignorOwner.toString,
    "guarantorTrader" -> Json.arr(
      maxTraderModelJson(GuarantorTrader),
      maxTraderModelJson(GuarantorTrader)
    )
  )


  lazy val minMovementGuaranteeModel: MovementGuaranteeModel = MovementGuaranteeModel(
    guarantorTypeCode = ConsignorOwner,
    guarantorTrader = None
  )

  lazy val minMovementGuaranteeXml: Elem = <urn:MovementGuarantee>
    <urn:GuarantorTypeCode>{ConsignorOwner.toString}</urn:GuarantorTypeCode>
  </urn:MovementGuarantee>

  lazy val minMovementGuaranteeJson: JsObject = Json.obj(
    "guarantorTypeCode" -> ConsignorOwner.toString
  )

}
