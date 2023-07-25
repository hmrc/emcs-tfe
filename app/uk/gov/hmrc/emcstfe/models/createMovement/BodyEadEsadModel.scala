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

package uk.gov.hmrc.emcstfe.models.createMovement
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.emcstfe.models.common.XmlBaseModel
import uk.gov.hmrc.emcstfe.utils.XmlWriterUtils

import scala.xml.Elem

case class BodyEadEsadModel(
                             bodyRecordUniqueReference: Int,
                             exciseProductCode: String,
                             cnCode: String,
                             quantity: BigDecimal,
                             grossMass: BigDecimal,
                             netMass: BigDecimal,
                             alcoholicStrengthByVolumeInPercentage: Option[BigDecimal],
                             degreePlato: Option[BigDecimal],
                             fiscalMark: Option[String],
                             fiscalMarkUsedFlag: Option[Boolean],
                             designationOfOrigin: Option[String],
                             sizeOfProducer: Option[Int],
                             density: Option[BigDecimal],
                             commercialDescription: Option[String],
                             brandNameOfProducts: Option[String],
                             maturationPeriodOrAgeOfProducts: Option[String],
                             packages: Seq[PackageModel],
                             wineProduct: Option[WineProductModel]
                           ) extends XmlBaseModel with XmlWriterUtils {
  def toXml: Elem = <urn:BodyEadEsad>
    <BodyRecordUniqueReference>{bodyRecordUniqueReference}</BodyRecordUniqueReference>
    <ExciseProductCode>{exciseProductCode}</ExciseProductCode>
    <CnCode>{cnCode}</CnCode>
    <Quantity>{quantity}</Quantity>
    <GrossMass>{grossMass}</GrossMass>
    <NetMass>{netMass}</NetMass>
    {alcoholicStrengthByVolumeInPercentage.mapNodeSeq(value => <AlcoholicStrengthByVolumeInPercentage>{value}</AlcoholicStrengthByVolumeInPercentage>)}
    {degreePlato.mapNodeSeq(value => <DegreePlato>{value}</DegreePlato>)}
    {fiscalMark.mapNodeSeq(value => <FiscalMark language="en">{value}</FiscalMark>)}
    {fiscalMarkUsedFlag.mapNodeSeq(value => <FiscalMarkUsedFlag>{value.toFlag}</FiscalMarkUsedFlag>)}
    {designationOfOrigin.mapNodeSeq(value => <DesignationOfOrigin language="en">{value}</DesignationOfOrigin>)}
    {sizeOfProducer.mapNodeSeq(value => <SizeOfProducer>{value}</SizeOfProducer>)}
    {density.mapNodeSeq(value => <Density>{value}</Density>)}
    {commercialDescription.mapNodeSeq(value => <CommercialDescription language="en">{value}</CommercialDescription>)}
    {brandNameOfProducts.mapNodeSeq(value => <BrandNameOfProducts language="en">{value}</BrandNameOfProducts>)}
    {maturationPeriodOrAgeOfProducts.mapNodeSeq(value => <MaturationPeriodOrAgeOfProducts language="en">{value}</MaturationPeriodOrAgeOfProducts>)}
    {packages.map(_.toXml)}
    {wineProduct.mapNodeSeq(_.toXml)}
  </urn:BodyEadEsad>
}

object BodyEadEsadModel {
  implicit val fmt: OFormat[BodyEadEsadModel] = Json.format
}
