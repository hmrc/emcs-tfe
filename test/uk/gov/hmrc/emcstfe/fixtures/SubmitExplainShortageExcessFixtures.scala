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

package uk.gov.hmrc.emcstfe.fixtures

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.emcstfe.models.common.{AddressModel, SubmitterType, TraderModel}
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.{AnalysisModel, AttributesModel, BodyAnalysisModel, ExciseMovementModel, SubmitExplainShortageExcessModel}

import scala.xml.Elem

trait SubmitExplainShortageExcessFixtures extends ChRISResponsesFixture {

  object AttributesFixtures {
    val attributesModel: AttributesModel = AttributesModel(
      submitterType = SubmitterType.Consignor
    )

    val attributesXml: Elem = <urn:Attributes>
      <urn:SubmitterType>1</urn:SubmitterType>
    </urn:Attributes>

    val attributesJson: JsObject = Json.obj(
      "submitterType" -> "1"
    )
  }

  object ConsigneeTraderFixtures {
    val consigneeTraderModel: TraderModel = TraderModel(
      vatNumber = None,
      traderExciseNumber = None,
      traderId = Some("id"),
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("a postcode"),
        city = Some("a city")
      )),
      eoriNumber = Some("eori")
    )

    val consigneeTraderXml: Elem = <urn:ConsigneeTrader language="en">
      <urn:Traderid>id</urn:Traderid>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
      <urn:EoriNumber>eori</urn:EoriNumber>
    </urn:ConsigneeTrader>

    val consigneeTraderJson: JsObject = Json.obj(
      "traderId" -> "id",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "a postcode",
        "city" -> "a city",
      ),
      "eoriNumber" -> "eori"
    )
  }

  object ExciseMovementFixtures {
    val exciseMovementModel: ExciseMovementModel = ExciseMovementModel(
      arc = "01DE0000012345",
      sequenceNumber = "1"
    )

    val exciseMovementXml: Elem = <urn:ExciseMovement>
      <urn:AdministrativeReferenceCode>01DE0000012345</urn:AdministrativeReferenceCode>
      <urn:SequenceNumber>1</urn:SequenceNumber>
    </urn:ExciseMovement>

    val exciseMovementJson: JsObject = Json.obj(
      "arc" -> "01DE0000012345",
      "sequenceNumber" -> "1"
    )
  }

  object ConsignorTraderFixtures {
    val consignorTraderModel: TraderModel = TraderModel(
      vatNumber = None,
      traderExciseNumber = Some("excise number"),
      traderId = None,
      traderName = Some("name"),
      address = Some(AddressModel(
        streetNumber = Some("number"),
        street = Some("street"),
        postcode = Some("a postcode"),
        city = Some("a city")
      )),
      eoriNumber = None
    )

    val consignorTraderXml: Elem = <urn:ConsignorTrader language="en">
      <urn:TraderExciseNumber>excise number</urn:TraderExciseNumber>
      <urn:TraderName>name</urn:TraderName>
      <urn:StreetName>street</urn:StreetName>
      <urn:StreetNumber>number</urn:StreetNumber>
      <urn:Postcode>a postcode</urn:Postcode>
      <urn:City>a city</urn:City>
    </urn:ConsignorTrader>

    val consignorTraderJson: JsObject = Json.obj(
      "traderExciseNumber" -> "excise number",
      "traderName" -> "name",
      "address" -> Json.obj(
        "streetNumber" -> "number",
        "street" -> "street",
        "postcode" -> "a postcode",
        "city" -> "a city",
      )
    )
  }

  object AnalysisFixtures {
    val analysisModel: AnalysisModel = AnalysisModel(
      dateOfAnalysis = "date",
      globalExplanation = "explanation"
    )

    val analysisXml: Elem = <urn:Analysis>
      <urn:DateOfAnalysis>date</urn:DateOfAnalysis>
      <urn:GlobalExplanation language="en">explanation</urn:GlobalExplanation>
    </urn:Analysis>

    val analysisJson: JsObject = Json.obj(
      "dateOfAnalysis" -> "date",
      "globalExplanation" -> "explanation"
    )
  }

  object BodyAnalysisFixtures {
    val bodyAnalysisModelMax: BodyAnalysisModel = BodyAnalysisModel(
      exciseProductCode = "code",
      bodyRecordUniqueReference = 1,
      explanation = "explanation",
      actualQuantity = Some(3.2)
    )

    val bodyAnalysisModelMin: BodyAnalysisModel = BodyAnalysisModel(
      exciseProductCode = "code",
      bodyRecordUniqueReference = 2,
      explanation = "explanation",
      actualQuantity = None
    )

    val bodyAnalysisXmlMax: Elem = <urn:BodyAnalysis>
      <urn:ExciseProductCode>code</urn:ExciseProductCode>
      <urn:BodyRecordUniqueReference>1</urn:BodyRecordUniqueReference>
      <urn:Explanation language="en">explanation</urn:Explanation>
      <urn:ActualQuantity>3.2</urn:ActualQuantity>
    </urn:BodyAnalysis>

    val bodyAnalysisXmlMin: Elem = <urn:BodyAnalysis>
      <urn:ExciseProductCode>code</urn:ExciseProductCode>
      <urn:BodyRecordUniqueReference>2</urn:BodyRecordUniqueReference>
      <urn:Explanation language="en">explanation</urn:Explanation>
    </urn:BodyAnalysis>

    val bodyAnalysisJsonMax: JsObject = Json.obj(
      "exciseProductCode" -> "code",
      "bodyRecordUniqueReference" -> 1,
      "explanation" -> "explanation",
      "actualQuantity" -> 3.2
    )

    val bodyAnalysisJsonMin: JsObject = Json.obj(
      "exciseProductCode" -> "code",
      "bodyRecordUniqueReference" -> 2,
      "explanation" -> "explanation"
    )
  }

  object SubmitExplainShortageExcessFixtures {
    import AttributesFixtures._
    import ConsigneeTraderFixtures._
    import ExciseMovementFixtures._
    import ConsignorTraderFixtures._
    import AnalysisFixtures._
    import BodyAnalysisFixtures._

    val submitExplainShortageExcessModelMax: SubmitExplainShortageExcessModel = SubmitExplainShortageExcessModel(
      attributes = attributesModel,
      consigneeTrader = Some(consigneeTraderModel),
      exciseMovement = exciseMovementModel,
      consignorTrader = Some(consignorTraderModel),
      analysis = Some(analysisModel),
      bodyAnalysis = Some(Seq(bodyAnalysisModelMax, bodyAnalysisModelMin))
    )

    val submitExplainShortageExcessModelMin: SubmitExplainShortageExcessModel = SubmitExplainShortageExcessModel(
      attributes = attributesModel,
      consigneeTrader = None,
      exciseMovement = exciseMovementModel,
      consignorTrader = None,
      analysis = None,
      bodyAnalysis = None
    )

    val submitExplainShortageExcessXmlMax: Elem = <urn:ExplanationOnReasonForShortage>
      {attributesXml}
      {consigneeTraderXml}
      {exciseMovementXml}
      {consignorTraderXml}
      {analysisXml}
      {bodyAnalysisXmlMax}
      {bodyAnalysisXmlMin}
    </urn:ExplanationOnReasonForShortage>

    val submitExplainShortageExcessXmlMin: Elem = <urn:ExplanationOnReasonForShortage>
      {attributesXml}
      {exciseMovementXml}
    </urn:ExplanationOnReasonForShortage>

    val submitExplainShortageExcessJsonMax: JsObject = Json.obj(
      "attributes" -> attributesJson,
      "consigneeTrader" -> consigneeTraderJson,
      "exciseMovement" -> exciseMovementJson,
      "consignorTrader" -> consignorTraderJson,
      "analysis" -> analysisJson,
      "bodyAnalysis" -> Json.arr(bodyAnalysisJsonMax, bodyAnalysisJsonMin)
    )

    val submitExplainShortageExcessJsonMin: JsObject = Json.obj(
      "attributes" -> attributesJson,
      "exciseMovement" -> exciseMovementJson
    )
  }
}
