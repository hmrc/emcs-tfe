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

package uk.gov.hmrc.emcstfe.models.response.getMovement

import cats.implicits.catsSyntaxTuple2Semigroupal
import com.lucidchart.open.xtract.{XmlReader, __}
import play.api.libs.json.{Format, Json}

case class CustomsRejectionDiagnosis(bodyRecordUniqueReference: String, diagnosisCode: CustomsRejectionDiagnosisCodeType)

object CustomsRejectionDiagnosis {
  implicit val format: Format[CustomsRejectionDiagnosis] = Json.format[CustomsRejectionDiagnosis]

  private lazy val bodyRecordUniqueReference = __ \\ "BodyRecordUniqueReference"

  private lazy val diagnosisCode = __ \\ "DiagnosisCode"

  implicit val xmlReads: XmlReader[CustomsRejectionDiagnosis] = (
    bodyRecordUniqueReference.read[String],
    diagnosisCode.read[CustomsRejectionDiagnosisCodeType](CustomsRejectionDiagnosisCodeType.xmlReads("DiagnosisCode")(CustomsRejectionDiagnosisCodeType.enumerable))
  ).mapN(CustomsRejectionDiagnosis.apply)

}
