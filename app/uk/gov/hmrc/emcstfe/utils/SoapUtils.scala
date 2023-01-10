package uk.gov.hmrc.emcstfe.utils

import scala.util.Try
import scala.xml.{Elem, XML}

object SoapUtils {
  def extractFromSoap(xml: Elem): Try[Elem] = Try {
    val cdata = (xml \\ "OperationResponse" \ "Results" \ "Result").text
    XML.loadString(cdata)
  }
}
