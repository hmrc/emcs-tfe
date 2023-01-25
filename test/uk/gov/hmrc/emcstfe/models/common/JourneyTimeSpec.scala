/*
 * Copyright 2023 HM Revenue & Customs
 *
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
