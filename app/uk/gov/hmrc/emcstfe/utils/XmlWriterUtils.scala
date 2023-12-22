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

package uk.gov.hmrc.emcstfe.utils

import scala.xml.NodeSeq

trait XmlWriterUtils extends Logging {

  implicit class OptionExtensions[T](x: Option[T]) {
    def mapNodeSeq(f: T => NodeSeq): NodeSeq = x.map(f).getOrElse(NodeSeq.Empty)
  }

  implicit class BooleanExtensions(x: Boolean) {
    def toFlag: String = if (x) "1" else "0"
  }

  implicit class StringExtensions(x: String) {
    def fromFlag: Boolean = x == "1"
  }

  def trimWhitespaceFromXml(xml: NodeSeq): NodeSeq =
    xml.headOption.fold(NodeSeq.Empty)(scala.xml.Utility.trim)

}
