/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfe.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status.NO_CONTENT
import uk.gov.hmrc.emcstfe.support.WireMockMethods

object AuditStub extends WireMockMethods {

  def writeAudit(): StubMapping = {
    when(uri = "/write/audit", method = POST)
      .thenReturn(status = NO_CONTENT)
  }

  def writeAuditMerged(): StubMapping = {
    when(uri = "/write/audit/merged", method = POST)
      .thenReturn(status = NO_CONTENT)
  }
}
