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

package uk.gov.hmrc.emcstfe.models.createMovement

import uk.gov.hmrc.emcstfe.fixtures.CreateMovementFixtures
import uk.gov.hmrc.emcstfe.models.XmlModelBaseSpec

class BodyEadEsadModelSpec extends XmlModelBaseSpec with CreateMovementFixtures {
  import BodyEadEsadFixtures._

  testJsonToModelToXml(
    scenario = "BodyEadEsadModel with max fields",
    json = bodyEadEsadJsonMax,
    model = bodyEadEsadModelMax,
    xml = bodyEadEsadXmlMax
  )

  testJsonToModelToXml(
    scenario = "BodyEadEsadModel with min fields",
    json = bodyEadEsadJsonMin,
    model = bodyEadEsadModelMin,
    xml = bodyEadEsadXmlMin
  )
}
