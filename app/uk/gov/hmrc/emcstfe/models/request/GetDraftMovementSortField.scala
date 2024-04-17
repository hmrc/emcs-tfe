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

import scala.util.Try

sealed trait GetDraftMovementSortField
case object LRN extends GetDraftMovementSortField {
  override def toString: String = "lrn"
}
case object LastUpdatedDate extends GetDraftMovementSortField {
  override def toString: String = "lastUpdated"
}

object GetDraftMovementSortField {

  def apply(sortField: String): GetDraftMovementSortField = sortField match {
    case "lrn" => LRN
    case "lastUpdated" => LastUpdatedDate
    case _ => throw new IllegalArgumentException(s"'$sortField' is not a valid sort field. Must be 'lrn' or 'lastUpdated'")
  }

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[GetDraftMovementSortField] =
    new QueryStringBindable[GetDraftMovementSortField] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, GetDraftMovementSortField]] =
        Try(stringBinder.bind(key, params).map(_.map(GetDraftMovementSortField.apply))).fold[Option[Either[String, GetDraftMovementSortField]]](
          e => Some(Left(e.getMessage)), identity
        )

      override def unbind(key: String, sortField: GetDraftMovementSortField): String =
        stringBinder.unbind(key, sortField.toString)
    }
}


