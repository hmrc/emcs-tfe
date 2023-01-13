package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class IE801Body(
                      consigneeTrader: Option[ConsigneeTrader],
                      exciseMovementEad: ExciseMovementEad,
                      consignorTrader: ConsignorTrader,
                      placeOfDispatchTrader: Option[PlaceOfDispatchTrader],
                      dispatchImportOffice: Option[DispatchImportOffice],
                      complementConsigneeTrader: Option[ComplementConsigneeTrader],
                      deliveryPlaceTrader: Option[DeliveryPlaceTrader],
                      deliveryPlaceCustomsOffice: Option[DeliveryPlaceCustomsOffice],
                      competentAuthorityDispatchOffice: CompetentAuthorityDispatchOffice,
                      transportArrangerTrader: Option[TransportArrangerTrader],
                      firstTransporterTrader: Option[FirstTransporterTrader],
                      documentCertificate: Option[Seq[DocumentCertificate]],
                      ead: Ead,
                      headerEad: HeaderEad,
                      transportMode: TransportMode,
                      movementGuarantee: MovementGuarantee,
                      bodyEad: Seq[BodyEad],
                      transportDetails: Seq[TransportDetails]
                    )

object IE801Body {
  def fromXml(xml: NodeSeq): IE801Body = {
    val consigneeTrader: Option[ConsigneeTrader] = (xml \\ "ConsigneeTrader").headOption.map(ConsigneeTrader.fromXml)
    val exciseMovementEad: ExciseMovementEad = ExciseMovementEad.fromXml(xml \\ "ExciseMovementEad")
    val consignorTrader: ConsignorTrader = ConsignorTrader.fromXml(xml \\ "ConsignorTrader")
    val placeOfDispatchTrader: Option[PlaceOfDispatchTrader] = (xml \\ "PlaceOfDispatchTrader").headOption.map(PlaceOfDispatchTrader.fromXml)
    val dispatchImportOffice: Option[DispatchImportOffice] = (xml \\ "DispatchImportOffice").headOption.map(DispatchImportOffice.fromXml)
    val complementConsigneeTrader: Option[ComplementConsigneeTrader] = (xml \\ "ComplementConsigneeTrader").headOption.map(ComplementConsigneeTrader.fromXml)
    val deliveryPlaceTrader: Option[DeliveryPlaceTrader] = (xml \\ "DeliveryPlaceTrader").headOption.map(DeliveryPlaceTrader.fromXml)
    val deliveryPlaceCustomsOffice: Option[DeliveryPlaceCustomsOffice] = (xml \\ "DeliveryPlaceCustomsOffice").headOption.map(DeliveryPlaceCustomsOffice.fromXml)
    val competentAuthorityDispatchOffice: CompetentAuthorityDispatchOffice = CompetentAuthorityDispatchOffice.fromXml(xml \\ "CompetentAuthorityDispatchOffice")
    val transportArrangerTrader: Option[TransportArrangerTrader] = (xml \\ "TransportArrangerTrader").headOption.map(TransportArrangerTrader.fromXml)
    val firstTransporterTrader: Option[FirstTransporterTrader] = (xml \\ "FirstTransporterTrader").headOption.map(FirstTransporterTrader.fromXml)
    val documentCertificate: Option[Seq[DocumentCertificate]] = {
      val nodeSeq = (xml \\ "DocumentCertificate")
      if(nodeSeq.length > 0) {
        Some(nodeSeq.map(DocumentCertificate.fromXml))
      } else None
    }
    val ead: Ead = Ead.fromXml(xml \\ "Ead")
    val headerEad: HeaderEad = HeaderEad.fromXml(xml \\ "HeaderEad")
    val transportMode: TransportMode = TransportMode.fromXml(xml \\ "TransportMode")
    val movementGuarantee: MovementGuarantee = MovementGuarantee.fromXml(xml \\ "MovementGuarantee")
    val bodyEad: Seq[BodyEad] = (xml \\ "BodyEad").map(BodyEad.fromXml)
    val transportDetails: Seq[TransportDetails] = (xml \\ "TransportDetails").map(TransportDetails.fromXml)

    IE801Body(
      consigneeTrader = consigneeTrader,
      exciseMovementEad = exciseMovementEad,
      consignorTrader = consignorTrader,
      placeOfDispatchTrader = placeOfDispatchTrader,
      dispatchImportOffice = dispatchImportOffice,
      complementConsigneeTrader = complementConsigneeTrader,
      deliveryPlaceTrader = deliveryPlaceTrader,
      deliveryPlaceCustomsOffice = deliveryPlaceCustomsOffice,
      competentAuthorityDispatchOffice = competentAuthorityDispatchOffice,
      transportArrangerTrader = transportArrangerTrader,
      firstTransporterTrader = firstTransporterTrader,
      documentCertificate = documentCertificate,
      ead = ead,
      headerEad = headerEad,
      transportMode = transportMode,
      movementGuarantee = movementGuarantee,
      bodyEad = bodyEad,
      transportDetails = transportDetails
    )
  }

  implicit val writes: OWrites[IE801Body] = Json.writes
}
