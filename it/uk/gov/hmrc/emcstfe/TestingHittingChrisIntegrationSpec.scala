/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfe.stubs.{AuditStub, DownstreamStub}
import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec

import scala.xml.Elem

class TestingHittingChrisIntegrationSpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = "/test-only/chris"
    def downstreamUri: String = s"/ChRISOSB/EMCS/EMCSApplicationService/2"

    def request(): WSRequest = {
      setupStubs()
      AuditStub.writeAudit()
      AuditStub.writeAuditMerged()
      buildRequest(uri)
    }
  }

  "Calling the hello world endpoint" should {
    "return a success page" when {
      "all downstream calls are successful" in new Test {

        val responseBody: Elem = <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
          <env:Body>
            <Control xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <MetaData>
                <MessageId>String</MessageId>
                <Source>String</Source>
                <Identity>String</Identity>
                <Partner>String</Partner>
                <CorrelationId>String</CorrelationId>
                <BusinessKey>String</BusinessKey>
                <MessageDescriptor>String</MessageDescriptor>
                <QualityOfService>String</QualityOfService>
                <Destination>String</Destination>
                <Priority>0</Priority>
              </MetaData>
              <OperationResponse>
                <Results>
                  <Result>
                    <![CDATA[<MovementHistory 	xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MovementHistoryEvents/3" 	xmlns:ns1="http://hmrc/emcs/tfe/data" 	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">	<Events>		<EventType>IE801</EventType>		<EventDate>2008-09-04T10:22:50</EventDate>		<SequenceNumber>1</SequenceNumber>		<MessageRole>0</MessageRole>		<SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>	</Events>		<TotalNumberOfMessagesAvailable>1</TotalNumberOfMessagesAvailable></MovementHistory>]]>
                  </Result>
                </Results>
              </OperationResponse>
            </Control>
          </env:Body>
        </env:Envelope>

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, responseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.header("Content-Type") shouldBe Some("application/xml; charset=UTF-8")
        response.body should include("SubmittedByRequestingTrader")
      }
    }
    "return an error page" when {
      "downstream call returns unexpected XML" in new Test {
        val responseBody: Elem = <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
          <env:Body>
            <Control xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <MetaData>
                <MessageId>String</MessageId>
                <Source>String</Source>
                <Identity>String</Identity>
                <Partner>String</Partner>
                <CorrelationId>String</CorrelationId>
                <BusinessKey>String</BusinessKey>
                <MessageDescriptor>String</MessageDescriptor>
                <QualityOfService>String</QualityOfService>
                <Destination>String</Destination>
                <Priority>0</Priority>
              </MetaData>
              <OperationResponse>
                <Results>
                  <beans>Something in here</beans>
                </Results>
              </OperationResponse>
            </Control>
          </env:Body>
        </env:Envelope>

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, responseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.body should include("Error reading CDATA or XML")
      }
      "downstream call returns something other than XML" in new Test {
        val referenceDataResponseBody: JsValue = Json.obj()

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.OK, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.body should include("Error reading CDATA or XML")
      }
      "downstream call returns a non-200 HTTP response" in new Test {
        val referenceDataResponseBody: JsValue = Json.parse(
          s"""
             |{
             |   "message": "test message"
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("application/json")
        response.body should include("Unexpected downstream response status")
      }
    }
  }
}
