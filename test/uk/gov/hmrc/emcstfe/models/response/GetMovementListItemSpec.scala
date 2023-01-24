/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.GetMovementListFixture
import uk.gov.hmrc.emcstfe.support.UnitSpec

class GetMovementListItemSpec extends UnitSpec with GetMovementListFixture {

  "writes" should {
    "write a model to JSON" in {
      Json.toJson(movement1) shouldBe movement1Json
    }
  }
}
