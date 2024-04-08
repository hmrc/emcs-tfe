/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.emcstfe.services.nrs

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.emcstfe.connectors.NRSBrokerConnector
import uk.gov.hmrc.emcstfe.models.auth.UserRequest
import uk.gov.hmrc.emcstfe.models.nrs.{IdentityData, NRSPayload, NotableEvent}
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.{IdentityDataException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfe.models.response.nrsBroker.NRSBrokerInsertPayloadResponse
import uk.gov.hmrc.emcstfe.services.nrs.NRSBrokerService.retrievals
import uk.gov.hmrc.emcstfe.utils.{Logging, TimeMachine}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NRSBrokerService @Inject()(
                                  brokerConnector: NRSBrokerConnector,
                                  override val authConnector: AuthConnector,
                                  timeMachine: TimeMachine
                                ) extends Logging with AuthorisedFunctions {

  def submitPayload[A](submission: A, ern: String, notableEvent: NotableEvent)
                      (implicit hc: HeaderCarrier, ec: ExecutionContext, request: UserRequest[_], writes: Writes[A]): Future[Either[ErrorResponse, NRSBrokerInsertPayloadResponse]] = {
    val submissionAsString = Json.stringify(Json.toJson(submission)(writes))
    getIdentityData().flatMap { identityData =>
      val payload = NRSPayload(submissionAsString, notableEvent, identityData, ern, timeMachine.instant())
      brokerConnector.submitPayload(payload, ern).map {
        case Left(value) => Left(value)
        case Right(response) =>
          logger.info(s"[submitPayload] - Successfully inserted a NRS payload into the broker. NRS broker reference: ${response.reference} ")
          Right(response)
      }.recover {
        case e =>
          logger.warn(s"[submitPayload] - An unexpected error occurred sending payload to NRS broker for ERN: $ern. Error: ${e.getClass.getSimpleName}")
          Left(UnexpectedDownstreamResponseError)
      }
    }.recover {
      case e =>
        //Don't log out the exception message in case of sensitive data
        logger.warn(s"[submitPayload] - An error occurred when retrieving identity data for ERN: $ern. Error: ${e.getClass.getSimpleName}")
        Left(IdentityDataException(e.getClass.getSimpleName))
    }
  }

  private[services] def getIdentityData()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[IdentityData] =
    authorised().retrieve(retrievals) {

      case affinityGroup ~ internalId ~
        externalId ~ agentCode ~
        credentials ~ confidenceLevel ~
        nino ~ saUtr ~
        name ~
        email ~ agentInfo ~
        groupId ~ credentialRole ~
        mdtpInfo ~ itmpName ~
        itmpAddress ~
        credentialStrength ~ loginTimes =>
        Future(IdentityData(internalId = internalId,
          externalId = externalId,
          agentCode = agentCode,
          credentials = credentials,
          confidenceLevel = confidenceLevel,
          nino = nino,
          saUtr = saUtr,
          name = name,
          email = email,
          agentInformation = agentInfo,
          groupIdentifier = groupId,
          credentialRole = credentialRole,
          mdtpInformation = mdtpInfo,
          itmpName = itmpName,
          itmpAddress = itmpAddress,
          affinityGroup = affinityGroup,
          credentialStrength = credentialStrength,
          loginTimes = loginTimes
        ))
    }

}

object NRSBrokerService {

  private[services] val retrievals = Retrievals.affinityGroup and Retrievals.internalId and
    Retrievals.externalId and Retrievals.agentCode and
    Retrievals.credentials and Retrievals.confidenceLevel and
    Retrievals.nino and Retrievals.saUtr and
    Retrievals.name and
    Retrievals.email and Retrievals.agentInformation and
    Retrievals.groupIdentifier and Retrievals.credentialRole and
    Retrievals.mdtpInformation and Retrievals.itmpName and
    Retrievals.itmpAddress and
    Retrievals.credentialStrength and Retrievals.loginTimes
}
