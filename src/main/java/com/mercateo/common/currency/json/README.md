# Money and ExchangeRate Jackson Integration

This package provides Jackson integration for the Money, ExchangeRate, and CurrencyConverter classes without modifying the original domain classes.

## Dependencies

To use the Jackson integration:

```java
// Create an ObjectMapper with Money support
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new MoneyJacksonModule());
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

## Implementation Details

This package uses Jackson's mixin functionality to provide serialization/deserialization for Money and ExchangeRate objects.
