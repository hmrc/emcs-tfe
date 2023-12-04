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

package uk.gov.hmrc.emcstfe.support

import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits}
import uk.gov.hmrc.emcstfe.controllers.actions.FakeAuthAction
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

trait UnitSpec extends AnyWordSpecLike with EitherValues with Matchers with FutureAwaits with DefaultAwaitTimeout with GuiceOneAppPerSuite with FakeAuthAction {

  val testTtl = "1 seconds"
  val testReplaceIndexes = true

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit lazy val userRequest: UserRequest[_] = UserRequest(FakeRequest(), testErn, testInternalId, testCredId)
}
