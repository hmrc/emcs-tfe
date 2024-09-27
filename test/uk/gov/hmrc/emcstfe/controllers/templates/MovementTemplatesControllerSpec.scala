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

package uk.gov.hmrc.emcstfe.controllers.templates

import play.api.http.Status
import play.api.libs.json.{Json, OFormat}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.emcstfe.controllers.actions.FakeAuthAction
import uk.gov.hmrc.emcstfe.mocks.services.MockMovementTemplatesService
import uk.gov.hmrc.emcstfe.models.mongo.{MovementTemplate, MovementTemplates}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MongoError
import uk.gov.hmrc.emcstfe.support.TestBaseSpec
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.Instant
import java.time.temporal.ChronoUnit
import scala.concurrent.Future

class MovementTemplatesControllerSpec extends TestBaseSpec
  with MockMovementTemplatesService
  with FakeAuthAction {

  implicit val movementTemplateFormat: OFormat[MovementTemplate] = MovementTemplate.responseFormat

  val instantNow = Instant.now().truncatedTo(ChronoUnit.MILLIS)
  implicit val timeMachine: TimeMachine = () => instantNow

  private val fakeRequest = FakeRequest()
  private val controller = new MovementTemplatesController(
    Helpers.stubControllerComponents(),
    mockService,
    FakeSuccessAuthAction
  )

  val template: MovementTemplate =
    MovementTemplate(
      ern = testErn,
      templateId = testTemplateId,
      templateName = testTemplateName,
      data = Json.obj(),
      lastUpdated = instantNow
    )

  ".getList" should {
    s"return $OK (OK)" when {
      "service returns a Right(Seq(template))" in {

        MockMovementTemplatesService.getList(testErn).returns(Future.successful(Right(MovementTemplates(Seq(template), 1))))

        val result = controller.getList(testErn, Some(1), Some(1))(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(MovementTemplates(Seq(template), 1))
      }
    }

    s"return $NO_CONTENT (NO_CONTENT)" when {
      "service returns a Seq()" in {

        MockMovementTemplatesService.getList(testErn).returns(Future.successful(Right(MovementTemplates(Seq(), 1))))

        val result = controller.getList(testErn, Some(1), Some(1))(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
        contentAsString(result) shouldBe ""
      }
    }

    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockMovementTemplatesService.getList(testErn).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.getList(testErn, Some(1), Some(1))(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  ".get" should {
    s"return $OK (OK)" when {
      "service returns a Right(Some(template))" in {

        MockMovementTemplatesService.get(testErn, testTemplateId).returns(Future.successful(Right(Some(template))))

        val result = controller.get(testErn, testTemplateId)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(template)
      }
    }

    s"return $NO_CONTENT (NO_CONTENT)" when {
      "service returns a Right(None)" in {

        MockMovementTemplatesService.get(testErn, testTemplateId).returns(Future.successful(Right(None)))

        val result = controller.get(testErn, testTemplateId)(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
        contentAsString(result) shouldBe ""
      }
    }

    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockMovementTemplatesService.get(testErn, testTemplateId).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.get(testErn, testTemplateId)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  ".set" should {
    s"return $OK (OK)" when {
      "service stores the new model returns a Right(template)" in {

        MockMovementTemplatesService.set(template).returns(Future.successful(Right(template)))

        val result = controller.set(testErn, testTemplateId)(fakeRequest.withBody(Json.toJson(template)))

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(template)
      }
    }
    s"return $BAD_REQUEST (BAD_REQUEST)" when {
      "Received JSON cannot be parsed to MovementTemplate" in {

        val result = controller.set(testErn, testTemplateId)(fakeRequest.withBody(Json.obj()))

        status(result) shouldBe Status.BAD_REQUEST
        contentAsString(result) should include("Invalid MovementTemplate payload")
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockMovementTemplatesService.set(template).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.set(testErn, testTemplateId)(fakeRequest.withBody(Json.toJson(template)))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  ".delete" should {
    s"return $NO_CONTENT (NO_CONTENT)" when {
      "service deletes the answers successfully" in {

        MockMovementTemplatesService.delete(testErn, testTemplateId).returns(Future.successful(Right(true)))

        val result = controller.delete(testErn, testTemplateId)(fakeRequest)

        status(result) shouldBe Status.NO_CONTENT
        contentAsString(result) shouldBe ""
      }
    }
    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockMovementTemplatesService.delete(testErn, testTemplateId).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.delete(testErn, testTemplateId)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  ".checkIfTemplateNameAlreadyExists" should {
    s"return $OK (OK)" when {

      "service returns a Right(true)" in {
        MockMovementTemplatesService.checkIfTemplateNameAlreadyExists(testErn, testTemplateName).returns(Future.successful(Right(true)))

        val result = controller.checkForExistingTemplate(testErn, testTemplateName)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(true)
      }

      "service returns a Right(false)" in {
        MockMovementTemplatesService.checkIfTemplateNameAlreadyExists(testErn, testTemplateName).returns(Future.successful(Right(false)))

        val result = controller.checkForExistingTemplate(testErn, testTemplateName)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(false)
      }
    }

    s"return $INTERNAL_SERVER_ERROR (ISE)" when {
      "service returns a Left" in {

        MockMovementTemplatesService.checkIfTemplateNameAlreadyExists(testErn, testTemplateName).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.checkForExistingTemplate(testErn, testTemplateName)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }

  ".createDraftFromTemplate" should {

    s"return $CREATED (CREATED)" when {

      "draft is successfully created" in {

        MockMovementTemplatesService.createDraftMovementFromTemplate(testErn, testTemplateId).returns(Future.successful(Right(testNewDraftId)))

        val result = controller.createDraftFromTemplate(testErn, testTemplateId)(fakeRequest)

        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.obj("createdDraftId" -> testNewDraftId)
      }
    }

    s"return an $INTERNAL_SERVER_ERROR (ISE)" when {

      "the service call fails" in {

        MockMovementTemplatesService.createDraftMovementFromTemplate(testErn, testTemplateId).returns(Future.successful(Left(MongoError("errMsg"))))

        val result = controller.createDraftFromTemplate(testErn, testTemplateId)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(MongoError("errMsg"))
      }
    }
  }
}
