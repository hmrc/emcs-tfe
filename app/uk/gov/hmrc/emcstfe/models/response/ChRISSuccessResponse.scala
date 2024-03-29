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

import cats.implicits.catsSyntaxTuple4Semigroupal
import com.google.common.io.BaseEncoding
import com.lucidchart.open.xtract.{XPath, XmlReader, __}
import play.api.libs.json.{Json, OWrites}

import java.nio.charset.StandardCharsets
import java.util.Base64

case class ChRISSuccessResponse(receipt: String,
                                receiptDate: String,
                                lrn: Option[String] = None,
                                submittedDraftId: Option[String] = None)

object ChRISSuccessResponse {

  private[response] def digestValueToReceipt(dv: String): String = {
    val decodedValue: Array[Byte] = Base64.getDecoder.decode(dv.getBytes(StandardCharsets.UTF_8))
    val stringValue: String = BaseEncoding.base32().encode(decodedValue)
    stringValue
  }

  val digestValue: XPath = __ \\ "Envelope" \ "Body" \ "HMRCSOAPResponse" \ "SuccessResponse" \ "IRmarkReceipt" \ "Signature" \ "SignedInfo" \ "Reference" \ "DigestValue"

  val receiptDateTime: XPath = __ \\ "Envelope" \ "Body" \ "HMRCSOAPResponse" \ "SuccessResponse" \ "AcceptedTime"

  implicit val xmlReader: XmlReader[ChRISSuccessResponse] =
    (
      digestValue.read[String].map(digestValueToReceipt),
      receiptDateTime.read[String],
      digestValue.read[String].map(_ => None), // can't think of a way to Reads.pure with xtract. TODO: investigate
      XmlReader.stringReader.map(_ => None)
    ).mapN(ChRISSuccessResponse.apply)

  implicit val writes: OWrites[ChRISSuccessResponse] = Json.writes
}
