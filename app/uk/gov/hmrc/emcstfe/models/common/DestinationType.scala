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

import play.api.mvc.QueryStringBindable

import scala.util.Try

sealed trait DestinationType {
  val movementScenarios: Seq[String]
}

object DestinationType extends Enumerable.Implicits {

  case object TaxWarehouse extends WithName("1") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("euTaxWarehouse", "gbTaxWarehouse", "niTaxWarehouse")
  }

  case object RegisteredConsignee extends WithName("2") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("registeredConsignee")
  }

  case object TemporaryRegisteredConsignee extends WithName("3") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("temporaryRegisteredConsignee")
  }

  case object DirectDelivery extends WithName("4") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("directDelivery")
  }

  case object ExemptedOrganisations extends WithName("5") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("exemptedOrganisation")
  }

  case object Export extends WithName("6") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("exportWithCustomsDeclarationLodgedInTheUk", "exportWithCustomsDeclarationLodgedInTheEu")
  }

  case object UnknownDestination extends WithName("8") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("unknownDestination")
  }

  case object CertifiedConsignee extends WithName("9") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("certifiedConsignee")
  }

  case object TemporaryCertifiedConsignee extends WithName("10") with DestinationType {
    override val movementScenarios: Seq[String] = Seq("temporaryCertifiedConsignee")
  }

  case object ReturnToThePlaceOfDispatchOfTheConsignor extends WithName("11") with DestinationType {
    override val movementScenarios: Seq[String] = Seq()
  }


  val values: Seq[DestinationType] = Seq(
    TaxWarehouse,
    RegisteredConsignee,
    TemporaryRegisteredConsignee,
    DirectDelivery,
    ExemptedOrganisations,
    Export,
    UnknownDestination,
    CertifiedConsignee,
    TemporaryCertifiedConsignee,
    ReturnToThePlaceOfDispatchOfTheConsignor
  )

  implicit val enumerable: Enumerable[DestinationType] =
    Enumerable(values.map(v => v.toString -> v): _*)

  def destinationType(code: String): DestinationType =
    values.find(_.toString == code).getOrElse(throw new IllegalArgumentException(s"Destination code of '$code' could not be mapped to a valid Destination Type"))

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[Seq[DestinationType]] =
    new QueryStringBindable[Seq[DestinationType]] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Seq[DestinationType]]] = {
        params.get(key).map { destinationTypeCodes =>
          Try(destinationTypeCodes.map(destinationType)).fold[Either[String, Seq[DestinationType]]](
            e => Left(e.getMessage),
            Right(_)
          )
        }
      }

      override def unbind(key: String, destinations: Seq[DestinationType]): String =
        destinations.map(destinationType =>
          stringBinder.unbind(key, destinationType.toString)
        ).mkString("&")
    }
}
