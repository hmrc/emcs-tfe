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

package uk.gov.hmrc.emcstfe.support

import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfe.models.request.eis.EisSubmissionRequest

import scala.xml.Utility.trim
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, NodeSeq, XML}

trait TestBaseSpec extends UnitSpec with MockFactory {
  implicit class TestXMLClassOps(n: NodeSeq) {
    def getMessageBody: NodeSeq = trim(XML.loadString((n \\ "Parameter" filter {
      _ \ "@Name" exists (_.text == "message")
    }).text))

    def getControlDocWithoutMessage: NodeSeq = {
      val removeMessageFromControl: RewriteRule = new RewriteRule {
        override def transform(n: Node): NodeSeq = n match {
          case e: Elem if (e \ "@Name").text == "message" & e.label == "Parameter" => NodeSeq.Empty
          case n => n
        }
      }
      new RuleTransformer(removeMessageFromControl).transform(n)
    }
  }

  def wrapInControlDoc(xml: Node)(implicit request: EisSubmissionRequest): Node =
    <con:Control xmlns:con="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
      <con:MetaData>
        <con:MessageId>
          {request.messageUUID}
        </con:MessageId>
        <con:Source>TFE</con:Source>
        <con:CorrelationId>
          {request.correlationUUID}
        </con:CorrelationId>
      </con:MetaData>
      <con:OperationRequest>
        <con:Parameters>
          <con:Parameter Name="ExciseRegistrationNumber">{request.exciseRegistrationNumber}</con:Parameter>
          {XML.loadString(s"""
                             |<con:Parameter Name="message">
                             |<![CDATA[$xml]]>
                             |</con:Parameter>
                             """.stripMargin)}
          </con:Parameters>
          <con:ReturnData/>
        </con:OperationRequest>
      </con:Control>
}
