/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.connector

import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

@Singleton
class ChrisConnector @Inject()(val http: HttpClient)(
                               implicit config: AppConfig
                              ) extends BaseConnector {

  override def appConfig: AppConfig = config
  override lazy val logger: Logger = Logger(this.getClass)

  def getMovementHistoryEvents()(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Either[String, Elem]] = {
    lazy val url: String = s"${config.chrisUrl}/ChRISOSB/EMCS/EMCSApplicationService/2"

    lazy val requestBody =
      s"""<?xml version='1.0' encoding='UTF-8'?>
        |<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
        |  <soapenv:Header>
        |    <VersionNo>2.1</VersionNo>
        |  </soapenv:Header>
        |  <soapenv:Body>
        |    <Control xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Common/ControlDocument">
        |      <MetaData>
        |        <MessageId>${java.util.UUID.randomUUID.toString}</MessageId>
        |        <Source>emcs_tfe</Source>
        |        <Identity>portal</Identity>
        |        <Partner>UK</Partner>
        |      </MetaData>
        |      <OperationRequest>
        |        <Parameters>
        |          <Parameter Name="ExciseRegistrationNumber">GBWK240176600</Parameter>
        |          <Parameter Name="ARC">16GB00000000000192223</Parameter>
        |        </Parameters>
        |        <ReturnData>
        |          <Data Name="schema" />
        |        </ReturnData>
        |      </OperationRequest>
        |    </Control>
        |  </soapenv:Body>
        |</soapenv:Envelope>""".stripMargin.trim

    logger.info(
      s"""
         |Making call to ChRIS from emcs-tfe
         |----------------------------------
         |URL: $url
         |Body: $requestBody
         |""".stripMargin)

    postString(http, url, requestBody, "http://www.govtalk.gov.uk/taxation/internationalTrade/Excise/EMCSApplicationService/2.0/GetMovementHistoryEvents") {
      response =>
        response.status match {
          case OK =>
            Try {
              val responseBody = XML.loadString(response.body)
              val cdata = XML.loadString((responseBody \\ "OperationResponse" \ "Results" \ "Result").text)
              Right(cdata)
            } match {
              case Failure(exception) =>
                logger.warn(s"Error reading CDATA or XML: ${exception.getMessage}")
                Left(s"Error reading CDATA or XML")
              case Success(value) => value
            }
          case status =>
            logger.error(
              s"""Unexpected status from chris: $status
                 |Unexpected response from chris: ${response.body}
                 |""".stripMargin)
            Left("Unexpected downstream response status")
        }
    }
  }

}
