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

package uk.gov.hmrc.emcstfe.services.userAllowList

import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfe.featureswitch.core.config.{EnablePrivateBeta, EnablePublicBetaThrottling}
import uk.gov.hmrc.emcstfe.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfe.mocks.connectors.MockUserAllowListConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.request.userAllowList.CheckUserAllowListRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec

import scala.concurrent.Future

class UserAllowListServiceSpec extends TestBaseSpec with MockAppConfig {

  val requestModel: CheckUserAllowListRequest = CheckUserAllowListRequest(testErn)

  trait Test extends MockAppConfig with MockUserAllowListConnector {
    val service  = new UserAllowListService(mockUserAllowListConnector, mockAppConfig)
  }

  "isEligible" should {

    "return true" when {

      "private beta is enabled and the ERN is in the list" in new Test {
        MockUserAllowListConnector.check("service", requestModel).returns(Future.successful(Right(true)))
        MockedAppConfig.isEnabled(EnablePrivateBeta.configName).returns(true)
        await(service.isEligible(testErn, "service")) shouldBe Right(true)
      }

      "private beta is disabled, public beta is enabled and the ERN is not in the traffic percentile (but was in private beta)" in new Test {
        MockUserAllowListConnector.check("service", requestModel).returns(Future.successful(Right(true)))
        MockedAppConfig.publicBetaTrafficPercentageForService("service").returns(Some(1))
        MockedAppConfig.isEnabled(EnablePrivateBeta.configName).returns(false)
        MockedAppConfig.isEnabled(EnablePublicBetaThrottling.configName).returns(true)

        val ern = "GBWK846834276" //12%
        implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), ern, testInternalId, testCredId, Set(ern))
        await(service.isEligible(testErn, "service")) shouldBe Right(true)
      }

      "private beta is disabled, public beta is enabled and the ERN is in the traffic percentile" in new Test {
        MockUserAllowListConnector.check("service", requestModel).returns(Future.successful(Right(false)))
        MockedAppConfig.publicBetaTrafficPercentageForService("service").returns(Some(12))
        MockedAppConfig.isEnabled(EnablePrivateBeta.configName).returns(false)
        MockedAppConfig.isEnabled(EnablePublicBetaThrottling.configName).returns(true)

        val ern = "GBRC1234561089" //12%
        implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), ern, testInternalId, testCredId, Set(ern))
        await(service.isEligible(testErn, "service")) shouldBe Right(true)
      }
    }

    "return false" when {

      "private beta is disabled, public beta is enabled but the ERN is not in the traffic percentile" in new Test {
        MockedAppConfig.publicBetaTrafficPercentageForService("service").returns(Some(1))
        MockUserAllowListConnector.check("service", requestModel).returns(Future.successful(Right(false)))
        MockedAppConfig.isEnabled(EnablePrivateBeta.configName).returns(false)
        MockedAppConfig.isEnabled(EnablePublicBetaThrottling.configName).returns(true)
        val ern = "GBWK846834276" //12%
        implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), ern, testInternalId, testCredId, Set(ern))
        await(service.isEligible(testErn, "service")) shouldBe Right(false)
      }

      "private beta is enabled but the ERN is not in the list" in new Test {
        MockUserAllowListConnector.check("service", requestModel).returns(Future.successful(Right(false)))
        MockedAppConfig.isEnabled(EnablePrivateBeta.configName).returns(true)
        await(service.isEligible(testErn, "service")) shouldBe Right(false)
      }

      "neither private beta or public are enabled (return true, global access)" in new Test {
        MockedAppConfig.isEnabled(EnablePrivateBeta.configName).returns(false)
        MockedAppConfig.isEnabled(EnablePublicBetaThrottling.configName).returns(false)
        await(service.isEligible(testErn, "service")) shouldBe Right(true)
      }
    }

    "return an error" when {

      "the connector call fails to user allow list" in new Test {
        MockedAppConfig.isEnabled(EnablePrivateBeta.configName).returns(true)
        MockUserAllowListConnector.check("service", requestModel).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        await(service.isEligible(testErn, "service")) shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  "checkPublicBetaEligibility" should {
    "return true" when {
      "the ERN is in the traffic percentile (equal to)" in new Test {
        MockedAppConfig.publicBetaTrafficPercentageForService("createMovement").returns(Some(12))

        val ern = "GBWK846834276" //12%
        implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), ern, testInternalId, testCredId, Set(ern))
        service.checkPublicBetaEligibility("createMovement") shouldBe true
      }

      "the ERN is in the traffic percentile (less than)" in new Test {
        MockedAppConfig.publicBetaTrafficPercentageForService("createMovement").returns(Some(12))

        val ern = "GBWK946856224" //8%
        implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), ern, testInternalId, testCredId, Set(ern))
        service.checkPublicBetaEligibility("createMovement") shouldBe true
      }
    }

    "return false" when {

      "the ERN is in not traffic percentile (greater than by 1 (boundary))" in new Test {
        MockedAppConfig.publicBetaTrafficPercentageForService("createMovement").returns(Some(11))

        val ern = "GBWK846834276" //12%
        implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), ern, testInternalId, testCredId, Set(ern))
        service.checkPublicBetaEligibility("createMovement") shouldBe false
      }

      "the ERN is not in the traffic percentile (greater than by many)" in new Test {
        MockedAppConfig.publicBetaTrafficPercentageForService("createMovement").returns(Some(1))

        val ern = "GBRC123456789" //6%
        implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), ern, testInternalId, testCredId, Set(ern))
        service.checkPublicBetaEligibility("createMovement") shouldBe false
      }

      "the service does not exist" in new Test {
        MockedAppConfig.publicBetaTrafficPercentageForService("fakeService").returns(None)

        service.checkPublicBetaEligibility("fakeService") shouldBe false
      }
    }
  }
}
