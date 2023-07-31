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

package uk.gov.hmrc.emcstfe.models.common

sealed trait TraderModelType
case object ConsigneeTrader extends TraderModelType
case object ConsignorTrader extends TraderModelType
case object PlaceOfDispatchTrader extends TraderModelType
case object DeliveryPlaceTrader extends TraderModelType
case object TransportTrader extends TraderModelType
case object GuarantorTrader extends TraderModelType