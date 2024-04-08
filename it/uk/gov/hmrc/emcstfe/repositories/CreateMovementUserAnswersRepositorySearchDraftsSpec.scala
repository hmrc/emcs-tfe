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

import play.api.libs.json._
import uk.gov.hmrc.emcstfe.fixtures.MovementSubmissionFailureFixtures
import uk.gov.hmrc.emcstfe.models.common.Ascending
import uk.gov.hmrc.emcstfe.models.common.DestinationType.{Export, RegisteredConsignee, TaxWarehouse}
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers
import uk.gov.hmrc.emcstfe.models.request.{GetDraftMovementSearchOptions, LRN}
import uk.gov.hmrc.emcstfe.utils.TimeMachine

import java.time.temporal.ChronoUnit
import java.time.{Instant, LocalDate}
import java.util.UUID
import scala.util.Random

class CreateMovementUserAnswersRepositorySearchDraftsSpec extends RepositoryBaseSpec[CreateMovementUserAnswers] with MovementSubmissionFailureFixtures {

  private val instantNow = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val timeMachine: TimeMachine = () => instantNow

  protected override val repository = new CreateMovementUserAnswersRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    time = timeMachine
  )

  val draftData = Seq(
    userAnswers(
      draftId = UUID.randomUUID().toString,
      data = Json.obj(
        "info" -> Json.obj(
          "localReferenceNumber" -> "LRN-ABCD-12456465"
        ),
        "consignee" -> Json.obj(
          "businessName" -> "Foo-ABCD-123456",
          "exciseRegistrationNumber" -> "ERNABCDEFGHIJ123456"
        ),
        "destination" -> Json.obj(
          "destinationWarehouseExcise" -> "WarehouseABERN"
        ),
        "items" -> Json.obj(
          "addedItems" -> Json.arr(
            Json.obj("itemExciseProductCode" -> "T200"),
            Json.obj("itemExciseProductCode" -> "B100")
          )
        )
      ),
      timestamp = Instant.ofEpochSecond(600)
    ),
    userAnswers(
      draftId = UUID.randomUUID().toString,
      data = Json.obj(
        "info" -> Json.obj(
          "localReferenceNumber" -> "ABCDEFGHIJKL123456",
          "destinationType" -> "gbTaxWarehouse"
        ),
        "consignee" -> Json.obj(
          "businessName" -> "ABCDFoo-123456",
          "exciseRegistrationNumber" -> "ABCERN123456"
        ),
        "destination" -> Json.obj(
          "destinationWarehouseExcise" -> "ABWarehouseERN"
        ),
        "items" -> Json.obj(
          "addedItems" -> Json.arr(
            Json.obj("itemExciseProductCode" -> "T200")
          )
        )
      ),
      timestamp = Instant.ofEpochSecond(3000),
      submissionFailures = Seq(movementSubmissionFailureModel.copy(hasBeenFixed = false))
    ),
    userAnswers(
      draftId = UUID.randomUUID().toString,
      data = Json.obj(
        "info" -> Json.obj(
          "localReferenceNumber" -> "123456ABC",
          "dispatchDetails" -> Json.obj(
            "date" -> LocalDate.of(2024, 4, 6)
          ),
          "destinationType" -> "euTaxWarehouse"
        ),
        "consignee" -> Json.obj(
          "businessName" -> "Foo-123456ABCDEFGHIJK",
          "exciseRegistrationNumber" -> "ERN123456ABC"
        ),
        "destination" -> Json.obj(
          "destinationWarehouseExcise" -> "WarehouseERNAB"
        ),
        "items" -> Json.obj(
          "addedItems" -> Json.arr(
            Json.obj("itemExciseProductCode" -> "B100"),
            Json.obj("itemExciseProductCode" -> "E200")
          )
        )
      ),
      timestamp = Instant.ofEpochSecond(4500)
    ),
    userAnswers(
      draftId = UUID.randomUUID().toString,
      data = Json.obj(
        "info" -> Json.obj(
          "localReferenceNumber" -> "123456ABC",
          "dispatchDetails" -> Json.obj(
            "date" -> LocalDate.of(2024, 5, 1)
          ),
          "destinationType" -> "exportWithCustomsDeclarationLodgedInTheUk"
        ),
        "consignee" -> Json.obj(
          "businessName" -> "Foo-123456ABC",
          "exciseRegistrationNumber" -> "ERN123456ABC"
        ),
        "destination" -> Json.obj(
          "destinationWarehouseExcise" -> "WarehouseERNABCDEFGHI"
        )
      ),
      timestamp = Instant.ofEpochSecond(6000),
      submissionFailures = Seq(movementSubmissionFailureModel)
    )
  )
  val additionalDraftsCount = 50
  val additionalDraftData = generateNDrafts(n = additionalDraftsCount)
  val insertedDrafts = draftData ++ additionalDraftData

  override protected def beforeAll(): Unit = {
    //Ensures the notablescan value is set to true. See beforeALl on the IndexedMongoQueriesSupport trait.
    super.beforeAll()
    //Ensures that DB is empty and indexes are set.
    prepareDatabase()
    insertedDrafts.foreach(draft => await(insert(draft)))
  }

  //IMPORTANT: This Intentionally overrides beforeEach to an empty Unit so as to NOT drop Mongo before every test.
  //In this spec, we want to specifically keep all data for performance reasons as we're repeatedly querying the same data set
  override protected def beforeEach(): Unit = { () }

  ".search" when {

    "searching by only ERN" must {

      "return paginated drafts related to that ERN (with default max rows, ordering, start position, etc.)" in {

        val searchOptions = GetDraftMovementSearchOptions()

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + draftData.size
        result.foundDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts related to that ERN, sorted by lastUpdated ascending" in {

        val searchOptions = GetDraftMovementSearchOptions(sortOrder = Ascending)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + draftData.size
        result.foundDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts related to that ERN, sorted by LRN descending" in {

        val searchOptions = GetDraftMovementSearchOptions(sortField = LRN)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + draftData.size
        result.foundDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.data.lrn)
            .reverse
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts related to that ERN, sorted by LRN ascending" in {

        val searchOptions = GetDraftMovementSearchOptions(sortField = LRN, sortOrder = Ascending)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + draftData.size
        result.foundDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.data.lrn)
            .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
      }

      "return paginated drafts, from 3 to MAX related to that ERN (should return the default max value 10)" in {

        val startPosition = 3
        val searchOptions = GetDraftMovementSearchOptions(startPosition = startPosition)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + draftData.size
        result.foundDrafts.size shouldBe GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS
        result.foundDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .slice(startPosition, GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS + startPosition)
      }

      s"return paginated drafts, from ${(additionalDraftsCount + draftData.size) - 3} to MAX related to that ERN (should return 3)" in {

        val startPosition = (additionalDraftsCount + draftData.size) - 3
        val searchOptions = GetDraftMovementSearchOptions(startPosition = startPosition)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + draftData.size
        result.foundDrafts.size shouldBe 3
        result.foundDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .slice(startPosition, GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS + startPosition)
      }

      s"return paginated drafts, from ${additionalDraftsCount + draftData.size} to MAX related to that ERN (should return 0)" in {

        val startPosition = additionalDraftsCount + draftData.size
        val searchOptions = GetDraftMovementSearchOptions(startPosition = startPosition)

        lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

        result.count shouldBe additionalDraftsCount + draftData.size
        result.foundDrafts.size shouldBe 0
        result.foundDrafts shouldBe
          insertedDrafts
            .filter(_.ern == testErn)
            .sortBy(_.lastUpdated)
            .reverse
            .slice(startPosition, GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS + startPosition)
      }
    }

    "searching by ERN and a search term" when {

      "only LRN is matched" must {

        val searchString = "ABCDEFGHIJKL"

        "return paginated drafts related to that ERN where the search term also matches against LRN" in {

          val searchOptions = GetDraftMovementSearchOptions(searchString = Some(searchString))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 1
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(_.data.lrn.exists(_.contains(searchString)))
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }

        "return paginated drafts related to that ERN where the search term also matches against LRN (sorted asecnding and different start position)" in {

          val startPosition = 2
          val maxRows = 10
          val searchOptions = GetDraftMovementSearchOptions(
            searchString = Some(searchString),
            sortField = LRN,
            sortOrder = Ascending,
            startPosition = startPosition,
            maxRows = maxRows
          )

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 1
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(_.data.lrn.exists(_.contains(searchString)))
              .sortBy(_.data.lrn)
              .slice(startPosition, maxRows + startPosition)
        }
      }

      "LRN and BusinessName matched" must {

        val searchString = "ABCDEFGHIJK"

        "return paginated drafts related to that ERN where the search term also matches against LRN and Business Name" in {

          val searchOptions = GetDraftMovementSearchOptions(searchString = Some(searchString))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 2
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchString)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchString))
              )
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }

        "return paginated drafts related to that ERN where the search term also matches against LRN (sorted asecnding and different start position)" in {

          val startPosition = 2
          val maxRows = 10
          val searchOptions = GetDraftMovementSearchOptions(
            searchString = Some(searchString),
            sortField = LRN,
            sortOrder = Ascending,
            startPosition = startPosition,
            maxRows = maxRows
          )

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 2
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchString)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchString))
              )
              .sortBy(_.data.lrn)
              .slice(startPosition, maxRows + startPosition)
        }
      }

      "LRN, BusinessName and exciseRegistrationNumber matched" must {

        val searchString = "ABCDEFGHIJ"

        "return paginated drafts related to that ERN where the search term also matches against LRN and Business Name" in {

          val searchOptions = GetDraftMovementSearchOptions(searchString = Some(searchString))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 3
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchString)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchString)) ||
                  draft.data.consigneeERN.exists(_.contains(searchString))
              )
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }

        "return paginated drafts related to that ERN where the search term also matches against LRN (sorted asecnding and different start position)" in {

          val startPosition = 2
          val maxRows = 10
          val searchOptions = GetDraftMovementSearchOptions(
            searchString = Some(searchString),
            sortField = LRN,
            sortOrder = Ascending,
            startPosition = startPosition,
            maxRows = maxRows
          )

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 3
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchString)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchString)) ||
                  draft.data.consigneeERN.exists(_.contains(searchString))
              )
              .sortBy(_.data.lrn)
              .slice(startPosition, maxRows + startPosition)
        }
      }

      "LRN, BusinessName, exciseRegistrationNumber and WarehouseERN matched" must {

        val searchString = "ABCDEFGHI"

        "return paginated drafts related to that ERN where the search term also matches against LRN and Business Name" in {

          val searchOptions = GetDraftMovementSearchOptions(searchString = Some(searchString))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 4
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchString)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchString)) ||
                  draft.data.consigneeERN.exists(_.contains(searchString)) ||
                  draft.data.destinationWarehouseERN.exists(_.contains(searchString))
              )
              .sortBy(_.lastUpdated)
              .reverse
              .take(GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS)
        }

        "return paginated drafts related to that ERN where the search term also matches against LRN (sorted asecnding and different start position)" in {

          val startPosition = 2
          val maxRows = 10
          val searchOptions = GetDraftMovementSearchOptions(
            searchString = Some(searchString),
            sortField = LRN,
            sortOrder = Ascending,
            startPosition = startPosition,
            maxRows = maxRows
          )

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 4
          result.foundDrafts shouldBe
            insertedDrafts
              .filter(_.ern == testErn)
              .filter(draft =>
                draft.data.lrn.exists(_.contains(searchString)) ||
                  draft.data.consigneeBusinessName.exists(_.contains(searchString)) ||
                  draft.data.consigneeERN.exists(_.contains(searchString)) ||
                  draft.data.destinationWarehouseERN.exists(_.contains(searchString))
              )
              .sortBy(_.data.lrn)
              .slice(startPosition, maxRows + startPosition)
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
          result.foundDrafts shouldBe
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

        result.count shouldBe 2
        result.foundDrafts shouldBe
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

        result.count shouldBe 2
        result.foundDrafts shouldBe
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

        result.count shouldBe 2
        result.foundDrafts shouldBe
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

        result.count shouldBe 2
        result.foundDrafts shouldBe
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
        result.foundDrafts shouldBe
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
          result.foundDrafts shouldBe
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
        result.foundDrafts shouldBe Seq()
      }
    }

    "searching for drafts which match specified Destination Types" must {

      "only return drafts where the Destination Type matches" when {

        "matches multiple drafts with a destination type that relates to the overarching movement scenario" in {

          val destinationTypes = Seq(TaxWarehouse, Export)
          val searchOptions = GetDraftMovementSearchOptions(destinationTypes = Some(destinationTypes))

          lazy val result = repository.searchDrafts(testErn, searchOptions).futureValue

          result.count shouldBe 3
          result.foundDrafts shouldBe
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
          result.foundDrafts shouldBe
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
        result.foundDrafts shouldBe Seq()
      }
    }
  }

  private implicit class JsObjectExtension(json: JsObject) {

    implicit def read(path: JsPath)(implicit reads: Reads[String]): Option[String] = path.readNullable[String].reads(json).get

    implicit def lrn: Option[String] = read(__ \ "info" \ "localReferenceNumber")
    implicit def destinationType: Option[String] = read(__ \ "info" \ "destinationType")
    implicit def consigneeBusinessName: Option[String] = read(__ \ "consignee" \ "businessName")
    implicit def consigneeERN: Option[String] = read(__ \ "consignee" \ "exciseRegistrationNumber")
    implicit def destinationWarehouseERN: Option[String] = read(__ \ "destination" \ "destinationWarehouseExcise")
    implicit def dispatchDate: Option[LocalDate] = read(__ \ "info" \ "dispatchDetails" \ "date").map(LocalDate.parse)
    implicit def itemEpcs: Option[Seq[String]] = {
      val reads = (__ \ "items" \ "addedItems").readNullable[Seq[String]](Reads.seq((__ \ "itemExciseProductCode").read[String]))
      json.as[Option[Seq[String]]](reads)
    }
  }

  def userAnswers(ern: String = testErn,
                  draftId: String = testDraftId,
                  data: JsObject = Json.obj("foo" -> "bar"),
                  submissionFailures: Seq[MovementSubmissionFailure] = Seq(),
                  timestamp: Instant = Instant.ofEpochSecond(1)): CreateMovementUserAnswers =
    CreateMovementUserAnswers(
      ern = ern,
      draftId = draftId,
      data = data,
      submissionFailures = submissionFailures,
      lastUpdated = timestamp,
      hasBeenSubmitted = true,
      submittedDraftId = Some(testDraftId)
    )

  private def generateNDrafts(n: Int = 1): Seq[CreateMovementUserAnswers] = (1 to n).flatMap { i =>
    val draft = userAnswers(
      draftId = s"draft$i",
      data = Json.obj("info" -> Json.obj(
        "localReferenceNumber" -> s"LRN${Random.nextInt(i * 1000)}"
      )),
      timestamp = Instant.ofEpochSecond(i * 5)
    )
    Seq(draft, generateRandomDraft(i))
  }

  private def generateRandomDraft(i: Int): CreateMovementUserAnswers =
    CreateMovementUserAnswers(
      ern = s"GB${Random.nextInt(i * 1000)}",
      draftId = UUID.randomUUID().toString,
      data = Json.obj("info" -> Json.obj(
        "localReferenceNumber" -> s"LRN${Random.nextInt(i * 1000)}"
      )),
      submissionFailures = Seq(),
      lastUpdated = Instant.ofEpochSecond(i * 2),
      hasBeenSubmitted = false,
      submittedDraftId = None
    )
}
