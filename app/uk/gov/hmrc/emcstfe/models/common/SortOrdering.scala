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

sealed trait SortOrdering
case object Ascending extends SortOrdering {
  override def toString: String = "A"
}
case object Descending extends SortOrdering {
  override def toString: String = "D"
}

object SortOrdering {

  def apply(sortOrder: String): SortOrdering = sortOrder match {
    case "A" => Ascending
    case "D" => Descending
    case _ => throw new IllegalArgumentException(s"'$sortOrder' is not a valid sort order. Must be 'A' or 'D'")
  }

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[SortOrdering] =
    new QueryStringBindable[SortOrdering] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, SortOrdering]] =
        stringBinder.bind(key, params).map(_.map(SortOrdering.apply))

      override def unbind(key: String, sortOrdering: SortOrdering): String =
        stringBinder.unbind(key, sortOrdering.toString)
    }
}


