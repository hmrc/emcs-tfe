package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class Ead(
                localReferenceNumber: String,
                invoiceNumber: String,
                invoiceDate: Option[String],
                originTypeCode: String,
                dateOfDispatch: String,
                timeOfDispatch: Option[String],
                upstreamArc: Option[String],
                importSadNumber: Option[Seq[String]]
              )

object Ead {
  def fromXml(xml: NodeSeq): Ead = {
    val localReferenceNumber: String = (xml \\ "LocalReferenceNumber").text
    val invoiceNumber: String = (xml \\ "InvoiceNumber").text
    val invoiceDate: Option[String] = (xml \\ "InvoiceDate").headOption.map(_.text)
    val originTypeCode: String = (xml \\ "OriginTypeCode").text
    val dateOfDispatch: String = (xml \\ "DateOfDispatch").text
    val timeOfDispatch: Option[String] = (xml \\ "TimeOfDispatch").headOption.map(_.text)
    val upstreamArc: Option[String] = (xml \\ "UpstreamArc").headOption.map(_.text)
    val importSadNumber: Option[Seq[String]] = {
      val nodeSeq = (xml \\ "ImportSad")
      if(nodeSeq.length > 0) {
        Some(nodeSeq.map(el => (el \\ "ImportSadNumber").text))
      } else None
    }
    Ead(
      localReferenceNumber = localReferenceNumber,
      invoiceNumber = invoiceNumber,
      invoiceDate = invoiceDate,
      originTypeCode = originTypeCode,
      dateOfDispatch = dateOfDispatch,
      timeOfDispatch = timeOfDispatch,
      upstreamArc = upstreamArc,
      importSadNumber = importSadNumber
    )
  }

  implicit val writes: OWrites[Ead] = Json.writes
}
