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

package uk.gov.hmrc.emcstfe.models.reportOfReceipt

import com.lucidchart.open.xtract.ParseSuccess
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.fixtures.UnsatisfactoryModelFixtures
import uk.gov.hmrc.emcstfe.models.common.WrongWithMovement
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.Utility.trim

class UnsatisfactoryModelSpec extends TestBaseSpec with UnsatisfactoryModelFixtures {

  "UnsatisfactoryModel" must {

    WrongWithMovement.values.foreach { reason =>

      s"for reason '$reason'" must {

        "have the correct reasonMapping to an Int" in {
          maxUnsatisfactoryModel(reason).reasonMapping shouldBe reasonMapping(reason)
        }

        "for the maximum number of fields" must {

          "be possible to serialise and de-serialise to/from JSON" in {
            Json.toJson(maxUnsatisfactoryModel(reason)).as[UnsatisfactoryModel] shouldBe maxUnsatisfactoryModel(reason)
          }

          "write to XML" in {
            trim(maxUnsatisfactoryModel(reason).toXml) shouldBe trim(maxUnsatisfactoryModelXML(reason))
          }

          "read from XML" in {
            UnsatisfactoryModel.xmlReads.read(maxUnsatisfactoryModelXML(reason)) shouldBe ParseSuccess(maxUnsatisfactoryModel(reason))
          }
        }

        "for the minimum number of fields" must {

          "be possible to serialise and de-serialise to/from JSON" in {
            Json.toJson(minUnsatisfactoryModel(reason)).as[UnsatisfactoryModel] shouldBe minUnsatisfactoryModel(reason)
          }

          "write to XML" in {
            trim(minUnsatisfactoryModel(reason).toXml) shouldBe trim(minUnsatisfactoryModelXML(reason))
          }

          "read from XML" in {
            UnsatisfactoryModel.xmlReads.read(minUnsatisfactoryModelXML(reason)) shouldBe ParseSuccess(minUnsatisfactoryModel(reason))
          }
        }

      }
    }
  }
}
