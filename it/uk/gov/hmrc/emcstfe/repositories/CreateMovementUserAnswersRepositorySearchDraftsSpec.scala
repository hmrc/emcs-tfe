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

package uk.gov.hmrc.emcstfe.repositories

import uk.gov.hmrc.emcstfe.fixtures.{CreateMovementSearchFixture, MovementSubmissionFailureFixtures}
import uk.gov.hmrc.emcstfe.models.common.Ascending
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{Export, RegisteredConsignee, TaxWarehouse}
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.request.{GetDraftMovementSearchOptions, LRN}
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate}

class CreateMovementUserAnswersRepositorySearchDraftsSpec extends RepositoryBaseSpec[CreateMovementUserAnswers]
  with CreateMovementSearchFixture
  with MovementSubmissionFailureFixtures {

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  protected override val repository = new CreateMovementUserAnswersRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    time = timeMachine
  )

  val additionalDraftsCount = 50
  val additionalDraftData = generateNDrafts(n = additionalDraftsCount)
  val insertedDrafts = searchableDrafts ++ additionalDraftData

  override protected def beforeAll(): Unit = {
    //Ensures the notablescan value is set to true. See beforeALl on the IndexedMongoQueriesSupport trait.
    super.beforeAll()
    //Ensures that DB is empty and indexes are set.
    prepareDatabase()
    await(repository.collection.insertMany(insertedDrafts).head())
  }

  //IMPORTANT: This Intentionally overrides beforeEach to an empty Unit so as to NOT drop Mongo before every test.
  //In this spec, we want to specifically keep all data for performance reasons as we're repeatedly querying the same data set
  override protected def beforeEach(): Unit = ()

  ".search" when {

    "searching by only ERN" must {

      "return paginated drafts related to that ERN (with default max rows, ordering, start position, etc.)" in {

        val searchOptions = GetDraftMovementSearchOptions()

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + searchableDrafts.size
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts related to that ERN, sorted by lastUpdated ascending" in {

        val searchOptions = GetDraftMovementSearchOptions(sortOrder = Ascending)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + searchableDrafts.size
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts related to that ERN, sorted by LRN descending" in {

        val searchOptions = GetDraftMovementSearchOptions(sortField = LRN)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + searchableDrafts.size
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.data.lrn)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts related to that ERN, sorted by LRN ascending" in {

        val searchOptions = GetDraftMovementSearchOptions(sortField = LRN, sortOrder = Ascending)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + searchableDrafts.size
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.data.lrn)
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts, from 3 to MAX related to that ERN (should return the default max value 10)" in {

        val startPosition = 3
        val searchOptions = GetDraftMovementSearchOptions(startPosition = startPosition)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + searchableDrafts.size
        result.paginatedDrafts.size shouldBe GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .slice(startPosition, GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS + startPosition)
      }

      s"return paginated drafts, from ${(additionalDraftsCount + searchableDrafts.size) - 3} to MAX related to that ERN (should return 3)" in {

        val startPosition = (additionalDraftsCount + searchableDrafts.size) - 3
        val searchOptions = GetDraftMovementSearchOptions(startPosition = startPosition)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + searchableDrafts.size
        result.paginatedDrafts.size shouldBe 3
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .slice(startPosition, GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS + startPosition)
      }

      s"return paginated drafts, from ${additionalDraftsCount + searchableDrafts.size} to MAX related to that ERN (should return 0)" in {

        val startPosition = additionalDraftsCount + searchableDrafts.size
        val searchOptions = GetDraftMovementSearchOptions(startPosition = startPosition)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + searchableDrafts.size
        result.paginatedDrafts.size shouldBe 0
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .slice(startPosition, GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS + startPosition)
      }
    }

    "searching by ERN and a search term" when {

      "search term doesn't match anything" must {

        val searchTerm = "FOO"

        "return empty and count 0" in {

          val searchOptions = GetDraftMovementSearchOptions(searchTerm = Some(searchTerm))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 0
          result.paginatedDrafts shouldBe Seq()
        }
      }

      "only LRN is matched" must {

        val searchTerm = "ABCDEFGHIJKL"

        "return paginated drafts related to that ERN where the search term also matches" in {

          val searchOptions = GetDraftMovementSearchOptions(searchTerm = Some(searchTerm))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 1
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(_.data.lrn.exists(_.contains(searchTerm)))
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }

      "LRN and BusinessName matched" must {

        val searchTerm = "ABCDEFGHIJK"

        "return paginated drafts related to that ERN where the search term also matches" in {

          val searchOptions = GetDraftMovementSearchOptions(searchTerm = Some(searchTerm))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 2
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchTerm)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchTerm))
              )
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }

      "LRN, BusinessName and consigneeErn matched" must {

        val searchTerm = "ABCDEFGHIJ"

        "return paginated drafts related to that ERN where the search term also matches" in {

          val searchOptions = GetDraftMovementSearchOptions(searchTerm = Some(searchTerm))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 3
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchTerm)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchTerm)) ||
                  draft.data.consigneeERN.exists(_.contains(searchTerm))
              )
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }

      "LRN, BusinessName, consigneeErn and DestinationErn matched" must {

        val searchTerm = "ABCDEFGHI"

        "return paginated drafts related to that ERN where the search term also matches" in {

          val searchOptions = GetDraftMovementSearchOptions(searchTerm = Some(searchTerm))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 4
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchTerm)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchTerm)) ||
                  draft.data.consigneeERN.exists(_.contains(searchTerm)) ||
                  draft.data.destinationWarehouseERN.exists(_.contains(searchTerm))
              )
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }

      "LRN, BusinessName, consigneeErn, DestinationErn and DispatchErn matched" must {

        val searchTerm = "ABCDEFGH"

        "return paginated drafts related to that ERN where the search term also matches" in {

          val searchOptions = GetDraftMovementSearchOptions(searchTerm = Some(searchTerm))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 5
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchTerm)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchTerm)) ||
                  draft.data.consigneeERN.exists(_.contains(searchTerm)) ||
                  draft.data.destinationWarehouseERN.exists(_.contains(searchTerm)) ||
                  draft.data.dispatchWarehouseERN.exists(_.contains(searchTerm))
              )
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }
    }

    "searching for drafts which have errors" must {

      "only return drafts with errors" when {

        "ERN matches and draft has errors (exists a fields which has not been fixed)" in {

          val searchOptions = GetDraftMovementSearchOptions(
            draftHasErrors = Some(true)
          )

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 1
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(_.submissionFailures.exists(_.hasBeenFixed == false))
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }
    }

    "searching for drafts with a dispatch date in range" must {

      "return drafts where the dispatch date is equal to the `from` date" in {

        val fromDate = LocalDate.of(2024, 4, 6)
        val searchOptions = GetDraftMovementSearchOptions(
          dateOfDispatchFrom = Some(fromDate)
        )

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe 3
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .filter(draft =>
              draft.data.dispatchDate.exists(date => !date.isBefore(fromDate))
            )
            .sortBy(_.lastUpdated)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return drafts where the dispatch date is after the `from` date" in {

        val fromDate = LocalDate.of(2024, 4, 5)
        val searchOptions = GetDraftMovementSearchOptions(
          dateOfDispatchFrom = Some(fromDate)
        )

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe 3
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .filter(draft =>
              draft.data.dispatchDate.exists(date => !date.isBefore(fromDate))
            )
            .sortBy(_.lastUpdated)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return drafts where the dispatch date is equal the `to` date" in {

        val toDate = LocalDate.of(2024, 5, 1)
        val searchOptions = GetDraftMovementSearchOptions(
          dateOfDispatchTo = Some(toDate)
        )

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe 3
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .filter(draft =>
              draft.data.dispatchDate.exists(date => !date.isAfter(toDate))
            )
            .sortBy(_.lastUpdated)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return drafts where the dispatch date is before the `to` date" in {

        val toDate = LocalDate.of(2024, 5, 2)
        val searchOptions = GetDraftMovementSearchOptions(
          dateOfDispatchTo = Some(toDate)
        )

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe 3
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .filter(draft =>
              draft.data.dispatchDate.exists(date => !date.isAfter(toDate))
            )
            .sortBy(_.lastUpdated)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return drafts where the dispatch date is between the `from` and `to` date" in {

        val fromDate = LocalDate.of(2024, 4, 1)
        val toDate = LocalDate.of(2024, 4, 7)
        val searchOptions = GetDraftMovementSearchOptions(
          dateOfDispatchFrom = Some(fromDate),
          dateOfDispatchTo = Some(toDate)
        )

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe 1
        result.paginatedDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .filter(draft =>
              draft.data.dispatchDate.exists(date => !date.isAfter(toDate)) &&
                draft.data.dispatchDate.exists(date => !date.isBefore(fromDate))
            )
            .sortBy(_.lastUpdated)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }
    }

    "searching for drafts which contains products of a certain EPC" must {

      "only return drafts which contain the selected EPC" when {

        "ERN matches and draft exists with an EPC selected" in {

          val searchOptions = GetDraftMovementSearchOptions(exciseProductCode = Some("B100"))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 2
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(_.data.itemEpcs.exists(_.exists(_ == "B100")))
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }

      "return no drafts where selected EPC doesn't exist in any of them" in {

        val searchOptions = GetDraftMovementSearchOptions(exciseProductCode = Some("X100"))

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe 0
        result.paginatedDrafts shouldBe Seq()
      }
    }

    "searching for drafts which match specified Destination Types" must {

      "only return drafts where the Destination Type matches" when {

        "matches multiple drafts with a destination type that relates to the overarching movement scenario" in {

          val destinationTypes = Seq(TaxWarehouse, Export)
          val searchOptions = GetDraftMovementSearchOptions(destinationTypes = Some(destinationTypes))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 3
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(_.data.destinationType.exists(destinationTypes.flatMap(_.movementScenarios).contains))
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }

        "matches one draft with a destination type that relates to the overarching movement scenario" in {

          val destinationTypes = Seq(Export)
          val searchOptions = GetDraftMovementSearchOptions(destinationTypes = Some(destinationTypes))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 1
          result.paginatedDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(_.data.destinationType.exists(destinationTypes.flatMap(_.movementScenarios).contains))
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }
      }

      "matches NO drafts with a destination type that relates to the overarching movement scenario" in {

        val destinationTypes = Seq(RegisteredConsignee)
        val searchOptions = GetDraftMovementSearchOptions(destinationTypes = Some(destinationTypes))

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe 0
        result.paginatedDrafts shouldBe Seq()
      }
    }
  }
}
