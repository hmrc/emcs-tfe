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

package uk.gov.hmrc.emcstfe.models.response

import com.lucidchart.open.xtract.{EmptyError, ParseFailure, ParseSuccess, __}
import uk.gov.hmrc.emcstfe.fixtures.SetMessageAsLogicallyDeletedFixtures
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.xml.Elem

class SetMessageAsLogicallyDeletedResponseSpec extends TestBaseSpec with SetMessageAsLogicallyDeletedFixtures {

  ".xmlReads" should {

    "successfully read a message set as logically deleted" when {

      "all fields are valid (RecordsAffected)" in {
        SetMessageAsLogicallyDeletedResponse.xmlReader.read(deletedMessageXML) shouldBe ParseSuccess(deletedMessage)
      }

    }

    "fail to read a message set as logically deleted" when {

      "RecordsAffected is missing" in {

        val badXML: Elem =
          <Results>
            <Result Name="schema">
            </Result>
          </Results>

        SetMessageAsLogicallyDeletedResponse.xmlReader.read(badXML) shouldBe ParseFailure(Seq(EmptyError(__ \\ "recordsAffected")))
      }
    }
  }
}
