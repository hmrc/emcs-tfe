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

package uk.gov.hmrc.emcstfe.repositories

import uk.gov.hmrc.emcstfe.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfe.config.AppConfig
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

trait RepositoryBaseSpec[A] extends IntegrationBaseSpec
  with DefaultPlayMongoRepositorySupport[A] {

  val mockAppConfig: AppConfig = app.injector.instanceOf[AppConfig]

  protected val repository: PlayMongoRepository[A]
}
