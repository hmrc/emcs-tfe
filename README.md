
# emcs-tfe

## API endpoints

<details>
<summary>Return a list of all movements for an exciseRegistrationNumber supplied

**`GET`** /movements/:exciseRegistrationNumber</summary>

### Responses

**Status**: 200 (OK) 

**Body**:

```json
{
  "movements": [
    {
      "arc": "GBTR000000EMCS1000040",
      "sequenceNumber": 1,
      "consignorName": "Mr Consignor 801",
      "dateOfDispatch": "2009-01-26T14:12:00.943Z",
      "movementStatus": "Accepted",
      "destinationId": "ABC1234567831",
      "consignorLanguageCode": "en"
    },
    {
      "arc": "GBTR000000EMCS1000044",
      "sequenceNumber": 1,
      "consignorName": "Mr Consignor 802",
      "dateOfDispatch": "2009-01-26T14:15:00.943Z",
      "movementStatus": "Accepted",
      "destinationId": "ABC1234567831",
      "consignorLanguageCode": "en"
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
  