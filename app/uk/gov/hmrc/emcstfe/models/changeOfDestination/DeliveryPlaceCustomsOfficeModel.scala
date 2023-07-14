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

package uk.gov.hmrc.emcstfe.models.changeOfDestination

import play.api.libs.json.{Json, Reads}

import scala.xml.Elem

case class DeliveryPlaceCustomsOfficeModel(
                                            referenceNumber: Seq[String]
                                          ) extends ChangeOfDestinationModel {
  assume(referenceNumber.nonEmpty, "referenceNumber.length must be > 0")
  def toXml: Elem = <urn:DeliveryPlaceCustomsOffice>
    {referenceNumber.map(value => <urn:ReferenceNumber>{value}</urn:ReferenceNumber>)}
  </urn:DeliveryPlaceCustomsOffice>
}

object DeliveryPlaceCustomsOfficeModel {
  implicit val reads: Reads[DeliveryPlaceCustomsOfficeModel] = Json.reads
}
