**How to instruct the Coding Agent -> see section 1**

**On the Currency Converter module -> see section 2**

## 1) Architecture of Intent: How this repo guides the Coding Agent

This repository is a demo of “systemic empowerment” with the GitHub Coding Agent. It separates stable, reusable instruction layers from issue‑specific business input and funnels them into a final, executable issue description.

It demoes basic levels of "Architecture of Intent" documentation:

- Highest‑level frame: `.github/copilot-instructions.md` — global guardrails and success criteria, following detailed docs provided by GitHub, curated from emerging best practices and refined in trials.
- Issue authoring process: `docs/INC_ISSUE_AUTHORING.md` — a disciplined workflow that transforms a raw, business‑only prompt into an authorized, implementation‑ready issue. -> time invest: months of personal refinement work "on the go", around 20 hours, stabilized, for this project it was just 1min copy&paste
- **Business source prompt: `docs/Prompt_with_BusinessRequirements.md` — the original "handwritten" request from the “Architect of Intent” (AI instructor/reviewer). This is the "true and authoritative" human input for this feature request** -> time invest 30min of concentrated writing, with some exercise needs zero iterations
- Issue template: `.github/ISSUE_TEMPLATE/increment-issue.md` — a lightweight, reusable structure for cross‑project use. -> time invest: close to zero maintenance
- Clarifications pass: `issues/inc-3-clarifications.md` — fast human‑in‑the‑loop disambiguation on scope, edge cases, and priorities. Drafted and submitted by the workflow spec in `docs/INC_ISSUE_AUTHORING.md` -> time invest: 10min of user review time
- Final instructions for the Agent: `issues/inc-3-description.md` — the single source the Coding Agent executes. -> time invest: 10min of user review time

- (and then there is .github\workflows\copilot-setup-steps.yml, which is for Github infrastructure setup. Defines the setup of the appropriate sandbox environment to enable the Coding Agent to integrate with testing framework etc (Maven..). To be activated via GitHub automated workflow, triggered automatically for all Coding Agent task executions -> time invest: 2h of repo analysis, GitHub schema docs, script testing)

This “architecture of intent” keeps meta‑level guidance stable while enabling rapid, high‑quality, issue‑specific specifications with minimal review time.

This is a low complexity setup. For larger projects that have a more involved evolutionary perspective, more framing layers are necessary. A compositional framework structure for that wider "Architecture of Intent"-scheme is not part of this demo scope. This proves already tractable, and this is where "the beef" goes in this transformative journey.

## 2) The Currency Converter Code Module

(this section has been largely auto-generated based on agentic code analysis, with input of original shorter README content - code owners may be more competent to review)

![CI](https://github.com/beschoe/currency-converter/actions/workflows/ci.yml/badge.svg)

A self-contained Java library for precise currency conversion in European B2B marketplace platforms. This library provides robust monetary calculations, exchange rate management, and JSON serialization support for internal systems requiring accurate daily currency conversion data.

## Features

The `com.mercateo.common.currency` package provides a comprehensive currency conversion system that allows for:

- Converting monetary amounts between different currencies with BigDecimal precision
- Managing exchange rates between multiple currencies with automatic rate derivation
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
// Get exchange rate between currencies
ExchangeRate rate = converter.getExchangeRate(EUR, USD);

// Manual conversion using exchange rate
Money converted = rate.convert(money, DecimalPlacesStrategy.PROPORTIONAL, RoundingMode.HALF_EVEN);
```

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

- `IllegalArgumentException`: Unknown currencies or invalid conversion pairs
- `IllegalStateException`: Conflicting exchange rates during initialization
- Currency compatibility validation in Money.compareTo()

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

This project is for demo purposes only. The code is owned by Unite Network SE. All Rights reservered.


