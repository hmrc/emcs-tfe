/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.utils

import com.sun.org.apache.xml.internal.security.Init
import com.sun.org.apache.xml.internal.security.c14n.Canonicalizer

import java.security.MessageDigest
import java.util.Base64
import javax.inject.{Inject, Singleton}

@Singleton
class HMRCMarkUtil @Inject()() extends Logging {

  def createHmrcMark(xmlBytes: Array[Byte]): String = {

    Init.init()

    val c14n: Canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS)
    val canonXmlBytes: Array[Byte] = c14n.canonicalize(xmlBytes)
    Base64.getEncoder.encodeToString(MessageDigest.getInstance("SHA-1").digest(canonXmlBytes))
  }
}
