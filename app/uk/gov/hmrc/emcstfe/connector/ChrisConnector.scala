/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connector

import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.emcstfe.models.response.HelloWorldResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.xml.Elem

@Singleton
class ChrisConnector @Inject()(val http: HttpClient,
                               val config: AppConfig
                              ) extends BaseConnector {

  override lazy val logger: Logger = Logger(this.getClass)

  def chrisHeaders(action: String): Seq[(String, String)] = Seq(
    ("Accept", "application/soap+xml"),
    ("Content-Type", s"""application/soap+xml; charset=UTF-8; action="$action"""")
  )

  def getMovementHistoryEvents()(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[String, Elem]] = {
    lazy val url: String = s"${config.chrisUrl}/ChRISOSB/EMCS/EMCSApplicationService/2"

    /*
      * POST /ChRISOSB/EMCS/EMCSApplicationService/2 HTTP/1.1
      Accept-Encoding: gzip
      X-Request-ID: govuk-tax-9194b27d-d992-47ee-b71a-6871aee196da
      X-Session-ID: session-711df0e3-3fe2-4e3c-abd8-bdbeb9e3554f
      User-Agent: emcs
      Accept: application/soap+xml
      Content-Type: application/soap+xml; charset=UTF-8; action="http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementHistoryEvents"
      Content-Length: 782
      Host: localhost:9090
      Connection: Keep-Alive
  */

    lazy val headers: Seq[(String, String)] = chrisHeaders("http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementHistoryEvents")

    lazy val requestBody =
      """
        |<?xml version='1.0' encoding='UTF-8'?>
        |<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
        |  <soapenv:Header>
        |    <VersionNo>2.1</VersionNo>
        |  </soapenv:Header>
        |  <soapenv:Body>
        |    <Control xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
        |      <MetaData>
        |        <MessageId>48682c6c-a30d-484e-8b44-cc3ab3964dc6</MessageId>
        |        <Source>emcs_tfe</Source>
        |        <Identity>portal</Identity>
        |        <Partner>UK</Partner>
        |      </MetaData>
        |      <OperationRequest>
        |        <Parameters>
        |          <Parameter Name="ExciseRegistrationNumber">GBWK000467014</Parameter>
        |          <Parameter Name="ARC">18GB00000000000226081</Parameter>
        |        </Parameters>
        |        <ReturnData>
        |          <Data Name="schema" />
        |        </ReturnData>
        |      </OperationRequest>
        |    </Control>
        |  </soapenv:Body>
        |</soapenv:Envelope>
        |""".stripMargin

    http.POSTString[HttpResponse](url, requestBody, headers).map {
      response =>
        response.status match {
          case OK =>
            Try {
              val responseBody = scala.xml.XML.loadString(response.body)
              val cdata = scala.xml.XML.loadString((responseBody \\ "OperationResponse" \ "Results" \ "Result").text)
              Right(cdata)
            } match {
              case Failure(exception) =>
                logger.warn(s"Error reading CDATA or XML: ${exception.getMessage}")
                Left(s"Error reading CDATA or XML")
              case Success(value) => value
            }
          case status =>
            logger.warn(s"Unexpected status from emcs-tfe-chris-stub: $status")
            Left("Unexpected downstream response status")
        }
    }
  }

}
