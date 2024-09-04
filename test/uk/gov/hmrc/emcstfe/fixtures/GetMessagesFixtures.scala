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

import play.api.libs.json.{JsArray, JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.getMessages.{GetMessagesResponse, Message}

import java.nio.charset.StandardCharsets
import java.util.Base64
import scala.xml.Utility.trim
import scala.xml.XML

trait GetMessagesFixtures extends BaseFixtures {

  object MessagesDataFixtures {
    val messagesDataXmlBody: String =
      """
        |<MessagesDataResponse xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessagesData/3" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        |	<Message>
        |		<UniqueMessageIdentifier>1000</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-17T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000001</Arc>
        |		<MessageType>IE801</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>false</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789012</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1000</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-17T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000002</Arc>
        |		<MessageType>IE801</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789012</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1001</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-18T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000003</Arc>
        |		<MessageType>IE802</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>false</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789013</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1002</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-19T09:30:47.0Z</DateCreatedOnCore>
        |		<MessageType>IE803</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |		<ReadIndicator>false</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789014</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1003</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-20T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000005</Arc>
        |		<MessageType>IE810</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789015</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1004</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-21T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000006</Arc>
        |		<MessageType>IE813</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789016</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1005</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-22T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000007</Arc>
        |		<MessageType>IE818</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |   <MessageRole>1</MessageRole>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |		<LRN>LRN4567890123456789017</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1006</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-23T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000008</Arc>
        |		<MessageType>IE837</MessageType>
        |		<RelatedMessageType></RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789018</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1007</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-24T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000009</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE818</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1008</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000010</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789010</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1009</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000011</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789020</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1010</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000012</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789021</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1011</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000013</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789022</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1012</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000014</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789023</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1013</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000015</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789024</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1014</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000016</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789025</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1015</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000017</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789026</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1016</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000018</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789027</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1017</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000019</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789028</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1018</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000020</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789029</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1019</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000021</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789030</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1020</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000022</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789031</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1021</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000023</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789032</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1022</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000024</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789033</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1023</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000025</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789034</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1024</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000026</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789035</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1025</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000027</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789036</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1026</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000028</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789037</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1027</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000029</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789038</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1028</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000030</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789039</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1029</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000031</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789040</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1030</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000032</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789041</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1031</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000033</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789042</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1032</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000034</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789043</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1033</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000035</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789044</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1034</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000036</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789045</LRN>
        |	</Message>
        |	<Message>
        |		<UniqueMessageIdentifier>1035</UniqueMessageIdentifier>
        |   <SubmittedByRequestingTrader>false</SubmittedByRequestingTrader>
        |		<DateCreatedOnCore>2008-09-25T09:30:47.0Z</DateCreatedOnCore>
        |		<Arc>GBTR000000EMCS1000037</Arc>
        |		<MessageType>IE704</MessageType>
        |		<RelatedMessageType>IE837</RelatedMessageType>
        |		<SequenceNumber>1</SequenceNumber>
        |		<ReadIndicator>true</ReadIndicator>
        |   <MessageRole>1</MessageRole>
        |		<LRN>LRN4567890123456789046</LRN>
        |	</Message>
        |	<TotalNumberOfMessagesAvailable>35</TotalNumberOfMessagesAvailable>
        |</MessagesDataResponse>""".stripMargin

    val messagesDataMinimumXmlBody: String =
      """
        |<MessagesDataResponse xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessagesData/3" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        |	<TotalNumberOfMessagesAvailable>0</TotalNumberOfMessagesAvailable>
        |</MessagesDataResponse>""".stripMargin
  }

  object MessageFixtures {
    val messageXmlBody: String =
      """
        |<Message>
        |	<UniqueMessageIdentifier>1000</UniqueMessageIdentifier>
        |	<DateCreatedOnCore>2008-09-17T09:30:47.0Z</DateCreatedOnCore>
        |	<Arc>GBTR000000EMCS1000001</Arc>
        |	<MessageType>IE801</MessageType>
        |	<RelatedMessageType>IE704</RelatedMessageType>
        |	<SequenceNumber>3</SequenceNumber>
        |	<ReadIndicator>false</ReadIndicator>
        |	<LRN>LRN4567890123456789012</LRN>
        | <MessageRole>3</MessageRole>
        | <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |</Message>
        |""".stripMargin

    val messageModel: Message = Message(
      uniqueMessageIdentifier = 1000,
      dateCreatedOnCore = "2008-09-17T09:30:47.0Z",
      arc = Some("GBTR000000EMCS1000001"),
      messageType = "IE801",
      relatedMessageType = Some("IE704"),
      sequenceNumber = Some(3),
      readIndicator = false,
      lrn = Some("LRN4567890123456789012"),
      messageRole = 3,
      submittedByRequestingTrader = true
    )

    val messageJson: JsValue = Json.obj(
      "uniqueMessageIdentifier" -> 1000,
      "dateCreatedOnCore" -> "2008-09-17T09:30:47.0Z",
      "arc" -> "GBTR000000EMCS1000001",
      "messageType" -> "IE801",
      "relatedMessageType" -> "IE704",
      "sequenceNumber" -> 3,
      "readIndicator" -> false,
      "lrn" -> "LRN4567890123456789012",
      "messageRole" -> 3,
      "submittedByRequestingTrader" -> true,
    )

    val messageMinimumXmlBody: String =
      """
        |<Message>
        |	<UniqueMessageIdentifier>1000</UniqueMessageIdentifier>
        |	<DateCreatedOnCore>2008-09-17T09:30:47.0Z</DateCreatedOnCore>
        |	<MessageType>IE801</MessageType>
        |	<RelatedMessageType></RelatedMessageType>
        |	<ReadIndicator>false</ReadIndicator>
        | <MessageRole>3</MessageRole>
        | <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
        |</Message>
        |""".stripMargin

    val messageMinimumModel: Message = Message(
      uniqueMessageIdentifier = 1000,
      dateCreatedOnCore = "2008-09-17T09:30:47.0Z",
      arc = None,
      messageType = "IE801",
      relatedMessageType = None,
      sequenceNumber = None,
      readIndicator = false,
      lrn = None,
      messageRole = 3,
      submittedByRequestingTrader = true
    )

    val messageMinimumJson: JsValue = Json.obj(
      "uniqueMessageIdentifier" -> 1000,
      "dateCreatedOnCore" -> "2008-09-17T09:30:47.0Z",
      "messageType" -> "IE801",
      "readIndicator" -> false,
      "messageRole" -> 3,
      "submittedByRequestingTrader" -> true,
    )
  }

  object GetMessagesResponseFixtures {

    import MessagesDataFixtures._

    val getMessagesResponseDownstreamJson: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(trim(XML.loadString(messagesDataXmlBody)).toString().getBytes(StandardCharsets.UTF_8))
    )

    val getMessagesResponseDownstreamJsonWrongEncoding: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(trim(XML.loadString(messagesDataXmlBody)).toString().getBytes(StandardCharsets.UTF_16))
    )

    val getMessagesResponseDownstreamJsonNotEncoded: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> trim(XML.loadString(messagesDataXmlBody)).toString()
    )

    //noinspection LanguageFeature
    val getMessagesResponseDownstreamJsonPartiallyBadXml: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(trim(
        <MessagesDataResponse xmlns="http://www.govtalk.gov.uk/taxation/InternationalTrade/Excise/MessagesData/3" xmlns:ns1="http://hmrc/emcs/tfe/data" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
          <Message>
            <UniqueMessageIdentifier>1000</UniqueMessageIdentifier>
            <SubmittedByRequestingTrader>true</SubmittedByRequestingTrader>
            <DateCreatedOnCore>2008-09-17T09:30:47.0Z</DateCreatedOnCore>
            <Arc>GBTR000000EMCS1000001</Arc>
            <MessageType>IE801</MessageType>
            <RelatedMessageType></RelatedMessageType>
            <SequenceNumber>1</SequenceNumber>
            <ReadIndicator>false</ReadIndicator>
            <MessageRole>1</MessageRole>
            <LRN>LRN4567890123456789012</LRN>
          </Message>
          <Message>
            <UniqueMessageIdentifier>1000</UniqueMessageIdentifier>
          </Message>
          <TotalNumberOfMessagesAvailable>35</TotalNumberOfMessagesAvailable>
        </MessagesDataResponse>
      ).toString().getBytes(StandardCharsets.UTF_8))
    )

    val getMessagesResponseDownstreamJsonBadXml: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(trim(<Message>Success!</Message>).toString().getBytes(StandardCharsets.UTF_8))
    )

    val getMessagesResponseModel: GetMessagesResponse = GetMessagesResponse(
      messages = Seq(
        Message(uniqueMessageIdentifier = 1000, dateCreatedOnCore = "2008-09-17T09:30:47.0Z", arc = Some("GBTR000000EMCS1000001"), messageType = "IE801", relatedMessageType = None, sequenceNumber = Some(1), readIndicator = false, lrn = Some("LRN4567890123456789012"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1000, dateCreatedOnCore = "2008-09-17T09:30:47.0Z", arc = Some("GBTR000000EMCS1000002"), messageType = "IE801", relatedMessageType = None, sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789012"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1001, dateCreatedOnCore = "2008-09-18T09:30:47.0Z", arc = Some("GBTR000000EMCS1000003"), messageType = "IE802", relatedMessageType = None, sequenceNumber = Some(1), readIndicator = false, lrn = Some("LRN4567890123456789013"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1002, dateCreatedOnCore = "2008-09-19T09:30:47.0Z", arc = None, messageType = "IE803", relatedMessageType = None, sequenceNumber = None, readIndicator = false, lrn = Some("LRN4567890123456789014"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1003, dateCreatedOnCore = "2008-09-20T09:30:47.0Z", arc = Some("GBTR000000EMCS1000005"), messageType = "IE810", relatedMessageType = None, sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789015"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1004, dateCreatedOnCore = "2008-09-21T09:30:47.0Z", arc = Some("GBTR000000EMCS1000006"), messageType = "IE813", relatedMessageType = None, sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789016"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1005, dateCreatedOnCore = "2008-09-22T09:30:47.0Z", arc = Some("GBTR000000EMCS1000007"), messageType = "IE818", relatedMessageType = None, sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789017"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1006, dateCreatedOnCore = "2008-09-23T09:30:47.0Z", arc = Some("GBTR000000EMCS1000008"), messageType = "IE837", relatedMessageType = None, sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789018"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1007, dateCreatedOnCore = "2008-09-24T09:30:47.0Z", arc = Some("GBTR000000EMCS1000009"), messageType = "IE704", relatedMessageType = Some("IE818"), sequenceNumber = Some(1), readIndicator = true, lrn = None, messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1008, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000010"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789010"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1009, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000011"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789020"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1010, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000012"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789021"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1011, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000013"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789022"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1012, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000014"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789023"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1013, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000015"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789024"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1014, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000016"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789025"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1015, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000017"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789026"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1016, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000018"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789027"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1017, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000019"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789028"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1018, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000020"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789029"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1019, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000021"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789030"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1020, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000022"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789031"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1021, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000023"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789032"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1022, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000024"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789033"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1023, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000025"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789034"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1024, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000026"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789035"), messageRole = 1, submittedByRequestingTrader = false),
        Message(uniqueMessageIdentifier = 1025, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000027"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789036"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1026, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000028"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789037"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1027, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000029"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789038"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1028, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000030"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789039"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1029, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000031"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789040"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1030, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000032"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789041"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1031, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000033"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789042"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1032, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000034"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789043"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1033, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000035"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789044"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1034, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000036"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789045"), messageRole = 1, submittedByRequestingTrader = true),
        Message(uniqueMessageIdentifier = 1035, dateCreatedOnCore = "2008-09-25T09:30:47.0Z", arc = Some("GBTR000000EMCS1000037"), messageType = "IE704", relatedMessageType = Some("IE837"), sequenceNumber = Some(1), readIndicator = true, lrn = Some("LRN4567890123456789046"), messageRole = 1, submittedByRequestingTrader = false)
      ),
      totalNumberOfMessagesAvailable = 35
    )

    val getMessagesResponseJson: JsValue = Json.obj(
      "messages" -> JsArray(Seq(
        Json.obj("uniqueMessageIdentifier" -> 1000, "dateCreatedOnCore" -> "2008-09-17T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000001", "messageType" -> "IE801", "sequenceNumber" -> 1, "readIndicator" -> false, "lrn" -> "LRN4567890123456789012", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1000, "dateCreatedOnCore" -> "2008-09-17T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000002", "messageType" -> "IE801", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789012", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1001, "dateCreatedOnCore" -> "2008-09-18T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000003", "messageType" -> "IE802", "sequenceNumber" -> 1, "readIndicator" -> false, "lrn" -> "LRN4567890123456789013", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1002, "dateCreatedOnCore" -> "2008-09-19T09:30:47.0Z", "messageType" -> "IE803", "readIndicator" -> false, "lrn" -> "LRN4567890123456789014", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1003, "dateCreatedOnCore" -> "2008-09-20T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000005", "messageType" -> "IE810", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789015", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1004, "dateCreatedOnCore" -> "2008-09-21T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000006", "messageType" -> "IE813", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789016", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1005, "dateCreatedOnCore" -> "2008-09-22T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000007", "messageType" -> "IE818", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789017", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1006, "dateCreatedOnCore" -> "2008-09-23T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000008", "messageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789018", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1007, "dateCreatedOnCore" -> "2008-09-24T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000009", "messageType" -> "IE704", "relatedMessageType" -> "IE818", "sequenceNumber" -> 1, "readIndicator" -> true, "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1008, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000010", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789010", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1009, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000011", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789020", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1010, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000012", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789021", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1011, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000013", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789022", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1012, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000014", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789023", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1013, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000015", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789024", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1014, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000016", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789025", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1015, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000017", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789026", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1016, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000018", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789027", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1017, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000019", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789028", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1018, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000020", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789029", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1019, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000021", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789030", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1020, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000022", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789031", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1021, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000023", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789032", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1022, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000024", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789033", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1023, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000025", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789034", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1024, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000026", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789035", "messageRole" -> 1, "submittedByRequestingTrader" -> false),
        Json.obj("uniqueMessageIdentifier" -> 1025, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000027", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789036", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1026, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000028", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789037", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1027, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000029", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789038", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1028, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000030", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789039", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1029, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000031", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789040", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1030, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000032", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789041", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1031, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000033", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789042", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1032, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000034", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789043", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1033, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000035", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789044", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1034, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000036", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789045", "messageRole" -> 1, "submittedByRequestingTrader" -> true),
        Json.obj("uniqueMessageIdentifier" -> 1035, "dateCreatedOnCore" -> "2008-09-25T09:30:47.0Z", "arc" -> "GBTR000000EMCS1000037", "messageType" -> "IE704", "relatedMessageType" -> "IE837", "sequenceNumber" -> 1, "readIndicator" -> true, "lrn" -> "LRN4567890123456789046", "messageRole" -> 1, "submittedByRequestingTrader" -> false)
      )),
      "totalNumberOfMessagesAvailable" -> 35
    )

    val getMessagesResponseMinimumDownstreamJson: JsValue = Json.obj(
      "dateTime" -> "now",
      "exciseRegistrationNumber" -> testErn,
      "message" -> Base64.getEncoder.encodeToString(trim(XML.loadString(messagesDataMinimumXmlBody)).toString().getBytes(StandardCharsets.UTF_8))
    )

    val getMessagesResponseMinimumModel: GetMessagesResponse = GetMessagesResponse(
      messages = Seq.empty,
      totalNumberOfMessagesAvailable = 0
    )

    val getMessagesResponseMinimumJson: JsValue = Json.obj(
      "messages" -> JsArray(Seq.empty),
      "totalNumberOfMessagesAvailable" -> 0
    )
  }
}
