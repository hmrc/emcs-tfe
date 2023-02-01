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

package uk.gov.hmrc.emcstfe.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.{OK, UNAUTHORIZED}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfe.config.EnrolmentKeys
import uk.gov.hmrc.emcstfe.fixtures.BaseFixtures

object AuthStub extends DownstreamStub with BaseFixtures {

  val authoriseUri = "/auth/authorise"

  def authorised(exciseNumber: String = ern): StubMapping =
    onSuccess(POST, authoriseUri, OK, Json.obj(
        "affinityGroup" -> "Organisation",
        "allEnrolments" -> Json.arr(
          Json.obj(
            "key" -> EnrolmentKeys.EMCS_ENROLMENT,
            "identifiers" -> Json.arr(
              Json.obj(
                "key" -> EnrolmentKeys.ERN,
                "value" -> exciseNumber,
                "state" -> EnrolmentKeys.ACTIVATED
              )
            )
          )
        ),
        "internalId" -> testInternalId,
        "optionalCredentials" -> Json.obj(
          "providerId" -> testCredId,
          "providerType" -> "gg"
        )
      ))

  def unauthorised(): StubMapping =
    onError(POST, authoriseUri, UNAUTHORIZED)
}