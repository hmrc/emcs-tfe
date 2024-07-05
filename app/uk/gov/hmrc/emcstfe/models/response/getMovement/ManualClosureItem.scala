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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import cats.implicits.catsSyntaxTuple6Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

case class ManualClosureItem(bodyRecordUniqueReference: Int,
                             productCode: Option[String],
                             indicatorOfShortageOrExcess: Option[String],
                             observedShortageOrExcess: Option[BigDecimal],
                             refusedQuantity: Option[BigDecimal],
                             complementaryInformation: Option[String],
                            )

object ManualClosureItem extends XmlWriterUtils {

  implicit val xmlReads: XmlReader[ManualClosureItem] = (
    (__ \ "BodyRecordUniqueReference").read[Int],
    (__ \ "ExciseProductCode").read[String].optional,
    (__ \ "IndicatorOfShortageOrExcess").read[String].optional,
    (__ \ "ObservedShortageOrExcess").read[String].map(BigDecimal(_)).optional,
    (__ \ "RefusedQuantity").read[String].map(BigDecimal(_)).optional,
    (__ \ "ComplementaryInformation").read[String].optional
  ).mapN(ManualClosureItem.apply)

  implicit val format: OFormat[ManualClosureItem] = Json.format
}
