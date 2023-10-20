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

package uk.gov.hmrc.emcstfe.models.common

import com.lucidchart.open.xtract.{ParseFailure, ParseSuccess}
import play.api.libs.json._
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.{Days, Hours, JourneyTimeParseFailure}
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.Elem

class JourneyTimeSpec extends TestBaseSpec {

  ".writes" should {
    "write a Hours model to JSON" in {
      Json.toJson[JourneyTime](Hours("40")) shouldBe JsString("40 hours")
    }

    "write a Days model to JSON" in {
      Json.toJson[JourneyTime](Days("40")) shouldBe JsString("40 days")
    }
  }

  ".reads" should {

    "successfully read a JourneyTime item" when {

      "response is in Hours" in {
        val json = JsString("30 hours")

        JourneyTime.reads.reads(json) shouldBe JsSuccess(Hours("30"))
      }

      "response is in Days" in {
        val json = JsString("30 days")

        JourneyTime.reads.reads(json) shouldBe JsSuccess(Days("30"))
      }
    }

    "fail to read JourneyTime" when {

      "not in Hours/Days format" in {
        val json = JsString("30")

        JourneyTime.reads.reads(json) shouldBe JsError("Could not parse JourneyTime from JSON, received: '30'")
      }

      "not a String" in {
        val json = JsNull

        JourneyTime.reads.reads(json) shouldBe JsError("Value is not a String: null")
      }
    }
  }

  ".xmlReads" should {

    "successfully read a JourneyTime item" when {

      "response is in Hours" in {

        val xml: Elem = <JourneyTime>H30</JourneyTime>

        JourneyTime.xmlReads.read(xml) shouldBe ParseSuccess(Hours("30"))
      }

      "response is in Days" in {

        val xml: Elem = <JourneyTime>D30</JourneyTime>

        JourneyTime.xmlReads.read(xml) shouldBe ParseSuccess(Days("30"))
      }
    }

    "fail to read JourneyTime" when {

      "not in Hours/Days format" in {

        val xml: Elem = <JourneyTime>30</JourneyTime>

        JourneyTime.xmlReads.read(xml) shouldBe ParseFailure(JourneyTimeParseFailure("Could not parse JourneyTime from XML, received: '30'"))
      }
    }
  }

  "toString" should {
    "return the correct value for Hours" in {
      Hours("20").toString shouldBe "20 hours"
    }

    "return the correct value for Days" in {
      Days("20").toString shouldBe "20 days"
    }
  }

  "toDownstream" should {

    "return the correct value for Hours (single digit)" in {
      Hours("1").toDownstream shouldBe "H01"
    }

    "return the correct value for Hours" in {
      Hours("20").toDownstream shouldBe "H20"
    }

    "return the correct value for Days (single digit)" in {
      Days("1").toDownstream shouldBe "D01"
    }

    "return the correct value for Days" in {
      Days("20").toDownstream shouldBe "D20"
    }
  }
}
