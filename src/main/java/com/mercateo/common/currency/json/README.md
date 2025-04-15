# Money and ExchangeRate Jackson Integration

This package provides Jackson integration for the Money, ExchangeRate, and CurrencyConverter classes without modifying the original domain classes.

## Dependencies

To use the Jackson integration:

```java
// Create an ObjectMapper with Money support
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new MoneyJacksonModule());

// Serialize Money
Money money = new Money(new BigDecimal("99.99"), KnownCurrencies.EUR);
String json = mapper.writeValueAsString(money);

// Deserialize Money
Money deserialized = mapper.readValue(json, Money.class);

// You can also serialize/deserialize ExchangeRate and CurrencyConverter
CurrencyConverter converter = new CurrencyConverter(rates);
String converterJson = mapper.writeValueAsString(converter);
CurrencyConverter deserializedConverter = mapper.readValue(converterJson, CurrencyConverter.class);
```

## JSON Format

Money objects are serialized as follows:

```json
{
  "amount": "99.99",
  "currency": "EUR"
}
```

ExchangeRate objects are serialized as:

```json
{
  "baseValue": {
    "amount": "1",
    "currency": "EUR"
  },
  "quoteValue": {
    "amount": "1.1",
    "currency": "USD"
  }
}
```

CurrencyConverter objects are serialized as:

```json
{
  "rates": [
    {
      "baseValue": {
        "amount": "1",
        "currency": "EUR"
      },
      "quoteValue": {
        "amount": "1.12",
        "currency": "USD"
      }
    },
    {
      "baseValue": {
        "amount": "1",
        "currency": "EUR"
      },
      "quoteValue": {
        "amount": "0.85",
        "currency": "GBP"
      }
    }
  ]
}
```

## Implementation Details

This package uses Jackson's mixin functionality to provide serialization/deserialization for Money, ExchangeRate, and CurrencyConverter classes without modifying the original classes. The KnownCurrencies enum is handled by custom serializers and deserializers. 