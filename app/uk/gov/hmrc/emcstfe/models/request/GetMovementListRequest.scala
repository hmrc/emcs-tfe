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

import uk.gov.hmrc.emcstfe.models.request.GetMovementListSearchOptions.{EIS_DEFAULT_SORT_FIELD, EIS_DEFAULT_START_POSITION, EIS_DEFAULT_TRADER_ROLE}
import uk.gov.hmrc.emcstfe.models.request.eis.{EisConsumptionRequest, Source}

case class GetMovementListRequest(exciseRegistrationNumber: String,
                                  searchOptions: GetMovementListSearchOptions) extends EisConsumptionRequest {

  override def metricName = "get-movement-list"

  override val queryParams: Seq[(String, String)] = Seq(
    "exciseregistrationnumber" -> Some(exciseRegistrationNumber),
    "traderrole" -> Some(searchOptions.traderRole.getOrElse(EIS_DEFAULT_TRADER_ROLE).toLowerCase),
    "sortfield" -> Some(searchOptions.sortField.getOrElse(EIS_DEFAULT_SORT_FIELD).toLowerCase),
    "sortorder" -> Some(searchOptions.sortOrder),
    "startposition" -> Some(searchOptions.startPosition.getOrElse(EIS_DEFAULT_START_POSITION).toString),
    "maxnotoreturn" -> Some(searchOptions.maxRows.toString),
    "arc" -> searchOptions.arc,
    "othertraderid" -> searchOptions.otherTraderId,
    "localreferencenumber" -> searchOptions.lrn,
    "dateofdispatchfrom" -> searchOptions.dateOfDispatchFrom,
    "dateofdispatchto" -> searchOptions.dateOfDispatchTo,
    "dateofreceiptfrom" -> searchOptions.dateOfReceiptFrom,
    "dateofreceiptto" -> searchOptions.dateOfReceiptTo,
    "countryoforigin" -> searchOptions.countryOfOrigin,
    "movementstatus" -> searchOptions.movementStatus,
    "transportertradername" -> searchOptions.transporterTraderName,
    "undischargedmovements" -> searchOptions.undischargedMovements,
    "exciseproductcode" -> searchOptions.exciseProductCode
  ).collect { case (key, Some(value)) => key -> value }

  override val source: Source = Source.TFE
}
