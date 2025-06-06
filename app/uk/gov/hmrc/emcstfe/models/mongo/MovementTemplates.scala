/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.models.mongo

import play.api.libs.json.{Json, OFormat}

import scala.annotation.unused

final case class MovementTemplates(templates: Seq[MovementTemplate], count: Int)

object MovementTemplates {
  /**
   * @param f an implicit OFormat for MovementTemplate. e.g. MovementTemplate.mongoFormat
   * @return an OFormat for MovementTemplates
   */
  implicit def format(implicit @unused f: OFormat[MovementTemplate]): OFormat[MovementTemplates] = Json.format[MovementTemplates]
}
