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

package uk.gov.hmrc.emcstfe.fixtures

trait SubmitDraftMovementFixture extends BaseFixtures {
  lazy val submitDraftMovementRequestBody: String = """<?xml version="1.0" encoding="UTF-8"?>
                                                      |<soapenv:Envelope xmlns:soapenv="http://www.w3.org/2003/05/soap-envelope">
                                                      |    <soapenv:Header>
                                                      |        <ns:Info xmlns:ns="http://www.hmrc.gov.uk/ws/info-header/1"/>
                                                      |    </soapenv:Header>
                                                      |    <soapenv:Body>
                                                      |        <urn:IE815 xmlns:urn="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:IE815:V3.01" xmlns:urn1="urn:publicid:-:EC:DGTAXUD:EMCS:PHASE4:TMS:V3.01">
                                                      |            <urn:Body>
                                                      |                <urn:SubmittedDraftOfEADESAD>
                                                      |                    <urn:EadEsadDraft>
                                                      |                        <urn:LocalReferenceNumber>EN</urn:LocalReferenceNumber>
                                                      |                    </urn:EadEsadDraft>
                                                      |                </urn:SubmittedDraftOfEADESAD>
                                                      |            </urn:Body>
                                                      |        </urn:IE815>
                                                      |    </soapenv:Body>
                                                      |</soapenv:Envelope>""".stripMargin
}
