package uk.gov.hmrc.emcstfe.models.request.getMessages

import uk.gov.hmrc.emcstfe.models.common.WithName

sealed trait SortOrder

object SortOrder {
  case object Ascending extends WithName("A") with SortOrder
  case object Descending extends WithName("D") with SortOrder
}
