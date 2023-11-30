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

package uk.gov.hmrc.emcstfe.models.request

import java.time.{LocalDate, LocalTime, ZoneId}
import java.util.UUID

trait BaseRequest {

  def exciseRegistrationNumber: String

  def uuid: String = java.util.UUID.randomUUID().toString

  val preparedDate: LocalDate = LocalDate.now(ZoneId.of("UTC"))
  val preparedTime: LocalTime = LocalTime.now(ZoneId.of("UTC"))
  val correlationUUID: String = UUID.randomUUID().toString
  val messageUUID: String = UUID.randomUUID().toString

  def metricName: String

}
