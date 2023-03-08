
# emcs-tfe

## API endpoints

### Movements

<details>
<summary>Return a list of all movements for an exciseRegistrationNumber supplied

**`GET`** /movements/:exciseRegistrationNumber</summary>

### Query string search parameters

| paramName            | Type   | Values                                                                     | Default                      |
|----------------------|--------|----------------------------------------------------------------------------|------------------------------|
| search.traderRole    | String | - `Consignor and/or Consignee` <br> - `Consignor` <br> - `Consignee`       | `Consignor and/or Consignee` | 
| search.sortField     | String | - `MessageType` <br> - `DateReceived` <br> - `ARC` <br>  - `ReadIndicator` | `DateReceived`               |
| search.sortOrder     | String | - `D` _(Descednding)_ <br> - `A` _(Ascending)_                             | `D`                          |
| search.startPosition | Int    | Valid Integer > 0                                                          | 1                            |
| search.maxRows       | Int    | Valid Integer > 0                                                          | 30                           |

E.g. to search for the first 15 movements by Consignor ordered by DateReceived ascending, the call would be:

`/movements/:exciseRegistrationNumber?search.traderRole=Consignor&search.sortOrder=A&search.maxRows=15`

### Responses

**Status**: 200 (OK) 

**Body**:

```json
{
  "movements": [
    {
      "arc": "GBTR000000EMCS1000040",
      "dateOfDispatch": "2009-01-26T14:12:00",
      "movementStatus": "Accepted",
      "otherTraderID": "ABCD1234"
    },
    {
      "arc": "GBTR000000EMCS1000044",
      "dateOfDispatch": "2009-01-26T14:15:00",
      "movementStatus": "Accepted",
      "otherTraderID": "ABCD1234"
    }
  ]
}
```

**Status**: 500 (ISE)

**Body**:

```json
{
  "message": "JSON validation error"
}
```
</details>

### User Answers

#### Report a Receipt

<details>
<summary>Retrieve Report Receipt Frontend UserAnswers for the ERN and ARC supplied

**`GET`** /user-answers/report-receipt/:ern/:arc</summary>

#### Success Response(s)

**Status**: 200 (OK) _(when data is found for supplied ern and arc)_

**Body**:

```json
{
  "internalId": "abcd1234",
  "ern" : "ern",
  "arc" : "arc",
  "data": {
    "page1": "foo",
    "page2": "bar"
  },
  "lastUpdated": {
    "$date": {
      "$numberLong":"1678194091686"
    }
  }
}
```

**Status**: 204 (NO_CONTENT) _(when NO data is found)_

**Body**: n/a


#### Error Response(s)

**Status**: 500 (ISE)

**Body**:

```json
{
  "message": "Err Message"
}
```
</details>

<details>
<summary>Store Report Receipt Frontend UserAnswers for the ERN and ARC supplied

**`PUT`** /user-answers/report-receipt/:ern/:arc</summary>

This method is idempotent, in the sense that if no data exists it will be created and if some data already exists it will be updated with the new submitted data.

#### Request Body

`ReportReceiptUserAnswers` model:

```json
{
  "internalId": "abcd1234",
  "ern" : "ern",
  "arc" : "arc",
  "data": {
    "page1": "foo",
    "page2": "bar",
    "page3": "newEntry"
  },
  "lastUpdated": {
    "$date": {
      "$numberLong":"1678194091686"
    }
  }
}
```

#### Success Response(s)

**Status**: 200 (OK)

**Body**:

`ReportReceiptUserAnswers` model:

```json
{
  "internalId": "abcd1234",
  "ern" : "ern",
  "arc" : "arc",
  "data": {
    "page1": "foo",
    "page2": "bar",
    "page3": "newEntry"
  },
  "lastUpdated": {
    "$date": {
      "$numberLong":"1678194091686"
    }
  }
}
```

#### Error Response(s)

**Status**: 400 (BAD_REQUEST)

**Body**:

```
"Invalid ReportReceiptUserAnswers payload " + JsonValidation Errors
```

**Status**: 500 (ISE)

**Body**:

```json
{
  "message": "Err Message"
}
```
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

**Body**:

```json
{
  "message": "Err Message"
}
```
</details>
  