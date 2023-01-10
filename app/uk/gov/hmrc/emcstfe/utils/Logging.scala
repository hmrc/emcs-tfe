package uk.gov.hmrc.emcstfe.utils

import play.api.Logger

trait Logging {
  lazy val logger: Logger = Logger(this.getClass)
}
