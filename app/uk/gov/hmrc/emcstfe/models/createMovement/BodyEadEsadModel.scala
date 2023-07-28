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
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
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
  def toXml(implicit request: UserRequest[_]): Elem = <urn:BodyEadEsad>
    <urn:BodyRecordUniqueReference>{bodyRecordUniqueReference.toString}</urn:BodyRecordUniqueReference>
    <urn:ExciseProductCode>{exciseProductCode}</urn:ExciseProductCode>
    <urn:CnCode>{cnCode}</urn:CnCode>
    <urn:Quantity>{quantity}</urn:Quantity>
    <urn:GrossMass>{grossMass}</urn:GrossMass>
    <urn:NetMass>{netMass}</urn:NetMass>
    {alcoholicStrengthByVolumeInPercentage.mapNodeSeq(value => <urn:AlcoholicStrengthByVolumeInPercentage>{value}</urn:AlcoholicStrengthByVolumeInPercentage>)}
    {degreePlato.mapNodeSeq(value => <urn:DegreePlato>{value}</urn:DegreePlato>)}
    {fiscalMark.mapNodeSeq(value => <urn:FiscalMark language="en">{value}</urn:FiscalMark>)}
    {fiscalMarkUsedFlag.mapNodeSeq(value => <urn:FiscalMarkUsedFlag>{value.toFlag}</urn:FiscalMarkUsedFlag>)}
    {designationOfOrigin.mapNodeSeq(value => <urn:DesignationOfOrigin language="en">{value}</urn:DesignationOfOrigin>)}
    {sizeOfProducer.mapNodeSeq(value => <urn:SizeOfProducer>{value}</urn:SizeOfProducer>)}
    {density.mapNodeSeq(value => <urn:Density>{value}</urn:Density>)}
    {commercialDescription.mapNodeSeq(value => <urn:CommercialDescription language="en">{value}</urn:CommercialDescription>)}
    {brandNameOfProducts.mapNodeSeq(value => <urn:BrandNameOfProducts language="en">{value}</urn:BrandNameOfProducts>)}
    {maturationPeriodOrAgeOfProducts.mapNodeSeq(value => <urn:MaturationPeriodOrAgeOfProducts language="en">{value}</urn:MaturationPeriodOrAgeOfProducts>)}
    {packages.map(_.toXml)}
    {wineProduct.mapNodeSeq(_.toXml)}
  </urn:BodyEadEsad>
}

object BodyEadEsadModel {
  implicit val fmt: OFormat[BodyEadEsadModel] = Json.format
}
