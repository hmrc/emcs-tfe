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

package uk.gov.hmrc.emcstfe.controllers

import org.mongodb.scala.bson.BsonDocument
import play.api.http.Status
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.libs.json.Json
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.emcstfe.fixtures.CreateMovementSearchFixture
import uk.gov.hmrc.emcstfe.models.common.Ascending
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{Export, TaxWarehouse}
import uk.gov.hmrc.emcstfe.models.request.{GetDraftMovementSearchOptions, LRN}
import uk.gov.hmrc.emcstfe.models.response.SearchDraftMovementsResponse
import uk.gov.hmrc.emcstfe.repositories.CreateMovementUserAnswersRepositoryImpl
import uk.gov.hmrc.emcstfe.stubs.AuthStub
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

class CreateMovementUserAnswersSearchDraftsIntegrationSpec extends IntegrationBaseSpec
  with CreateMovementSearchFixture  {

  val repository = app.injector.instanceOf[CreateMovementUserAnswersRepositoryImpl]

  val additionalDraftsCount = 50
  val additionalDraftData = generateNDrafts(n = additionalDraftsCount)
  val insertedDrafts = searchableDrafts ++ additionalDraftData

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    await(repository.collection.deleteMany(BsonDocument()).head())
    await(repository.collection.insertMany(insertedDrafts).head())
  }

  val uri = s"/user-answers/create-movement/drafts/search/$testErn"

  s"GET /user-answers/create-movement/drafts/search/$testErn" when {
    "user is unauthenticated" must {
      "return Forbidden" in {

        AuthStub.unauthorised()

        val response: WSResponse = await(buildRequest(uri).get())

        response.status shouldBe Status.FORBIDDEN
      }
    }

    "user is authenticated but the ERN requested does not match the ERN of the credential" in {

      AuthStub.authorised("WrongERN")

      val response: WSResponse = await(buildRequest(uri).get())

      response.status shouldBe Status.FORBIDDEN
    }

    "user is authenticated and authorised" must {
      s"return $OK (OK) and search results" when {

        "search criteria doesn't match any data" in {

          AuthStub.authorised()

          val searchRequest = buildRequest(uri).withQueryStringParameters(
            "search.searchTerm" -> "notFoundSearchTerm"
          )

          val response: WSResponse = await(searchRequest.get())

          response.status shouldBe OK
          response.json shouldBe Json.toJson(SearchDraftMovementsResponse(count = 0, paginatedDrafts = Seq.empty))
        }

        "multiple search criteria match" in {

          AuthStub.authorised()

          val searchTerm = "ABC"

          val searchRequest = buildRequest(uri).withQueryStringParameters(
            "search.searchTerm" -> searchTerm,
            "search.sortOrder" -> Ascending.toString,
            "search.sortField" -> LRN.toString,
            "search.destinationType" -> TaxWarehouse.toString,
            "search.destinationType" -> Export.toString
          )

          val response: WSResponse = await(searchRequest.get())

          val expectedReturnedDrafts = insertedDrafts
            .filter(_.ern == testErn)
            .filter(_.hasBeenSubmitted == false)
            .filter(draft =>
              draft.data.lrn.exists(_.contains(searchTerm)) ||
                draft.data.consigneeBusinessName.exists(_.contains(searchTerm)) ||
                draft.data.consigneeERN.exists(_.contains(searchTerm)) ||
                draft.data.destinationWarehouseERN.exists(_.contains(searchTerm)) ||
                draft.data.dispatchWarehouseERN.exists(_.contains(searchTerm))
            )
            .filter(_.data.destinationType.exists(Seq(TaxWarehouse, Export).flatMap(_.movementScenarios).contains))
            .sortBy(_.data.lrn)
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)

          response.status shouldBe OK
          response.json shouldBe Json.toJson(SearchDraftMovementsResponse(count = 3, paginatedDrafts = expectedReturnedDrafts))
        }

        "paginated search results" must {

          "return record 1 to 5 when Start Position is 0 and max records is 5" in {

            AuthStub.authorised()

            val start = 0
            val max = 5

            val searchRequest = buildRequest(uri).withQueryStringParameters(
              "search.startPosition" -> start.toString,
              "search.maxRows" -> max.toString
            )

            val response: WSResponse = await(searchRequest.get())

            val foundDrafts = insertedDrafts
              .filter(_.data.lrn.isDefined)
              .filter(_.ern == testErn)
              .filter(_.hasBeenSubmitted == false)

            val expectedReturnedDrafts = foundDrafts
              .sortBy(_.lastUpdated)
              .reverse
              .slice(start, start + max)

            expectedReturnedDrafts.size shouldBe Math.min(max, foundDrafts.size)
            response.status shouldBe OK
            response.json shouldBe Json.toJson(SearchDraftMovementsResponse(count = foundDrafts.size, paginatedDrafts = expectedReturnedDrafts))
          }

          "return record 6 to 10 when Start Position is 5 and max records is 5" in {

            AuthStub.authorised()

            val start = 5
            val max = 5

            val searchRequest = buildRequest(uri).withQueryStringParameters(
              "search.startPosition" -> start.toString,
              "search.maxRows" -> max.toString
            )

            val response: WSResponse = await(searchRequest.get())

            val foundDrafts = insertedDrafts
              .filter(_.data.lrn.isDefined)
              .filter(_.ern == testErn)
              .filter(_.hasBeenSubmitted == false)

            val expectedReturnedDrafts = foundDrafts
              .sortBy(_.lastUpdated)
              .reverse
              .slice(start, start + max)

            expectedReturnedDrafts.size shouldBe Math.min(max, foundDrafts.size)
            response.status shouldBe OK
            response.json shouldBe Json.toJson(SearchDraftMovementsResponse(count = foundDrafts.size, paginatedDrafts = expectedReturnedDrafts))
          }

          "return record 1 to 30 when Start Position is 0 and max records is 30" in {

            AuthStub.authorised()

            val start = 0
            val max = 30

            val searchRequest = buildRequest(uri).withQueryStringParameters(
              "search.startPosition" -> start.toString,
              "search.maxRows" -> max.toString
            )

            val response: WSResponse = await(searchRequest.get())

            val foundDrafts = insertedDrafts
              .filter(_.data.lrn.isDefined)
              .filter(_.ern == testErn)
              .filter(_.hasBeenSubmitted == false)

            val expectedReturnedDrafts = foundDrafts
              .sortBy(_.lastUpdated)
              .reverse
              .slice(start, start + max)

            expectedReturnedDrafts.size shouldBe Math.min(max, foundDrafts.size)
            response.status shouldBe OK
            response.json shouldBe Json.toJson(SearchDraftMovementsResponse(count = foundDrafts.size, paginatedDrafts = expectedReturnedDrafts))
          }

          "return 400 BadRequest if the startPosition is negative" in {

            AuthStub.authorised()

            val start = -1

            val searchRequest = buildRequest(uri).withQueryStringParameters(
              "search.startPosition" -> start.toString
            )

            val response: WSResponse = await(searchRequest.get())

            response.status shouldBe BAD_REQUEST
            response.body shouldBe "startPosition must be positive"
          }

          "return 400 BadRequest if the maxRows is negative" in {

            AuthStub.authorised()

            val maxRows = -1

            val searchRequest = buildRequest(uri).withQueryStringParameters(
              "search.maxRows" -> maxRows.toString
            )

            val response: WSResponse = await(searchRequest.get())

            response.status shouldBe BAD_REQUEST
            response.body shouldBe "maxRows must be positive"
          }
        }
      }
    }
  }
}
