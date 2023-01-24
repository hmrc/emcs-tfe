/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.support.UnitSpec

import scala.xml.XML

class GetMovementListResponseSpec extends UnitSpec with GetMovementListFixture {

  "writes" should {
    "write a model to JSON" in {
      Json.toJson(getMovementListResponse) shouldBe getMovementListJson
    }
  }

  ".apply(xml: NodeSeq)" should {

    "convert a full XML response into a model" in {
      GetMovementListResponse(XML.loadString(getMovementListXMLResponseBody)) shouldBe getMovementListResponse
    }
  }
}
