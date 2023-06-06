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

import cats.implicits.catsSyntaxTuple5Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}

case class WineProduct(wineProductCategory: String,
                       wineGrowingZoneCode: Option[String],
                       thirdCountryOfOrigin: Option[String],
                       otherInformation: Option[String],
                       wineOperations: Option[Seq[String]]
                      )

object WineProduct {

  implicit val xmlReader: XmlReader[WineProduct] = (
    (__ \ "WineProductCategory").read[String],
    (__ \ "WineGrowingZoneCode").read[String].optional,
    (__ \ "ThirdCountryOfOrigin").read[String].optional,
    (__ \ "OtherInformation").read[String].optional,
    (__ \\ "WineOperation" \\ "WineOperationCode").read[Seq[String]](strictReadSeq).optional,
  ).mapN(WineProduct.apply)

  implicit val format: OFormat[WineProduct] = Json.format
}
