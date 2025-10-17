# Currency Converter

![CI](https://github.com/beschoe/currency-converter/actions/workflows/ci.yml/badge.svg)

A self-contained Java library for precise currency conversion in European B2B marketplace platforms. This library provides robust monetary calculations, exchange rate management, and JSON serialization support for internal systems requiring accurate daily currency conversion data.

## Features

The `com.mercateo.common.currency` package provides a comprehensive currency conversion system that allows for:

- Converting monetary amounts between different currencies with BigDecimal precision
- Managing exchange rates between multiple currencies with automatic rate derivation
- Supporting arbitrary currency pairs with on-the-fly synthetic cross-rate calculation (up to 4 hops)
- Handling proper decimal scaling and rounding according to currency-specific requirements
- Supporting daily or more frequent exchange rate updates through immutable and updateable converters
- Thread-safe currency conversion operations
- JSON serialization/deserialization support via Jackson integration

The `com.mercateo.common.currency.json` package provides an object mapper configuration module for seamless JSON handling.

## Quick Start

### Basic Usage

```java
// Create exchange rates
Money eurBase = new Money(BigDecimal.ONE, ConvertableCurrency.EUR);
Money usdQuote = new Money(new BigDecimal("1.09"), ConvertableCurrency.USD);
ExchangeRate eurToUsd = new ExchangeRate(eurBase, usdQuote);

// Create converter
FrozenCurrencyConverter converter = new FrozenCurrencyConverter(Arrays.asList(eurToUsd));

// Convert money
Money euros = new Money(new BigDecimal("100.00"), ConvertableCurrency.EUR);
Money dollars = converter.convertToPrice(euros, ConvertableCurrency.USD);
// Result: 109.00 USD
```

### JSON Integration

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new MoneyJacksonModule());

Money money = new Money(new BigDecimal("99.99"), ConvertableCurrency.EUR);
String json = mapper.writeValueAsString(money);
// Result: {"amount":"99.99","currency":"EUR"}
```

## Architecture

### Core Components

- **Money**: Immutable monetary value with BigDecimal amount and currency
- **ConvertableCurrency**: Enum of supported ISO 4217 currencies with default scales and rounding
- **ExchangeRate**: Represents conversion rates between two currencies with lazy calculation
- **CurrencyConverter**: Interface for all currency conversion operations
- **FrozenCurrencyConverter**: Immutable implementation with fixed exchange rates
- **UpdateableCurrencyConverter**: Thread-safe wrapper allowing runtime rate updates
- **DecimalPlacesStrategy**: Precision strategies (TO_PRICE for invoices, PROPORTIONAL for calculations)

### Supported Currencies

EUR, USD, GBP, PLN, CZK, CHF, DKK, HRK, SEK, BGN, HUF, LVL, LTL, RON, TRY, DEM, CNY, INR, BRL, MXN

## Build & Development

### Prerequisites

- Java 17 or later
- Maven 3.6+

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Run all tests
mvn test

# Package library
mvn package

# Install to local repository
mvn install
```

### Testing

This library maintains comprehensive test coverage including:

- Unit tests for all conversion operations
- Edge case testing (same currency conversion, unknown currencies)
- Integration tests for JSON serialization
- Performance benchmarks for critical paths
- Mock-based testing for updateable converters

Run tests with: `mvn test`

### Dependencies

- **Jackson 2.15.2**: JSON serialization
- **Lombok 1.18.28**: Boilerplate reduction
- **JUnit**: Unit testing
- **AssertJ**: Fluent assertions
- **Mockito**: Mocking framework
- **Internal**: Mercateo util-annotations, i18n

## Usage Examples

### Conversion Strategies

```java
// For invoice prices (uses currency default scale)
Money invoicePrice = converter.convertToPrice(amount, targetCurrency);

// For calculations (preserves proportional precision)
Money calculatedAmount = converter.convertProportionally(amount, targetCurrency);

// Custom strategy and rounding
Money customResult = converter.convert(amount, targetCurrency, 
    DecimalPlacesStrategy.TO_PRICE, RoundingMode.HALF_UP);
```

### Exchange Rate Management

```java
// Get exchange rate between currencies (direct or synthetic)
ExchangeRate rate = converter.getExchangeRate(EUR, USD);

// Manual conversion using exchange rate
Money converted = rate.convert(money, DecimalPlacesStrategy.PROPORTIONAL, RoundingMode.HALF_EVEN);
```

### Synthetic Cross-Rates

The library automatically computes synthetic cross-rates when a direct exchange rate is not available:

```java
// Create converter with arbitrary currency pairs
List<ExchangeRate> rates = Arrays.asList(
    new ExchangeRate(new Money(ONE, USD), new Money("0.92", EUR)),  // USD -> EUR
    new ExchangeRate(new Money(ONE, EUR), new Money("0.84", GBP))   // EUR -> GBP
);
FrozenCurrencyConverter converter = new FrozenCurrencyConverter(rates);

// Automatically computes USD -> GBP via EUR (synthetic 2-hop path)
Money gbp = converter.convertToPrice(new Money("100.00", USD), GBP);
// Result: 77.28 GBP (100 * 0.92 * 0.84)
```

Features:
- Deterministic shortest-path selection (up to 4 hops maximum)
- Direct rates take precedence over synthetic paths
- On-the-fly calculation without persistent caching
- Throws `IllegalArgumentException` when no path exists

### Runtime Updates

```java
// Create updateable converter
UpdateableCurrencyConverter updateableConverter = 
    new UpdateableCurrencyConverter(initialConverter);

// Update rates at runtime (thread-safe)
FrozenCurrencyConverter newRates = new FrozenCurrencyConverter(newExchangeRates);
updateableConverter.set(newRates);
```

## Performance Notes

- Uses EnumMap for O(1) currency lookups
- Lazy calculation and caching of derived exchange rates
- Minimal object creation in conversion paths
- BigDecimal precision maintained throughout calculations
- Thread-safe operations where indicated

## Error Handling

- `IllegalArgumentException`: Unknown currencies, invalid conversion pairs, or no exchange rate path found
- `IllegalStateException`: Conflicting exchange rates during initialization
- Currency compatibility validation in Money.compareTo()
- Maximum path length: 4 hops for synthetic cross-rates

## Contributing

See `CONTRIBUTING.md` for local build steps, contribution workflow, and Coding Agent guidance.

### Code Standards

- Use meaningful variable and method names
- Prefer immutable objects (Money, ExchangeRate)
- Use BigDecimal for all monetary calculations (never float/double)
- Follow existing exception handling patterns
- Maintain thread-safety where documented

### Testing Requirements

- All tests must pass before committing
- Never comment out or mock away failing tests
- Add tests for new functionality
- Include edge case testing
- Test both conversion strategies

### Validation

Before any commit:
```bash
mvn clean test
```

## Deployment

See documentation contained in the [release-chain](https://gitlab.build-unite.unite.eu/procurement-platform/procurement-common-libraries/release-chain) project readme.

## License

Internal Mercateo library - proprietary usage only.


