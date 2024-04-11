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

package uk.gov.hmrc.emcstfe.models.request

import play.api.mvc.QueryStringBindable
import uk.gov.hmrc.emcstfe.models.common.{Descending, DestinationType, SortOrdering}
import uk.gov.hmrc.emcstfe.models.request.GetDraftMovementSearchOptions.DEFAULT_START_POSITION

import java.time.LocalDate

case class GetDraftMovementSearchOptions(sortField: GetDraftMovementSortField = GetDraftMovementSearchOptions.DEFAULT_SORT_FIELD,
                                         sortOrder: SortOrdering = GetDraftMovementSearchOptions.DEFAULT_SORT_ORDER,
                                         startPosition: Int = DEFAULT_START_POSITION,
                                         maxRows: Int = GetDraftMovementSearchOptions.DEFAULT_MAX_ROWS,
                                         searchTerm: Option[String] = None,
                                         draftHasErrors: Option[Boolean] = None,
                                         destinationTypes: Option[Seq[DestinationType]] = None,
                                         dateOfDispatchFrom: Option[LocalDate] = None,
                                         dateOfDispatchTo: Option[LocalDate] = None,
                                         exciseProductCode: Option[String] = None)

object GetDraftMovementSearchOptions {

  val DEFAULT_SORT_FIELD: GetDraftMovementSortField = LastUpdatedDate
  val DEFAULT_SORT_ORDER: SortOrdering = Descending
  val DEFAULT_START_POSITION: Int = 0
  val DEFAULT_MAX_ROWS: Int = 10

  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String],
                                 booleanBinder: QueryStringBindable[Boolean],
                                 sortOrderBinder: QueryStringBindable[SortOrdering],
                                 sortFieldBinder: QueryStringBindable[GetDraftMovementSortField],
                                 destinationTypeBinder: QueryStringBindable[Seq[DestinationType]]): QueryStringBindable[GetDraftMovementSearchOptions] =
    new QueryStringBindable[GetDraftMovementSearchOptions] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, GetDraftMovementSearchOptions]] = {
        Some(for {
          sortField <- sortFieldBinder.bind(key + ".sortField", params).getOrElse(Right(DEFAULT_SORT_FIELD))
          sortOrder <- sortOrderBinder.bind(key + ".sortOrder", params).getOrElse(Right(DEFAULT_SORT_ORDER))
          startPosition <- intBinder.bind(key + ".startPosition", params).getOrElse(Right(DEFAULT_START_POSITION))
          maxRows <- intBinder.bind(key + ".maxRows", params).getOrElse(Right(DEFAULT_MAX_ROWS))
          searchTerm <- stringBinder.bind(key + ".searchTerm", params).map(_.map(Some(_))).getOrElse(Right(None))
          draftHasErrors <- booleanBinder.bind(key + ".draftHasErrors", params).map(_.map(Some(_))).getOrElse(Right(None))
          destinationTypes <- destinationTypeBinder.bind(key + ".destinationType", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfDispatchFrom <- stringBinder.bind(key + ".dateOfDispatchFrom", params).map(_.map(date => Some(LocalDate.parse(date)))).getOrElse(Right(None))
          dateOfDispatchTo <- stringBinder.bind(key + ".dateOfDispatchTo", params).map(_.map(date => Some(LocalDate.parse(date)))).getOrElse(Right(None))
          exciseProductCode <- stringBinder.bind(key + ".exciseProductCode", params).map(_.map(Some(_))).getOrElse(Right(None))
        } yield {
          GetDraftMovementSearchOptions(
            sortField = sortField,
            sortOrder = sortOrder,
            startPosition = startPosition,
            maxRows = maxRows,
            searchTerm = searchTerm,
            draftHasErrors = draftHasErrors,
            destinationTypes = destinationTypes,
            dateOfDispatchFrom = dateOfDispatchFrom,
            dateOfDispatchTo = dateOfDispatchTo,
            exciseProductCode = exciseProductCode
          )
        })
      }

      override def unbind(key: String, searchOptions: GetDraftMovementSearchOptions): String =
        Seq(
          Some(sortFieldBinder.unbind(key + ".sortField", searchOptions.sortField)),
          Some(sortOrderBinder.unbind(key + ".sortOrder", searchOptions.sortOrder)),
          Some(intBinder.unbind(key + ".startPosition", searchOptions.startPosition)),
          Some(intBinder.unbind(key + ".maxRows", searchOptions.maxRows)),
          searchOptions.searchTerm.map(stringBinder.unbind(key + ".searchTerm", _)),
          searchOptions.draftHasErrors.map(booleanBinder.unbind(key + ".draftHasErrors", _)),
          searchOptions.destinationTypes.map(destinationTypeBinder.unbind(key + ".destinationType", _)),
          searchOptions.dateOfDispatchFrom.map(date => stringBinder.unbind(key + ".dateOfDispatchFrom", date.toString)),
          searchOptions.dateOfDispatchTo.map(date => stringBinder.unbind(key + ".dateOfDispatchTo", date.toString)),
          searchOptions.exciseProductCode.map(stringBinder.unbind(key + ".exciseProductCode", _)),
        ).flatten.mkString("&")
    }
}


