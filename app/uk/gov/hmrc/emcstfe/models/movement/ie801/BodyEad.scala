package uk.gov.hmrc.emcstfe.models.movement.ie801

import play.api.libs.json.{Json, OWrites}

import scala.xml.NodeSeq

case class BodyEad(
                    bodyRecordUniqueReference: String,
                    exciseProductCode: String,
                    cnCode: String,
                    quantity: String,
                    grossWeight: String,
                    netWeight: String,
                    alcoholicStrengthByVolumeInPercentage: Option[String],
                    degreePlato: Option[String],
                    fiscalMark: Option[String],
                    fiscalMarkUsedFlag: Option[String],
                    designationOfOrigin: Option[String],
                    sizeOfProducer: Option[String],
                    density: Option[String],
                    commercialDescription: Option[String],
                    brandNameOfProducts: Option[String],
                    `package`: Seq[EadPackage],
                    wineProduct: Option[WineProduct]
                  )

object BodyEad {
  def fromXml(xml: NodeSeq): BodyEad = {
    val bodyRecordUniqueReference: String = (xml \\ "BodyRecordUniqueReference").text
    val exciseProductCode: String = (xml \\ "ExciseProductCode").text
    val cnCode: String = (xml \\ "CnCode").text
    val quantity: String = (xml \\ "Quantity").text
    val grossWeight: String = (xml \\ "GrossWeight").text
    val netWeight: String = (xml \\ "NetWeight").text
    val alcoholicStrengthByVolumeInPercentage: Option[String] = (xml \\ "AlcoholicStrengthByVolumeInPercentage").headOption.map(_.text)
    val degreePlato: Option[String] = (xml \\ "DegreePlato").headOption.map(_.text)
    val fiscalMark: Option[String] = (xml \\ "FiscalMark").headOption.map(_.text)
    val fiscalMarkUsedFlag: Option[String] = (xml \\ "FiscalMarkUsedFlag").headOption.map(_.text)
    val designationOfOrigin: Option[String] = (xml \\ "DesignationOfOrigin").headOption.map(_.text)
    val sizeOfProducer: Option[String] = (xml \\ "SizeOfProducer").headOption.map(_.text)
    val density: Option[String] = (xml \\ "Density").headOption.map(_.text)
    val commercialDescription: Option[String] = (xml \\ "CommercialDescription").headOption.map(_.text)
    val brandNameOfProducts: Option[String] = (xml \\ "BrandNameOfProducts").headOption.map(_.text)
    val `package`: Seq[EadPackage] = (xml \\ "Package").map(EadPackage.fromXml)
    val wineProduct: Option[WineProduct] = (xml \\ "WineProduct").headOption.map(WineProduct.fromXml)
    BodyEad(
      bodyRecordUniqueReference = bodyRecordUniqueReference,
      exciseProductCode = exciseProductCode,
      cnCode = cnCode,
      quantity = quantity,
      grossWeight = grossWeight,
      netWeight = netWeight,
      alcoholicStrengthByVolumeInPercentage = alcoholicStrengthByVolumeInPercentage,
      degreePlato = degreePlato,
      fiscalMark = fiscalMark,
      fiscalMarkUsedFlag = fiscalMarkUsedFlag,
      designationOfOrigin = designationOfOrigin,
      sizeOfProducer = sizeOfProducer,
      density = density,
      commercialDescription = commercialDescription,
      brandNameOfProducts = brandNameOfProducts,
      `package` = `package`,
      wineProduct = wineProduct
    )
  }

  implicit val writes: OWrites[BodyEad] = Json.writes
}
