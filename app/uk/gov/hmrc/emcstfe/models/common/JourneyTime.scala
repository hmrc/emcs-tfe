/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.common

import play.api.libs.json.{JsString, Writes}

sealed trait JourneyTime {
  val time: String
}

object JourneyTime {
  implicit val writes: Writes[JourneyTime] = (o: JourneyTime) => JsString(o.toString)

  case class Hours(time: String) extends JourneyTime {
    override def toString: String = s"$time hours"
  }

  case class Days(time: String) extends JourneyTime {
    override def toString: String = s"$time days"
  }
}
