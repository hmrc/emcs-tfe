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
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.emcstfe.models.common.JourneyTime.{Days, Hours, JourneyTimeParseFailure}
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.Elem

class JourneyTimeSpec extends UnitSpec {

  ".writes" should {
    "write a Hours model to JSON" in {
      Json.toJson[JourneyTime](Hours("40")) shouldBe JsString("40 hours")
    }

    "write a Days model to JSON" in {
      Json.toJson[JourneyTime](Days("40")) shouldBe JsString("40 days")
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

        JourneyTime.xmlReads.read(xml) shouldBe ParseFailure(JourneyTimeParseFailure("Could not parse JourneyTime, received: '30'"))
      }
    }
  }
}
