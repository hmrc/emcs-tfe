
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

<details>
<summary>Submit Alert or Rejection

**`POST`** /alert-or-rejection/:ern/:arc</summary>

**Request Body**: [SubmitAlertOrRejectionModel Model](app/uk/gov/hmrc/emcstfe/models/alertOrRejection/SubmitAlertOrRejectionModel.scala)

### Responses

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [ChRISSuccessResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ChRISSuccessResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Change Destination

**`POST`** /change-destination/:ern/:arc</summary>

**Request Body**: [SubmitChangeDestinationExcess Model](app/uk/gov/hmrc/emcstfe/models/changeDestination/SubmitChangeDestinationModel.scala)

### Responses

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [ChRISSuccessResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ChRISSuccessResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Explain Shortage or Excess

**`POST`** /explain-shortage-excess/:ern/:arc</summary>

**Request Body**: [SubmitExplainShortageExcess Model](app/uk/gov/hmrc/emcstfe/models/explainShortageExcess/SubmitExplainShortageExcessModel.scala)

### Responses

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [ChRISSuccessResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ChRISSuccessResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Cancel a movement

**`POST`** /cancel-movement/:ern/:arc</summary>

**Request Body**: [SubmitCancellationOfMovement Model](app/uk/gov/hmrc/emcstfe/models/cancellationOfMovement/SubmitCancellationOfMovementModel.scala)

### Responses

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [ChRISSuccessResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ChRISSuccessResponse.scala)

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Create a movement

**`POST`** /create-movement/:ern/:arc</summary>

**Request Body**: [CreateMovement Model](app/uk/gov/hmrc/emcstfe/models/createMovement/SubmitCreateMovementModel.scala)

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

#### Report a Receipt Frontend

<details>
<summary>Retrieve Report Receipt Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/report-receipt/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

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

**Request Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid UserAnswers payload " + JsonValidation Errors
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

---

#### Create Movement Frontend

<details>
<summary>Retrieve Create Movement Frontend UserAnswers for the ERN and DRAFT-ID supplied

**`GET`** /user-answers/create-movement/:ern/:draftId</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and draftId)_

**Body**: [CreateMovementUserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/CreateMovementUserAnswers.scala)

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Store Create Movement Frontend UserAnswers for the ERN and DRAFT-ID supplied

**`PUT`** /user-answers/create-movement/:ern/:draftId</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

**Request Body**: [CreateMovementUserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/CreateMovementUserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [CreateMovementUserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/CreateMovementUserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid CreateMovementUserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Remove Create Movement Frontend UserAnswers for the ERN and DRAFT-ID supplied

**`DELETE`** /user-answers/create-movement/:ern/:draftId</summary>

This method is idempotent, in the sense that if no data exists it returns NO_CONTENT as a successful response. If data exist, it will removed and also return a NO_CONTENT.

#### Success Response(s)

**Status**: 204 (NO_CONTENT)

**Body**: n/a

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

#### Explain Delay Frontend

<details>
<summary>Retrieve Explain Delay Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/explain-delay/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Store Explain Delay Frontend UserAnswers for the ERN and ARC supplied

**`PUT`** /user-answers/explain-delay/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

**Request Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid UserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Remove Explain Delay Frontend UserAnswers for the ERN and ARC supplied

**`DELETE`** /user-answers/explain-delay/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it returns NO_CONTENT as a successful response. If data exist, it will removed and also return a NO_CONTENT.

#### Success Response(s)

**Status**: 204 (NO_CONTENT)

**Body**: n/a

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

#### Explain Shortage or Excess Frontend

<details>
<summary>Retrieve Explain Shortage or Excess Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/explain-shortage-or-excess/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Store Explain Shortage or Excess Frontend UserAnswers for the ERN and ARC supplied

**`PUT`** /user-answers/explain-shortage-or-excess/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

**Request Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid UserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Remove Explain Shortage or Excess Frontend UserAnswers for the ERN and ARC supplied

**`DELETE`** /user-answers/explain-shortage-or-excess/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it returns NO_CONTENT as a successful response. If data exist, it will removed and also return a NO_CONTENT.

#### Success Response(s)

**Status**: 204 (NO_CONTENT)

**Body**: n/a

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

#### Cancel Movement Frontend

<details>
<summary>Retrieve Cancel Movement Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/cancel-movement/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Store Cancel Movement Frontend UserAnswers for the ERN and ARC supplied

**`PUT`** /user-answers/cancel-movement/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

**Request Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid UserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Remove Cancel Movement Frontend UserAnswers for the ERN and ARC supplied

**`DELETE`** /user-answers/cancel-movement/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it returns NO_CONTENT as a successful response. If data exist, it will removed and also return a NO_CONTENT.

#### Success Response(s)

**Status**: 204 (NO_CONTENT)

**Body**: n/a

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

#### Change Destination Frontend

<details>
<summary>Retrieve Change Destination Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/change-destination/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Store Change Destination Frontend UserAnswers for the ERN and ARC supplied

**`PUT`** /user-answers/change-destination/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

**Request Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid UserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Remove Change Destination Frontend UserAnswers for the ERN and ARC supplied

**`DELETE`** /user-answers/change-destination/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it returns NO_CONTENT as a successful response. If data exist, it will removed and also return a NO_CONTENT.

#### Success Response(s)

**Status**: 204 (NO_CONTENT)

**Body**: n/a

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

---

#### Alert or Rejection Frontend

<details>
<summary>Retrieve Alert or Rejection Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/alert-or-rejection/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Store Alert or Rejection Frontend UserAnswers for the ERN and ARC supplied

**`PUT`** /user-answers/alert-or-rejection/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

**Request Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Success Response(s)

**Status**: 200 (OK)

**Body**: [UserAnswers Model](app/uk/gov/hmrc/emcstfe/models/mongo/UserAnswers.scala)

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid UserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>

<details>
<summary>Remove Alert or Rejection Frontend UserAnswers for the ERN and ARC supplied

**`DELETE`** /user-answers/alert-or-rejection/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it returns NO_CONTENT as a successful response. If data exist, it will removed and also return a NO_CONTENT.

#### Success Response(s)

**Status**: 204 (NO_CONTENT)

**Body**: n/a

#### Error Response(s)

**Status**: 500 (ISE)

**Body**: [ErrorResponse Model](app/uk/gov/hmrc/emcstfe/models/response/ErrorResponse.scala)

</details>
  