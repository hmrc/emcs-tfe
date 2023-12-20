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
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.InstantXMLReader.InstantParseFailure
import uk.gov.hmrc.emcstfe.utils.LocalDateTimeXMLReader.LocalDateTimeParseFailure

import scala.xml.Elem

class GetMovementListItemSpec extends TestBaseSpec with GetMovementListFixture {

  ".writes" should {
    "write a model to JSON" in {
      Json.toJson(movement1) shouldBe movement1Json
    }
  }

  ".xmlReads" should {

    "successfully read a movement list item" when {

      "all fields are valid (DateOfDispatch in LocalDateTime format)" in {
        GetMovementListItem.xmlReader.read(movement1XML) shouldBe ParseSuccess(movement1)
      }

      "all fields are valid (DateOfDispatch in Instant format)" in {
        GetMovementListItem.xmlReader.read(movement1XMLWithoutZulu) shouldBe ParseSuccess(movement1)
      }
    }

    "fail to read movement list item" when {

      "Arc is missing" in {

        val badXml: Elem =
          <Movement>
            <DateOfDispatch>2009-01-26T14:11:00</DateOfDispatch>
            <MovementStatus>Accepted</MovementStatus>
            <OtherTraderID>ABCD1234</OtherTraderID>
          </Movement>

        GetMovementListItem.xmlReader.read(badXml) shouldBe ParseFailure(Seq(EmptyError(__ \ "Arc")))
      }

      "DateOfDispatch is missing" in {

        val badXml: Elem =
          <Movement>
            <Arc>18GB00000000000232361</Arc>
            <MovementStatus>Accepted</MovementStatus>
            <OtherTraderID>ABCD1234</OtherTraderID>
          </Movement>

        GetMovementListItem.xmlReader.read(badXml) shouldBe ParseFailure(Seq(
          LocalDateTimeParseFailure("Text '' could not be parsed at index 0"),
          InstantParseFailure("Text '' could not be parsed at index 0")
        ))
      }

      "DateOfDispatch is invalid date format" in {

        val badXml: Elem =
          <Movement>
            <Arc>18GB00000000000232361</Arc>
            <DateOfDispatch>2009-01-26T14:11:00BANG</DateOfDispatch>
            <MovementStatus>Accepted</MovementStatus>
            <OtherTraderID>ABCD1234</OtherTraderID>
          </Movement>

        GetMovementListItem.xmlReader.read(badXml) shouldBe ParseFailure(Seq(
          LocalDateTimeParseFailure("Text '2009-01-26T14:11:00BANG' could not be parsed, unparsed text found at index 19"),
          InstantParseFailure("Text '2009-01-26T14:11:00BANG' could not be parsed at index 19")
        ))
      }

      "MovementStatus is missing" in {

        val badXml: Elem =
          <Movement>
            <Arc>18GB00000000000232361</Arc>
            <DateOfDispatch>2009-01-26T14:11:00</DateOfDispatch>
            <OtherTraderID>ABCD1234</OtherTraderID>
          </Movement>

        GetMovementListItem.xmlReader.read(badXml) shouldBe ParseFailure(Seq(EmptyError(__ \ "MovementStatus")))
      }

      "OtherTraderID is missing" in {

        val badXml: Elem =
          <Movement>
            <Arc>18GB00000000000232361</Arc>
            <DateOfDispatch>2009-01-26T14:11:00</DateOfDispatch>
            <MovementStatus>Accepted</MovementStatus>
          </Movement>

        GetMovementListItem.xmlReader.read(badXml) shouldBe ParseFailure(Seq(EmptyError(__ \ "OtherTraderID")))
      }
    }
  }
}
