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

package uk.gov.hmrc.emcstfe.utils

import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import java.util.UUID

class UUIDGeneratorSpec extends TestBaseSpec {

  "UUIDGenerator" should {
    "generate a valid UUID string" in {
      val uuidGenerator = new UUIDGenerator()
      val uuid          = uuidGenerator.randomUUID
      noException should be thrownBy UUID.fromString(uuid)
    }

    "generate unique UUIDs for multiple calls" in {
      val uuidGenerator = new UUIDGenerator()
      val uuid1         = uuidGenerator.randomUUID
      val uuid2         = uuidGenerator.randomUUID
      uuid1 should not equal uuid2
    }
  }

}
