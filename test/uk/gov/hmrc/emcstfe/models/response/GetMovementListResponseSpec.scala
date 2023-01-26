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

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess, __}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.support.UnitSpec
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader.LocalDateTimeParseFailure

import scala.xml.{Elem, XML}

class GetMovementListResponseSpec extends UnitSpec with GetMovementListFixture {

  "writes" should {
    "write a model to JSON" in {
      Json.toJson(getMovementListResponse) shouldBe getMovementListJson
    }
  }

  ".xmlReads" should {

    "successfully read a movement list response" when {

      "all fields are valid" in {
        GetMovementListResponse.xmlReader.read(XML.loadString(getMovementListXMLResponseBody)) shouldBe ParseSuccess(getMovementListResponse)
      }

      "there are no Movements returned" in {

        GetMovementListResponse.xmlReader.read(noMovements) shouldBe ParseSuccess(GetMovementListResponse(movements = Seq(), count = 0))
      }
    }

    "fail to read a movement list response" when {

      "Count of documents is missing" in {

        val badXml: Elem =
          <MovementListDataResponse>
          </MovementListDataResponse>

        GetMovementListResponse.xmlReader.read(badXml) shouldBe ParseFailure(Seq(EmptyError(__ \ "CountOfMovementsAvailable")))
      }

      "One of the Movement is invalid" in {

        val badXml: Elem =
          <MovementListDataResponse>
            <Movement>
              <Arc>18GB00000000000232361</Arc>
              <DateOfDispatch>2009-01-26T14:11:00BANG</DateOfDispatch>
              <MovementStatus>Accepted</MovementStatus>
              <OtherTraderID>ABCD1234</OtherTraderID>
            </Movement>
            <Movement>
              <Arc>18GB00000000000232362</Arc>
              <MovementStatus>Accepted</MovementStatus>
              <OtherTraderID>ABCD1234</OtherTraderID>
            </Movement>
            <CountOfMovementsAvailable>1</CountOfMovementsAvailable>
          </MovementListDataResponse>

        GetMovementListResponse.xmlReader.read(badXml) shouldBe ParseFailure(Seq(LocalDateTimeParseFailure("Text '' could not be parsed at index 0")))
      }
    }
  }
}
