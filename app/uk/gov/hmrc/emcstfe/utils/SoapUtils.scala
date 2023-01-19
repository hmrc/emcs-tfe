/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.utils

import scala.util.Try
import scala.xml.{NodeSeq, XML}

object SoapUtils {
  def extractFromSoap(xml: NodeSeq): Try[NodeSeq] = Try {
    val cdata = (xml \\ "OperationResponse" \\ "Results" \\ "Result").text
    XML.loadString(cdata)
  }
}
