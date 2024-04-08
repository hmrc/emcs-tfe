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

case class GetMovementListSearchOptions(traderRole: Option[String] = None,
                                        sortField: Option[String] = None,
                                        sortOrder: String = GetMovementListSearchOptions.DEFAULT_SORT_ORDER,
                                        startPosition: Option[Int] = None,
                                        maxRows: Int = GetMovementListSearchOptions.DEFAULT_MAX_ROWS,
                                        arc: Option[String] = None,
                                        otherTraderId: Option[String] = None,
                                        lrn: Option[String] = None,
                                        dateOfDispatchFrom: Option[String] = None,
                                        dateOfDispatchTo: Option[String] = None,
                                        dateOfReceiptFrom: Option[String] = None,
                                        dateOfReceiptTo: Option[String] = None,
                                        countryOfOrigin: Option[String] = None,
                                        movementStatus: Option[String] = None,
                                        transporterTraderName: Option[String] = None,
                                        undischargedMovements: Option[String] = None,
                                        exciseProductCode: Option[String] = None
                                       )

object GetMovementListSearchOptions {

  val DEFAULT_TRADER_ROLE: String = "Consignor and/or Consignee"
  val EIS_DEFAULT_TRADER_ROLE: String = "both"
  val DEFAULT_SORT_FIELD: String = "DateReceived"
  val EIS_DEFAULT_SORT_FIELD: String = "dateofdispatch"
  val DEFAULT_SORT_ORDER: String = "D"
  val DEFAULT_START_POSITION: Int = 1
  val EIS_DEFAULT_START_POSITION: Int = 0
  val DEFAULT_MAX_ROWS: Int = 30

  implicit def queryStringBinder(implicit intBinder: QueryStringBindable[Int],
                                 stringBinder: QueryStringBindable[String]): QueryStringBindable[GetMovementListSearchOptions] =
    new QueryStringBindable[GetMovementListSearchOptions] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, GetMovementListSearchOptions]] = {
        Some(for {
          traderRole <- stringBinder.bind(key + ".traderRole", params).map(_.map(Some(_))).getOrElse(Right(None))
          sortField <- stringBinder.bind(key + ".sortField", params).map(_.map(Some(_))).getOrElse(Right(None))
          sortOrder <- stringBinder.bind(key + ".sortOrder", params).getOrElse(Right(DEFAULT_SORT_ORDER))
          startPosition <- intBinder.bind(key + ".startPosition", params).map(_.map(Some(_))).getOrElse(Right(None))
          maxRows <- intBinder.bind(key + ".maxRows", params).getOrElse(Right(DEFAULT_MAX_ROWS))
          arc <- stringBinder.bind(key + ".arc", params).map(_.map(Some(_))).getOrElse(Right(None))
          otherTraderId <- stringBinder.bind(key + ".otherTraderId", params).map(_.map(Some(_))).getOrElse(Right(None))
          lrn <- stringBinder.bind(key + ".lrn", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfDispatchFrom <- stringBinder.bind(key + ".dateOfDispatchFrom", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfDispatchTo <- stringBinder.bind(key + ".dateOfDispatchTo", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfReceiptFrom <- stringBinder.bind(key + ".dateOfReceiptFrom", params).map(_.map(Some(_))).getOrElse(Right(None))
          dateOfReceiptTo <- stringBinder.bind(key + ".dateOfReceiptTo", params).map(_.map(Some(_))).getOrElse(Right(None))
          countryOfOrigin <- stringBinder.bind(key + ".countryOfOrigin", params).map(_.map(Some(_))).getOrElse(Right(None))
          movementStatus <- stringBinder.bind(key + ".movementStatus", params).map(_.map(Some(_))).getOrElse(Right(None))
          transporterTraderName <- stringBinder.bind(key + ".transporterTraderName", params).map(_.map(Some(_))).getOrElse(Right(None))
          undischargedMovements <- stringBinder.bind(key + ".undischargedMovements", params).map(_.map(Some(_))).getOrElse(Right(None))
          exciseProductCode <- stringBinder.bind(key + ".exciseProductCode", params).map(_.map(Some(_))).getOrElse(Right(None))
        } yield {
          GetMovementListSearchOptions(traderRole, sortField, sortOrder, startPosition, maxRows, arc, otherTraderId, lrn, dateOfDispatchFrom, dateOfDispatchTo, dateOfReceiptFrom, dateOfReceiptTo, countryOfOrigin, movementStatus, transporterTraderName, undischargedMovements, exciseProductCode)
        })
      }

      override def unbind(key: String, searchOptions: GetMovementListSearchOptions): String =
        Seq(
          searchOptions.traderRole.map(stringBinder.unbind(key + ".traderRole", _)),
          searchOptions.sortField.map(stringBinder.unbind(key + ".sortField", _)),
          searchOptions.startPosition.map(intBinder.unbind(key + ".startPosition", _)),
          Some(stringBinder.unbind(key + ".sortOrder", searchOptions.sortOrder)),
          Some(intBinder.unbind(key + ".maxRows", searchOptions.maxRows)),
          searchOptions.arc.map(stringBinder.unbind(key + ".arc", _)),
          searchOptions.otherTraderId.map(stringBinder.unbind(key + ".otherTraderId", _)),
          searchOptions.lrn.map(stringBinder.unbind(key + ".lrn", _)),
          searchOptions.dateOfDispatchFrom.map(stringBinder.unbind(key + ".dateOfDispatchFrom", _)),
          searchOptions.dateOfDispatchTo.map(stringBinder.unbind(key + ".dateOfDispatchTo", _)),
          searchOptions.dateOfReceiptFrom.map(stringBinder.unbind(key + ".dateOfReceiptFrom", _)),
          searchOptions.dateOfReceiptTo.map(stringBinder.unbind(key + ".dateOfReceiptTo", _)),
          searchOptions.countryOfOrigin.map(stringBinder.unbind(key + ".countryOfOrigin", _)),
          searchOptions.movementStatus.map(stringBinder.unbind(key + ".movementStatus", _)),
          searchOptions.transporterTraderName.map(stringBinder.unbind(key + ".transporterTraderName", _)),
          searchOptions.undischargedMovements.map(stringBinder.unbind(key + ".undischargedMovements", _)),
          searchOptions.exciseProductCode.map(stringBinder.unbind(key + ".exciseProductCode", _)),
        ).flatten.mkString("&")
    }
}


