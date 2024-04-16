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

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsObject, JsPath, Json, Reads, __}
import uk.gov.hmrc.emcstfe.models.createMovement.submissionFailures.MovementSubmissionFailure
import uk.gov.hmrc.emcstfe.models.mongo.CreateMovementUserAnswers

import java.time.{Instant, LocalDate}
import java.util.UUID
import scala.util.Random

trait CreateMovementSearchFixture extends BaseFixtures with MovementSubmissionFailureFixtures {

  def userAnswers(ern: String = testErn,
                  draftId: String = testDraftId,
                  data: JsObject = Json.obj("foo" -> "bar"),
                  submissionFailures: Seq[MovementSubmissionFailure] = Seq(),
                  hasBeenSubmitted: Boolean = false,
                  timestamp: Instant = Instant.ofEpochSecond(1)): CreateMovementUserAnswers =
    CreateMovementUserAnswers(
      ern = ern,
      draftId = draftId,
      data = data,
      submissionFailures = submissionFailures,
      lastUpdated = timestamp,
      hasBeenSubmitted = hasBeenSubmitted,
      submittedDraftId = Some(testDraftId)
    )

  val searchableDrafts = Seq(
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
    ),
    userAnswers(
      draftId = UUID.randomUUID().toString,
      data = Json.obj(
        "info" -> Json.obj(
          "localReferenceNumber" -> "123456ABC",
          "dispatchDetails" -> Json.obj(
            "date" -> LocalDate.of(2024, 5, 1)
          ),
          "destinationType" -> "directDelivery"
        ),
        "consignee" -> Json.obj(
          "businessName" -> "Foo-123456ABC",
          "exciseRegistrationNumber" -> "ERN123456ABC"
        ),
        "dispatch" -> Json.obj(
          "dispatchWarehouseExcise" -> "ABCDEFGH012345600ERN"
        ),
        "destination" -> Json.obj(
          "destinationWarehouseExcise" -> "WarehouseERN"
        )
      ),
      timestamp = Instant.ofEpochSecond(8000)
    ),
    userAnswers(
      draftId = UUID.randomUUID().toString,
      data = Json.obj(
        "info" -> Json.obj(
          "localReferenceNumber" -> "123456ABC",
          "dispatchDetails" -> Json.obj(
            "date" -> LocalDate.of(2024, 5, 1)
          ),
          "destinationType" -> "directDelivery"
        ),
        "consignee" -> Json.obj(
          "businessName" -> "Foo-123456ABC",
          "exciseRegistrationNumber" -> "ERN123456ABC"
        ),
        "dispatch" -> Json.obj(
          "dispatchWarehouseExcise" -> "ABCDEFGH012345600ERN"
        ),
        "destination" -> Json.obj(
          "destinationWarehouseExcise" -> "WarehouseERN"
        )
      ),
      timestamp = Instant.ofEpochSecond(9000),
      hasBeenSubmitted = true
    )
  )

  implicit class JsObjectExtension(json: JsObject) {

    implicit def read(path: JsPath)(implicit reads: Reads[String]): Option[String] = path.readNullable[String].reads(json).get

    implicit def lrn: Option[String] = read(__ \ "info" \ "localReferenceNumber")

    implicit def destinationType: Option[String] = read(__ \ "info" \ "destinationType")

    implicit def consigneeBusinessName: Option[String] = read(__ \ "consignee" \ "businessName")

    implicit def consigneeERN: Option[String] = read(__ \ "consignee" \ "exciseRegistrationNumber")

    implicit def destinationWarehouseERN: Option[String] = read(__ \ "destination" \ "destinationWarehouseExcise")

    implicit def dispatchWarehouseERN: Option[String] = read(__ \ "dispatch" \ "dispatchWarehouseExcise")

    implicit def dispatchDate: Option[LocalDate] = read(__ \ "info" \ "dispatchDetails" \ "date").map(LocalDate.parse)

    implicit def itemEpcs: Option[Seq[String]] = {
      val reads = (__ \ "items" \ "addedItems").readNullable[Seq[String]](Reads.seq((__ \ "itemExciseProductCode").read[String]))
      json.as[Option[Seq[String]]](reads)
    }
  }

  def generateNDrafts(n: Int = 1): Seq[CreateMovementUserAnswers] = (1 to n).flatMap { i =>
    val draft = userAnswers(
      draftId = s"draft$i",
      data = Json.obj("info" -> Json.obj(
        "localReferenceNumber" -> s"LRN${Random.nextInt(i * 1000)}"
      )),
      timestamp = Instant.ofEpochSecond(i * 5)
    )
    Seq(draft, generateRandomDraft(i))
  }

  def generateRandomDraft(i: Int): CreateMovementUserAnswers =
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
