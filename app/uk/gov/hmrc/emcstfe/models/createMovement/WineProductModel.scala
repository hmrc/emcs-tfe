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

package uk.gov.hmrc.emcstfe.models.createMovement
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class WineProductModel(
                             wineProductCategory: String,
                             wineGrowingZoneCode: Option[String],
                             thirdCountryOfOrigin: Option[String],
                             otherInformation: Option[String],
                             wineOperations: Option[Seq[String]]
                           ) extends XmlBaseModel with XmlWriterUtils {
  def toXml: Elem = <urn:WineProduct>
    <urn:WineProductCategory>{wineProductCategory}</urn:WineProductCategory>
    {wineGrowingZoneCode.mapNodeSeq(value => <urn:WineGrowingZoneCode>{value}</urn:WineGrowingZoneCode>)}
    {thirdCountryOfOrigin.mapNodeSeq(value => <urn:ThirdCountryOfOrigin>{value}</urn:ThirdCountryOfOrigin>)}
    {otherInformation.mapNodeSeq(value => <urn:OtherInformation language="en">{value}</urn:OtherInformation>)}
    {wineOperations.mapNodeSeq(_.map(value => <urn:WineOperation>{value}</urn:WineOperation>))}
  </urn:WineProduct>
}

object WineProductModel {
  implicit val fmt: OFormat[WineProductModel] = Json.format
}
