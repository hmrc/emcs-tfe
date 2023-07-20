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
import uk.gov.hmrc.emcstfe.models.common.Flag

import scala.xml.{Elem, NodeSeq}

case class BodyEadEsadModel(
                             bodyRecordUniqueReference: String,
                             exciseProductCode: String,
                             cnCode: String,
                             quantity: BigDecimal,
                             grossMass: BigDecimal,
                             netMass: BigDecimal,
                             alcoholicStrengthByVolumeInPercentage: Option[BigDecimal],
                             degreePlato: Option[BigDecimal],
                             fiscalMark: Option[String],
                             fiscalMarkUsedFlag: Option[Flag],
                             designationOfOrigin: Option[String],
                             sizeOfProducer: Option[Int],
                             density: Option[BigDecimal],
                             commercialDescription: Option[String],
                             brandNameOfProducts: Option[String],
                             maturationPeriodOrAgeOfProducts: Option[String],
                             `package`: Seq[PackageModel],
                             wineProduct: Option[WineProductModel]
                           ) extends CreateMovement {
  def toXml: Elem = <urn:BodyEadEsad>
    <BodyRecordUniqueReference>{bodyRecordUniqueReference}</BodyRecordUniqueReference>
    <ExciseProductCode>{exciseProductCode}</ExciseProductCode>
    <CnCode>{cnCode}</CnCode>
    <Quantity>{quantity}</Quantity>
    <GrossMass>{grossMass}</GrossMass>
    <NetMass>{netMass}</NetMass>
    {alcoholicStrengthByVolumeInPercentage.map(value => <AlcoholicStrengthByVolumeInPercentage>{value}</AlcoholicStrengthByVolumeInPercentage>).getOrElse(NodeSeq.Empty)}
    {degreePlato.map(value => <DegreePlato>{value}</DegreePlato>).getOrElse(NodeSeq.Empty)}
    {fiscalMark.map(value => <FiscalMark language="en">{value}</FiscalMark>).getOrElse(NodeSeq.Empty)}
    {fiscalMarkUsedFlag.map(value => <FiscalMarkUsedFlag>{value.toString}</FiscalMarkUsedFlag>).getOrElse(NodeSeq.Empty)}
    {designationOfOrigin.map(value => <DesignationOfOrigin language="en">{value}</DesignationOfOrigin>).getOrElse(NodeSeq.Empty)}
    {sizeOfProducer.map(value => <SizeOfProducer>{value}</SizeOfProducer>).getOrElse(NodeSeq.Empty)}
    {density.map(value => <Density>{value}</Density>).getOrElse(NodeSeq.Empty)}
    {commercialDescription.map(value => <CommercialDescription language="en">{value}</CommercialDescription>).getOrElse(NodeSeq.Empty)}
    {brandNameOfProducts.map(value => <BrandNameOfProducts language="en">{value}</BrandNameOfProducts>).getOrElse(NodeSeq.Empty)}
    {maturationPeriodOrAgeOfProducts.map(value => <MaturationPeriodOrAgeOfProducts language="en">{value}</MaturationPeriodOrAgeOfProducts>).getOrElse(NodeSeq.Empty)}
    {`package`.map(_.toXml)}
    {wineProduct.map(_.toXml).getOrElse(NodeSeq.Empty)}
  </urn:BodyEadEsad>
}

object BodyEadEsadModel {
  implicit val fmt: OFormat[BodyEadEsadModel] = Json.format
}
