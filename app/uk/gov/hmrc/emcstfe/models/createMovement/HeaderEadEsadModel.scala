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
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, JourneyTime, TransportArrangement, XmlBaseModel}

import scala.xml.Elem

case class HeaderEadEsadModel(
                               destinationType: DestinationType,
                               journeyTime: JourneyTime,
                               transportArrangement: TransportArrangement
                             ) extends XmlBaseModel {
  def toXml(implicit request: UserRequest[_]): Elem = <urn:HeaderEadEsad>
    <urn:DestinationTypeCode>{destinationType}</urn:DestinationTypeCode>
    <urn:JourneyTime>{journeyTime.toDownstream}</urn:JourneyTime>
    <urn:TransportArrangement>{transportArrangement.toString}</urn:TransportArrangement>
  </urn:HeaderEadEsad>
}

object HeaderEadEsadModel {
  implicit val fmt: OFormat[HeaderEadEsadModel] = Json.format
}
