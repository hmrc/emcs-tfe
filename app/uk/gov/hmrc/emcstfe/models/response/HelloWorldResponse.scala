/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.response

import play.api.libs.json.{Json, OFormat}

case class HelloWorldResponse(message: String)

object HelloWorldResponse {
  implicit val format: OFormat[HelloWorldResponse] = Json.format
}
