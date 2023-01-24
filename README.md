
# emcs-tfe

## API endpoints

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
  