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

package uk.gov.hmrc.emcstfe.models.response.getMessages

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.XmlReader.strictReadSeq
import com.lucidchart.open.xtract.{XPath, XmlReader, __ => XMLPath}
import play.api.libs.json._
import uk.gov.hmrc.emcstfe.models.response.Base64Model

case class GetMessagesResponse(messages: Seq[Message],
                               totalNumberOfMessagesAvailable: Long)

object GetMessagesResponse {
  implicit val writes: OWrites[GetMessagesResponse] = Json.writes

  private val message: XPath = XMLPath \\ "MessagesDataResponse" \\ "Message"
  private val totalNumberOfMessagesAvailable: XPath = XMLPath \\ "MessagesDataResponse" \\ "TotalNumberOfMessagesAvailable"

  //XML Reads (also used by EIS JSON reads below as an implicit XMLReader is required to Base64Model)
  implicit val xmlReader: XmlReader[GetMessagesResponse] = (
    message.read[Seq[Message]](strictReadSeq),
    totalNumberOfMessagesAvailable.read[Long],
  ).mapN(GetMessagesResponse.apply)

  //EIS Reads
  implicit val reads: Reads[GetMessagesResponse] =
    (__ \ "message").read[Base64Model[GetMessagesResponse]].map(_.value)
}
