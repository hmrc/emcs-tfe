/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.models.request

import play.api.mvc.QueryStringBindable

case class GetMovementListSearchOptions(traderRole: String = GetMovementListSearchOptions.DEFAULT_TRADER_ROLE,
                                        sortField: String = GetMovementListSearchOptions.DEFAULT_SORT_FIELD,
                                        sortOrder: String = GetMovementListSearchOptions.DEFAULT_SORT_ORDER,
                                        startPosition: Int = GetMovementListSearchOptions.DEFAULT_START_POSITION,
                                        maxRows: Int = GetMovementListSearchOptions.DEFAULT_MAX_ROWS)

object GetMovementListSearchOptions {

  val DEFAULT_TRADER_ROLE: String = "Consignor and/or Consignee"
  val DEFAULT_SORT_FIELD: String = "DateReceived"
  val DEFAULT_SORT_ORDER: String = "D"
  val DEFAULT_START_POSITION: Int = 1
  val DEFAULT_MAX_ROWS: Int = 30

  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[GetMovementListSearchOptions] =
    new QueryStringBindable[GetMovementListSearchOptions] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, GetMovementListSearchOptions]] = {
        Some(for {
          traderRole <- stringBinder.bind(key + ".traderRole", params).getOrElse(Right(DEFAULT_TRADER_ROLE))
          sortField <- stringBinder.bind(key + ".sortField", params).getOrElse(Right(DEFAULT_SORT_FIELD))
          sortOrder <- stringBinder.bind(key + ".sortOrder", params).getOrElse(Right(DEFAULT_SORT_ORDER))
          startPosition <- intBinder.bind(key + ".startPosition", params).getOrElse(Right(DEFAULT_START_POSITION))
          maxRows <- intBinder.bind(key + ".maxRows", params).getOrElse(Right(DEFAULT_MAX_ROWS))
        } yield {
          GetMovementListSearchOptions(traderRole, sortField, sortOrder, startPosition, maxRows)
        })
      }
      override def unbind(key: String, searchOptions: GetMovementListSearchOptions): String =
        Seq(
          stringBinder.unbind(key + ".traderRole", searchOptions.traderRole),
          stringBinder.unbind(key + ".sortField", searchOptions.sortField),
          stringBinder.unbind(key + ".sortOrder", searchOptions.sortOrder),
          intBinder.unbind(key + ".startPosition", searchOptions.startPosition),
          intBinder.unbind(key + ".maxRows", searchOptions.maxRows),
        ).mkString("&")
    }
}


