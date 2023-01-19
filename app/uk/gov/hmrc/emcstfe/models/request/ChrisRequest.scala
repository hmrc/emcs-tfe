/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.request

trait ChrisRequest {
  def requestBody: String

  def exciseRegistrationNumber: String

  def arc: String

  def action: String

  def uuid: String = java.util.UUID.randomUUID().toString
}
