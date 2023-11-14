# The Currency Exchange Project

## Description
REST API for describing currencies and exchange rates. 
Allows you to view and edit lists of currencies and exchange rates, 
and calculate the conversion of arbitrary amounts from one currency to another.

## Technologies
- Pattern MVC
- Maven
- Java Servlets
- SQLite
- JDBC
- Postman

## Postman request examples
[<img src="https://run.pstmn.io/button.svg" alt="Run In Postman" style="width: 128px; height: 32px;">](https://god.gw.postman.com/run-collection/30748979-92d322b9-8c14-41e1-98ca-837c2e245fea?action=collection%2Ffork&source=rip_markdown&collection-url=entityId%3D30748979-92d322b9-8c14-41e1-98ca-837c2e245fea%26entityType%3Dcollection%26workspaceId%3D3ef886e2-4b21-452d-9bc0-3ffd4fd9fd65)

## REST API 

### Currencies


#### GET `/currencies`
Getting a list of currencies. Response example:
```
[
    {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
    },   
    {
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
    }
]
```

#### GET `/currency/EUR`
Getting a specific currency. Response example:
```
[    
    {
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
    }
]    
```

#### POST `/currencies`
Adding a new currency to the database. 
The data is transmitted in the request body as form fields (x-www-form-urlencoded). 
The form fields are name, code, sign. 
An example of a response is a JSON representation of a record inserted into the database, including its ID:
```
[    
    {
    "id": 0,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
    }
]
```

### Exchange rates

#### GET `/exchangeRates`
Getting a list of all exchange rates. Response example:
```
[
    {
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
    }
]
```   

#### GET `/exchangeRate/USDRUB`
Getting a specific exchange rate. 
The currency pair is set by consecutive currency codes in the request address. Response example:
```
[   
    {
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
    }
]
```    

#### POST `/exchangeRates`
Adding a new exchange rate to the database. 
The data is transmitted in the request body as form fields (x-www-form-urlencoded). 
The form fields are base Currency Code, targetCurrencyCode, rate. Example of form fields:
```
[    
    {
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
    }
]
```    

#### PATCH `/exchangeRate/USDRUB`
Updating the existing exchange rate in the database. 
The currency pair is set by consecutive currency codes in the request address. 
The data is transmitted in the request body as form fields (x-www-form-urlencoded). 
The only field of the form is rate.

An example of a response is a JSON representation of an updated record in the database, including its ID:
```
[    
    {
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
    }
]
```

### Currency exchange

#### GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`
Calculation of the transfer of a certain amount of funds from one currency to another. 
Request example - GET /exchange?from=USD&to=AUD&amount=10.

Response example:
```
[    
    {
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A€"
    },
    "rate": 1.45,
    "amount": 10.00
    "convertedAmount": 14.50
    }
]
```