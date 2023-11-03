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
import uk.gov.hmrc.emcstfe.models.common.SubmitterType.{Consignee, Consignor}
import uk.gov.hmrc.emcstfe.models.common.{ConsigneeTrader, ConsignorTrader, ExciseMovementModel, SubmitterType}
import uk.gov.hmrc.emcstfe.models.explainShortageExcess.{AnalysisModel, AttributesModel, BodyAnalysisModel, SubmitExplainShortageExcessModel}

import scala.xml.Elem

trait SubmitExplainShortageExcessFixtures extends ChRISResponsesFixture with TraderModelFixtures with EISResponsesFixture {

  object AttributesFixtures {
    def attributesModel(submitterType: SubmitterType): AttributesModel = AttributesModel(
      submitterType = submitterType
    )

    def attributesXml(submitterType: SubmitterType): Elem = <urn:Attributes>
      <urn:SubmitterType>{submitterType}</urn:SubmitterType>
    </urn:Attributes>

    def attributesJson(submitterType: SubmitterType): JsObject = Json.obj(
      "submitterType" -> s"$submitterType"
    )
  }

  object ExciseMovementFixtures {
    val exciseMovementModel: ExciseMovementModel = ExciseMovementModel(
      arc = "01DE0000012345",
      sequenceNumber = 1
    )

    val exciseMovementXml: Elem = <urn:ExciseMovement>
      <urn:AdministrativeReferenceCode>01DE0000012345</urn:AdministrativeReferenceCode>
      <urn:SequenceNumber>1</urn:SequenceNumber>
    </urn:ExciseMovement>

    val exciseMovementJson: JsObject = Json.obj(
      "arc" -> "01DE0000012345",
      "sequenceNumber" -> 1
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
    import AnalysisFixtures._
    import AttributesFixtures._
    import BodyAnalysisFixtures._
    import ExciseMovementFixtures._

    def submitExplainShortageExcessModelMax(submitterType: SubmitterType): SubmitExplainShortageExcessModel = SubmitExplainShortageExcessModel(
      attributes = attributesModel(submitterType),
      consigneeTrader = if (submitterType == Consignee) Some(maxTraderModel(ConsigneeTrader)) else None,
      exciseMovement = exciseMovementModel,
      consignorTrader = if (submitterType == Consignor) Some(maxTraderModel(ConsignorTrader)) else None,
      analysis = Some(analysisModel),
      bodyAnalysis = Some(Seq(bodyAnalysisModelMax, bodyAnalysisModelMin))
    )

    def submitExplainShortageExcessModelMin(submitterType: SubmitterType): SubmitExplainShortageExcessModel = SubmitExplainShortageExcessModel(
      attributes = attributesModel(submitterType),
      consigneeTrader = None,
      exciseMovement = exciseMovementModel,
      consignorTrader = None,
      analysis = None,
      bodyAnalysis = None
    )

    def submitExplainShortageExcessXmlMax(submitterType: SubmitterType): Elem = {
      <urn:ExplanationOnReasonForShortage>
        {attributesXml(submitterType)}{if (submitterType == Consignee)
        <urn:ConsigneeTrader language="en">
          {maxTraderModelXML(ConsigneeTrader)}
        </urn:ConsigneeTrader>}{exciseMovementXml}{if (submitterType == Consignor)
        <urn:ConsignorTrader language="en">
          {maxTraderModelXML(ConsignorTrader)}
        </urn:ConsignorTrader>}{analysisXml}{bodyAnalysisXmlMax}{bodyAnalysisXmlMin}
      </urn:ExplanationOnReasonForShortage>
    }

    def submitExplainShortageExcessXmlMin(submitterType: SubmitterType): Elem = <urn:ExplanationOnReasonForShortage>
      {attributesXml(submitterType)}
      {exciseMovementXml}
    </urn:ExplanationOnReasonForShortage>

    def submitExplainShortageExcessJsonMax(submitterType: SubmitterType): JsObject = {
      val jsObject = Json.obj(
        "attributes" -> attributesJson(submitterType),
        "exciseMovement" -> exciseMovementJson,
        "analysis" -> analysisJson,
        "bodyAnalysis" -> Json.arr(bodyAnalysisJsonMax, bodyAnalysisJsonMin)
      )

      val traderDetails = submitterType match {
        case Consignor => "consignorTrader" -> maxTraderModelJson(ConsignorTrader)
        case Consignee => "consigneeTrader" -> maxTraderModelJson(ConsigneeTrader)
      }

      jsObject + traderDetails
    }

    def submitExplainShortageExcessJsonMin(submitterType: SubmitterType): JsObject = Json.obj(
      "attributes" -> attributesJson(submitterType),
      "exciseMovement" -> exciseMovementJson
    )
  }
}
