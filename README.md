
# emcs-tfe

## API endpoints

### Movements

<details>
<summary>Return a list of all movements for an ERN

**`GET`** /movements/:ern</summary>

### Query string search parameters

| paramName            | Type   | Values                                                                     | Default                      |
|----------------------|--------|----------------------------------------------------------------------------|------------------------------|
| search.traderRole    | String | - `Consignor and/or Consignee` <br> - `Consignor` <br> - `Consignee`       | `Consignor and/or Consignee` | 
| search.sortField     | String | - `MessageType` <br> - `DateReceived` <br> - `ARC` <br>  - `ReadIndicator` | `DateReceived`               |
| search.sortOrder     | String | - `D` _(Descednding)_ <br> - `A` _(Ascending)_                             | `D`                          |
| search.startPosition | Int    | Valid Integer > 0                                                          | 1                            |
| search.maxRows       | Int    | Valid Integer > 0                                                          | 30                           |

E.g. to search for the first 15 movements by Consignor ordered by DateReceived ascending, the call would be:

`/movements/:ern?search.traderRole=Consignor&search.sortOrder=A&search.maxRows=15`

### Responses

#### Success Response(s)

**Status**: 200 (OK) 

**Body**: [GetMovementListResponse Model](app/uk/gov/hmrc/emcstfe/models/response/GetMovementListResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Return movement details for a ERN and ARC

**`GET`** /movement/:ern/:arc</summary>

### Responses

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [GetMovementResponse Model](app/uk/gov/hmrc/emcstfe/models/response/GetMovementResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

### Report Of Receipt

<details>
<summary>Submit a Report of Receipt

**`POST`** /report-of-receipt/:ern/:arc</summary>

**Request Body**: [SubmitReportOfReceiptModel Model](app/uk/gov/hmrc/emcstfe/models/reportOfReceipt/SubmitReportOfReceiptModel.scala)

### Responses

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [ChRISSuccessResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ChRISSuccessResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

### Explain Delay

<details>
<summary>Submit Explanation of Delay

**`POST`** /explain-delay/:ern/:arc</summary>

**Request Body**: [SubmitExplainDelayModel Model](app/uk/gov/hmrc/emcstfe/models/explainDelay/SubmitExplainDelayModel.scala)

### Responses

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [ChRISSuccessResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ChRISSuccessResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

### User Answers

#### Report a Receipt

<details>
<summary>Retrieve Report Receipt Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/report-receipt/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**: [ReportReceiptUserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/ReportReceiptUserAnswers.scala)

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Store Report Receipt Frontend UserAnswers for the ERN and ARC supplied

**`PUT`** /user-answers/report-receipt/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

**Request Body**: [ReportReceiptUserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/ReportReceiptUserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [ReportReceiptUserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/ReportReceiptUserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid ReportReceiptUserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Remove Report Receipt Frontend UserAnswers for the ERN and ARC supplied

**`DELETE`** /user-answers/report-receipt/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it returns NO_CONTENT as a successful response. If data exist, it will removed and also return a NO_CONTENT.

#### Success Response(s)

**Status**: 204 (NO_CONTENT)

**Body**: n/a

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>
  