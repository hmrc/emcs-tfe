/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.{GetMovementListRequest, GetMovementRequest}
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementListResponse, GetMovementResponse}
import uk.gov.hmrc.emcstfe.utils.{Logging, SoapUtils}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementService @Inject()(connector: ChrisConnector) extends Logging {
  def getMovement(getMovementRequest: GetMovementRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] = {
    connector.postChrisSOAPRequest(getMovementRequest).map {
      SoapUtils.parseResponseXMLAsEitherT[GetMovementResponse](GetMovementResponse.apply)
    }
  }

  def getMovementList(getMovementListRequest: GetMovementListRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementListResponse]] = {
    connector.postChrisSOAPRequest(getMovementListRequest).map {
      SoapUtils.parseResponseXMLAsEitherT[GetMovementListResponse](GetMovementListResponse.apply)
    }
  }
}
