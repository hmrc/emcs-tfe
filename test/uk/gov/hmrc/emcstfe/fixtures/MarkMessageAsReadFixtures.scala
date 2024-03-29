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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.MarkMessageAsReadResponse
import uk.gov.hmrc.emcstfe.utils.SoapXmlFactory

import scala.xml.Elem

trait MarkMessageAsReadFixtures extends BaseFixtures with SoapXmlFactory {


  lazy val markMessageAsReadChrisXml: String = responseSoapEnvelope(
    <Results>
      <Result Name="schema">&lt;recordsAffected&gt;1&lt;/recordsAffected&gt;</Result>
    </Results>
  )

  lazy val markMessageAsReadParsedChrisXml: Elem =
    <Results>
      <Result Name="schema"><recordsAffected>1</recordsAffected></Result>
    </Results>

  val markMessageAsReadEisJson: JsValue = Json.obj(
    "dateTime" -> now,
    "exciseRegistrationNumber" -> testErn,
    "recordsAffected" -> 1
  )
  val markMessageAsReadResponseModel: MarkMessageAsReadResponse = MarkMessageAsReadResponse(
    recordsAffected = 1
  )
  val markMessageAsReadJson: JsValue = Json.obj(
    "recordsAffected" -> 1
  )
}
