/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.support.UnitSpec

class GetMovementListSearchOptionsSpec extends UnitSpec {

  val request = GetMovementListRequest(exciseRegistrationNumber = "My ERN", GetMovementListSearchOptions())

  "GetMovementListSearchOptions" must {

    "construct with default options" in {

      GetMovementListSearchOptions() shouldBe GetMovementListSearchOptions(
        traderRole = "Consignor and/or Consignee",
        sortField = "DateReceived",
        sortOrder = "D",
        startPosition = 1,
        maxRows = 30
      )
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query params are supplied" in {
        GetMovementListSearchOptions.queryStringBinder.bind("search", Map()) shouldBe
          Some(Right(GetMovementListSearchOptions()))
      }

      "all query parameters are supplied" in {
        GetMovementListSearchOptions.queryStringBinder.bind("search", Map(
          "search.traderRole" -> Seq("foo"),
          "search.sortField" -> Seq("bar"),
          "search.sortOrder" -> Seq("wizz"),
          "search.startPosition" -> Seq("10"),
          "search.maxRows" -> Seq("99")
        )) shouldBe
          Some(Right(GetMovementListSearchOptions(
            traderRole = "foo",
            sortField = "bar",
            sortOrder = "wizz",
            startPosition = 10,
            maxRows = 99
          )))
      }

      "some query parameters are supplied" in {
        GetMovementListSearchOptions.queryStringBinder.bind("search", Map(
          "search.traderRole" -> Seq("foo"),
          "search.startPosition" -> Seq("10"),
        )) shouldBe
          Some(Right(GetMovementListSearchOptions(
            traderRole = "foo",
            sortField = GetMovementListSearchOptions.DEFAULT_SORT_FIELD,
            sortOrder = GetMovementListSearchOptions.DEFAULT_SORT_ORDER,
            startPosition = 10,
            maxRows = GetMovementListSearchOptions.DEFAULT_MAX_ROWS
          )))
      }
    }

    "unbind QueryString to URL format" in {
      GetMovementListSearchOptions.queryStringBinder.unbind("search", GetMovementListSearchOptions()) shouldBe
        "search.traderRole=Consignor+and%2For+Consignee" +
          "&search.sortField=DateReceived" +
          "&search.sortOrder=D" +
          "&search.startPosition=1" +
          "&search.maxRows=30"
    }
  }
}
