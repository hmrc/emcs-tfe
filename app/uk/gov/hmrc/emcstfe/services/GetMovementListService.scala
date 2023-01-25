/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfe.connectors.ChrisConnector
import uk.gov.hmrc.emcstfe.models.request.GetMovementListRequest
import uk.gov.hmrc.emcstfe.models.response.{ErrorResponse, GetMovementListResponse}
import uk.gov.hmrc.emcstfe.utils.{Logging, SoapUtils}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementListService @Inject()(connector: ChrisConnector) extends Logging {

  def getMovementList(getMovementListRequest: GetMovementListRequest)
                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetMovementListResponse]] =
    connector.postChrisSOAPRequest[GetMovementListResponse](getMovementListRequest)
}
