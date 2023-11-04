package uk.gov.hmrc.emcstfe.models.response.getMessages

import com.lucidchart.open.xtract.{ParseError, ParseFailure, ParseSuccess, PartialParseSuccess}
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.Reads._
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.XmlParseError

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.util.{Failure, Success, Try}
import scala.xml.XML

case class GetMessagesResponse(dateTime: String, ern: String, messagesData: MessagesData)

object GetMessagesResponse {
  implicit val writes: OWrites[GetMessagesResponse] = Json.writes

  private def xmlErrorsToJsError(errors: Seq[ParseError]): JsError = JsError(XmlParseError(errors).message)

  implicit val reads: Reads[GetMessagesResponse] = (
    (__ \ "dateTime").read[String] and
      (__ \ "exciseRegistrationNumber").read[String] and
      (__ \ "message").read[String].map {
        message =>
          Try {
            val decodedMessage: String = new String(Base64.getDecoder.decode(message), StandardCharsets.UTF_8)
            MessagesData.xmlReader.read(XML.loadString(decodedMessage)) match {
              case ParseSuccess(result) => result
              case PartialParseSuccess(_, errors) => throw JsResult.Exception(xmlErrorsToJsError(errors))
              case ParseFailure(errors) => throw JsResult.Exception(xmlErrorsToJsError(errors))
            }
          } match {
            case Failure(exception) => throw JsResult.Exception(JsError(exception.getMessage))
            case Success(value) => value
          }
      }
  )(GetMessagesResponse.apply _)
}
