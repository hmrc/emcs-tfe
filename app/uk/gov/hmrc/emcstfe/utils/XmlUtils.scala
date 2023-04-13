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

import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{GenericParseError, MarkPlacementError, MinifyXmlError, SoapExtractionError, XmlParseError}

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Right, Success, Try}
import scala.xml.{Elem, Node, NodeSeq, XML}

@Singleton
class XmlUtils @Inject()(hmrcMarkUtil: HMRCMarkUtil) extends Logging {

  def readXml(xmlString: String): Either[ErrorResponse, NodeSeq] = {
    Try {
      XML.loadString(xmlString)
    } match {
      case Failure(exception) =>
        logger.warn("[readXml] Error converting String to NodeSeq", exception)
        Left(XmlParseError(Seq(GenericParseError(exception.getMessage))))
      case Success(value) => Right(value)
    }
  }
  def extractFromSoap(xml: Elem): Either[ErrorResponse, NodeSeq] = Try {
      val cdata = (xml \\ "OperationResponse" \\ "Results" \\ "Result").text
      XML.loadString(cdata)
    } match {
      case Failure(exception) =>
        logger.warn("[extractFromSoap] Error extracting response body from SOAP wrapper", exception)
        (xml \\ "Errors" \\ "Error").foreach(error => logger.warn(error.text))
        Left(SoapExtractionError)
      case Success(value) => Right(value)
    }

  private def addMarkToXml(xml: NodeSeq, mark: String): Either[ErrorResponse, NodeSeq] = {

    def recurse(xml: NodeSeq): NodeSeq = xml.foldLeft(NodeSeq.Empty) {
      (acc: NodeSeq, curr: Node) =>
        curr match {
          case elem: Elem if elem.prefix == "ns"
            && elem.label == "Info"
            && elem.scope.getURI("ns") == "http://www.hmrc.gov.uk/ws/info-header/1"
            && elem.scope.getURI("soapenv") == "http://www.w3.org/2003/05/soap-envelope" => acc ++ elem ++ <Security xmlns="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
            <BinarySecurityToken ValueType="http://www.hmrc.gov.uk#MarkToken">
              {mark}
            </BinarySecurityToken>
          </Security>
          case elem: Elem => acc ++ Elem(
            elem.prefix,
            elem.label,
            elem.attributes,
            elem.scope,
            elem.minimizeEmpty,
            recurse(NodeSeq.fromSeq(elem.child)): _*)
          case node => acc ++ node
        }
    }

    if ((xml \\ "Envelope" \ "Header" \ "Info").isEmpty) Left(MarkPlacementError) else Right(recurse(xml))
  }

  def trimWhitespaceFromXml(xml: NodeSeq): Either[ErrorResponse, NodeSeq] = Try {
    scala.xml.Utility.trim(xml.head)
  } match {
    case Failure(exception) =>
      logger.warn(exception.getMessage)
      Left(MinifyXmlError)
    case Success(value) => Right(value)
  }

  def prepareXmlForSubmission(xml: Elem): Either[ErrorResponse, String] = for {
    mark <- hmrcMarkUtil.createHmrcMark(xml)
    xmlWithMarkAdded <- addMarkToXml(xml, mark)
    trimmedXml <- trimWhitespaceFromXml(xmlWithMarkAdded)
  } yield {
    s"""<?xml version='1.0' encoding='UTF-8'?>
       |${trimmedXml.toString()}""".stripMargin
  }
}

