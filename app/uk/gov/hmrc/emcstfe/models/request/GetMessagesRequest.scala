package uk.gov.hmrc.emcstfe.models.request

import uk.gov.hmrc.emcstfe.models.request.getMessages._

case class GetMessagesRequest(exciseRegistrationNumber: String, sortField: SortField, sortOrder: SortOrder, page: BigInt) {
  require(page >= 1, "Page cannot be less than 1")
}
