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

import cats.implicits.catsSyntaxTuple5Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.{DestinationType, JourneyTime, TransportArrangement}

case class HeaderEadEsadModel(
    sequenceNumber: Int,
    dateAndTimeOfUpdateValidation: String,
    destinationType: DestinationType,
    journeyTime: String,
    transportArrangement: TransportArrangement
)

object HeaderEadEsadModel {

  implicit val xmlReads: XmlReader[HeaderEadEsadModel] = (
    (__ \\ "SequenceNumber").read[Int],
    (__ \\ "DateAndTimeOfUpdateValidation").read[String],
    (__ \\ "DestinationTypeCode").read[DestinationType](DestinationType.xmlReads("HeaderEadEsad/DestinationTypeCode")(DestinationType.enumerable)),
    (__ \\ "JourneyTime").read[JourneyTime](JourneyTime.xmlReads).map(_.toString),
    (__ \\ "TransportArrangement").read[TransportArrangement](TransportArrangement.xmlReads("HeaderEadEsad/TransportArrangement")(TransportArrangement.enumerable))
  ).mapN(HeaderEadEsadModel.apply)

  implicit val fmt: OFormat[HeaderEadEsadModel] = Json.format
}
