/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMovementRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.SoapExtractionError
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.utils.{Logging, SoapUtils}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class GetMovementService @Inject()(connector: ChrisConnector) extends Logging {
  def getMovement(getMovementRequest: GetMovementRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] = {
    connector.postChrisSOAPRequest(getMovementRequest).map {
      response => {
        response.flatMap {
          xml =>
            SoapUtils.extractFromSoap(xml) match {
              case Failure(exception) =>
                logger.warn("Error extracting response body from SOAP wrapper", exception)
                (xml \\ "Errors" \\ "Error").foreach(error => logger.warn(error.text))
                Left(SoapExtractionError)
              case Success(value) => Right(GetMovementResponse.fromXml(value))
            }
        }
      }
    }
  }
}
