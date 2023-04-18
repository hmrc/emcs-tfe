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

package uk.gov.hmrc.emcstfe.models.request.reportOfReceipt

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement
import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement._

import scala.xml.NodeSeq

case class UnsatisfactoryModel(reason: WrongWithMovement,
                               additionalInformation: Option[String]) {

  val reasonMapping = reason match {
    case Other => 0
    case Excess => 1
    case Shortage => 2
    case Damaged => 3
    case BrokenSeals => 4
  }

  val toXml =
    <UnsatisfactoryReason>
      <UnsatisfactoryReasonCode>
        {reasonMapping}
      </UnsatisfactoryReasonCode>
      {additionalInformation.map(x => <ComplementaryInformation language="en">{x}</ComplementaryInformation>).getOrElse(NodeSeq.Empty)}
    </UnsatisfactoryReason>
}

object UnsatisfactoryModel {
  implicit val fmt: Format[UnsatisfactoryModel] = Json.format
}
