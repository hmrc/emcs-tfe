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

package uk.gov.hmrc.emcstfe.models

import com.lucidchart.open.xtract.{ParseSuccess, XmlReader}
import play.api.libs.json.{JsObject, Reads}
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.Elem
import scala.xml.Utility.trim

trait XmlModelBaseSpec extends TestBaseSpec {
  def testJsonToModelToXml[Model <: XmlBaseModel](scenario: String,
                                                  json: JsObject,
                                                  model: Model,
                                                  xml: Elem)(implicit reads: Reads[Model]): Unit = {

    s"${model.getClass.getSimpleName}" when {

      s"$scenario" must {

        "convert JSON to a model correctly" in {
          json.as[Model] shouldBe model
        }
        "convert a model to XML correctly" in {
          trim(model.toXml).toString shouldBe trim(xml).toString
        }
      }
    }
  }

  def testXmlToModel[Model <: XmlBaseModel](scenario: String,
                                                  model: Model,
                                                  xml: Elem)(implicit reads: XmlReader[Model]): Unit = {

    s"${model.getClass.getSimpleName}" when {

      s"$scenario" must {

        "convert XML to a model correctly" in {
          reads.read(trim(xml)) shouldBe ParseSuccess(model)
        }
      }
    }
  }
}
