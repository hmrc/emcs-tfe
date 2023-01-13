package uk.gov.hmrc.emcstfe.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfe.connector.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMessageRequest
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.SoapExtractionError
import uk.gov.hmrc.emcstfe.utils.{Logging, SoapUtils}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class GetMessageService @Inject()(connector: ChrisConnector) extends Logging {
  def getMessage(getMessageRequest: GetMessageRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, String]] = {
    connector.getMessage(getMessageRequest).map {
      response => {
        response.flatMap {
          xml =>
            SoapUtils.extractFromSoap(xml) match {
              case Failure(exception) =>
                logger.warn("Error extracting response body from SOAP wrapper", exception)
                (xml \\ "Errors" \ "Error").foreach(error => logger.warn(error.text))
                Left(SoapExtractionError)
              case Success(value) => ???
            }
        }
      }
    }
  }
}
