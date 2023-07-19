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

package uk.gov.hmrc.emcstfe.models.changeDestination

import uk.gov.hmrc.emcstfe.fixtures.SubmitChangeDestinationFixtures

class DestinationChangedModelSpec extends ChangeDestinationModelSpec with SubmitChangeDestinationFixtures {
  import DestinationChangedFixtures._

  testJsonToModelToXml(
    scenario = "DestinationChangedModel with max fields",
    json = destinationChangedJsonMax,
    model = destinationChangedModelMax,
    xml = destinationChangedXmlMax
  )

  testJsonToModelToXml(
    scenario = "DestinationChangedModel with min fields",
    json = destinationChangedJsonMin,
    model = destinationChangedModelMin,
    xml = destinationChangedXmlMin
  )
}