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

package uk.gov.hmrc.emcstfe.utils

import com.sun.org.apache.xml.internal.security.Init
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse
import uk.gov.hmrc.emcstfe.models.response.ErrorResponse.MarkCreationError

import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.Base64
import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}
import scala.xml.Elem

@Singleton
class HMRCMarkUtil @Inject()() extends Logging {

  def createHmrcMark(xml: Elem): Either[ErrorResponse, String] = Try {

    Init.init()

    val c14n: Canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS)

    val trimmedXml = scala.xml.Utility.trim((xml \ "Body").head)
    val xmlBytes = trimmedXml.toString().getBytes()
    val canonStream: ByteArrayOutputStream = new ByteArrayOutputStream()
    c14n.canonicalize(xmlBytes, canonStream, true)
    val canonXmlBytes: Array[Byte] = canonStream.toByteArray

    Base64.getEncoder.encodeToString(MessageDigest.getInstance("SHA-1").digest(canonXmlBytes))
  } match {
    case Failure(exception) =>
      logger.warn(exception.getMessage)
      Left(MarkCreationError)
    case Success(value) => Right(value)
  }
}
