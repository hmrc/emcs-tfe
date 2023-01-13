package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class DocumentCertificate(
                                documentType: Option[String],
                                documentReference: Option[String],
                                documentDescription: Option[String],
                                referenceOfDocument: Option[String]
                              )

object DocumentCertificate {
  def fromXml(xml: NodeSeq): DocumentCertificate = {
    val documentType: Option[String] = (xml \\ "DocumentType").headOption.map(_.text)
    val documentReference: Option[String] = (xml \\ "DocumentReference").headOption.map(_.text)
    val documentDescription: Option[String] = (xml \\ "DocumentDescription").headOption.map(_.text)
    val referenceOfDocument: Option[String] = (xml \\ "ReferenceOfDocument").headOption.map(_.text)
    DocumentCertificate(
      documentType = documentType,
      documentReference = documentReference,
      documentDescription = documentDescription,
      referenceOfDocument = referenceOfDocument
    )
  }

  implicit val writes: OWrites[DocumentCertificate] = Json.writes
}
