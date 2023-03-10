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

package uk.gov.hmrc.emcstfe.models.response

import cats.implicits.catsSyntaxTuple7Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OWrites}

case class MovementItem(itemUniqueReference: Int,
                        productCode: String,
                        cnCode: String,
                        quantity: BigDecimal,
                        grossMass: BigDecimal,
                        netMass: BigDecimal,
                        alcoholicStrength: Option[BigDecimal])
object MovementItem {

  implicit val xmlReader: XmlReader[MovementItem] = (
    (__ \ "BodyRecordUniqueReference").read[Int],
    (__ \ "ExciseProductCode").read[String],
    (__ \ "CnCode").read[String],
    (__ \ "Quantity").read[String].map(BigDecimal(_)),
    (__ \ "GrossMass").read[String].map(BigDecimal(_)),
    (__ \ "NetMass").read[String].map(BigDecimal(_)),
    (__ \ "AlcoholicStrengthByVolumeInPercentage").read[String].map(BigDecimal(_)).optional,
  ).mapN(MovementItem.apply)

  implicit val writes: OWrites[MovementItem] = Json.writes
}
