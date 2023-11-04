package uk.gov.hmrc.emcstfe.models.request.getMessages

import uk.gov.hmrc.emcstfe.models.common.WithName

sealed trait SortField

object SortField {
  case object MessageType extends WithName("messagetype") with SortField

  case object DateReceived extends WithName("datereceived") with SortField

  case object Arc extends WithName("arc") with SortField

  case object ReadIndicator extends WithName("readindicator") with SortField
}
