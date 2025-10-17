# Currency Converter - GitHub Copilot & Coding Agent Instructions (v1.0)

Authoritative location (2025-10-15). This file (in .github directory) is the single authoritative source; no mirror or proxy shall be maintained.
Last updated: 2025-10-15

Purpose: Stable project framing for GitHub Coding Agent & Copilot. Defines increment plan, guardrails, tooling, and success criteria. Keep concise; append changes with a new version header rather than rewriting silently.

Cross-reference index (authoritative sources, do not duplicate text):
- Summarizing README: `README.md`
- Roadmap & increment statuses: `docs/ROADMAP.md`
- Contribution workflow & review checklist: `CONTRIBUTING.md`
- external reference docs in `docs/` as needed
- This file: high-level framing + shared guardrails.

## Project Overview

This is a small, self-contained Java library for currency conversion in a European B2B marketplace platform. The library provides precise monetary calculations, exchange rate management, and JSON serialization support for internal systems requiring daily currency conversion data.

**Project Type**: Maven Java library  
**Languages**: Java 17+  
**Frameworks**: Jackson (JSON), JUnit (testing), Mockito (mocking)  
**Target Runtime**: JVM  
**Repository Size**: Small (~20 classes, well-contained)

## Architecture Overview

### Core Components

1. **Money** - Immutable monetary value with currency and BigDecimal amount
2. **ConvertableCurrency** - Enum of supported ISO 4217 currencies with default scales/rounding
3. **ExchangeRate** - Represents conversion rates between two currencies
4. **CurrencyConverter** - Interface for currency conversion operations
5. **FrozenCurrencyConverter** - Immutable implementation with fixed exchange rates
6. **UpdateableCurrencyConverter** - Thread-safe wrapper allowing runtime rate updates
7. **DecimalPlacesStrategy** - Enum defining precision strategies (TO_PRICE, PROPORTIONAL)

### Package Structure

```
src/main/java/com/mercateo/common/currency/
├── Money.java                    # Core monetary value class
├── ConvertableCurrency.java      # Supported currencies enum
├── ExchangeRate.java            # Exchange rate calculations
├── CurrencyConverter.java       # Main interface
├── FrozenCurrencyConverter.java # Immutable implementation
├── UpdateableCurrencyConverter.java # Mutable wrapper
├── DecimalPlacesStrategy.java   # Precision strategies
└── json/                        # Jackson integration
    ├── MoneyJacksonModule.java
    ├── MoneyMixin.java
    └── ExchangeRateMixin.java
```

## Build Instructions

### Prerequisites
- Java 17 or later
- Maven 3.6+

### Standard Build Commands

Use the Maven Wrapper from the project root for reproducible, non-interactive builds:

```bash
# Clean and compile
./mvnw -B -ntp clean compile

# Run all tests (CRITICAL: Never comment out or mock failing tests)
./mvnw -B -ntp test

# Package library
./mvnw -B -ntp package

# Install to local repository
./mvnw -B -ntp install
```

### Reusable setup workflow (for CI and Agent callers)
- A reusable setup workflow is provided at `./.github/workflows/copilot-setup-steps.yml`.
- It installs Temurin JDK (input: `java-version`), enables Maven cache, ensures the wrapper is executable, prints tool versions, and can run an optional command via `inputs.run`.
- Example caller with matrix:
    - File: `./.github/workflows/ci.yml`
    - Calls the reusable workflow for Java 17 and 21 and runs `./mvnw -B -ntp clean test`.

### Dependencies
- Jackson 2.15.2 (core, databind, annotations)
- Lombok 1.18.28 (scope: provided)
- JUnit 4.13.2, AssertJ 3.24.2, Mockito 4.11.0 (test scope)
- JMH 1.37 (test scope: jmh-core, jmh-generator-annprocess)
- Self-contained: minimal replacements for internal annotations and KnownCurrencies are vendored in `src/main/java`; do not reintroduce private artifacts.

### Environment Setup
1. Ensure Java 17+ is configured
2. Run `./mvnw -B -ntp clean test` to validate environment
3. All tests must pass before making changes

## Development Guidelines

### Core Principles
- **Keep It Simple**: Optimize for automated coding via GitHub Coding Agent
- **MVP First**: Shortest path to working solution
- **No Over-engineering**: Clean but not bloated
- **Frequent Testing**: Test in lockstep with development
- **Never Remove Tests**: Always fix underlying behavior instead

### Code Standards
- Use meaningful variable/method names (avoid `foo`, `bar`)
- Immutable objects where possible (Money, ExchangeRate)
- BigDecimal for all monetary calculations (never float/double)
- Proper exception handling with IllegalArgumentException for invalid conversions
- Thread-safety considerations (volatile for UpdateableCurrencyConverter)

### Key Constraints
- All monetary amounts use BigDecimal for precision
- Currency conversions require proper decimal scaling
- Supports arbitrary currency pairs with synthetic cross-rate calculation (max 4 hops)
- Direct exchange rates take precedence over synthetic paths
- JSON serialization must not expose internal `rateValue` field
- Thread-safe updates for UpdateableCurrencyConverter

### Testing Requirements
- Full test coverage for conversion operations
- Test both TO_PRICE and PROPORTIONAL decimal strategies
- Test edge cases: same currency conversion, unknown currencies
- Mock external dependencies appropriately
- Integration tests for JSON serialization/deserialization
- Performance benchmarks for critical conversion paths

### Common Patterns
- Use `new Money(BigDecimal, ConvertableCurrency)` for monetary values
- Exchange rates created with base and quote values for any currency pair
- Conversion strategies: TO_PRICE for invoices, PROPORTIONAL for calculations
- Use EnumMap for currency-based lookups (performance optimization)
- BFS-based shortest-path finding for synthetic cross-rate calculation

### Error Handling
- `IllegalArgumentException` for unknown currencies, invalid conversion pairs, or no exchange rate path found
- `IllegalStateException` for conflicting exchange rates
- Validate currency compatibility in Money.compareTo()
- Maximum synthetic path length: 4 hops

### Integration Points
- Jackson JSON serialization via `MoneyJacksonModule`
- Standalone Maven POM with pinned plugin and dependency versions
- Internal annotations and i18n enum are replaced by vendored equivalents; keep the repository self-contained

## File Locations
- Main sources: `src/main/java/com/mercateo/common/currency/`
- Test sources: `src/test/java/com/mercateo/common/currency/`
- Build configuration: `pom.xml`

## Key Implementation Details

### Currency Conversion Process
1. Get exchange rate between currencies via CurrencyConverter.getExchangeRate()
   - Direct rates are returned if available (takes precedence)
   - Synthetic cross-rates are computed on-the-fly via shortest path (max 4 hops)
   - Throws IllegalArgumentException if no path exists
2. Apply conversion using ExchangeRate.convert() with appropriate DecimalPlacesStrategy
3. Handle same-currency conversions with proper scaling

### Performance Considerations
- EnumMap usage for O(1) currency lookups
- Lazy calculation and caching of rate values in ExchangeRate
- Minimal object creation in conversion paths
- BFS path finding with early termination for synthetic rates
- No persistent caching of synthetic paths (on-demand calculation)

### Extension Points
- Implement CurrencyConverter interface for different rate sources
- Add new currencies to ConvertableCurrency enum
- Extend DecimalPlacesStrategy for custom precision rules

## Validation Commands
Before any commit, always run:
```bash
./mvnw -B -ntp clean test
```

All tests must pass. If tests fail, fix the underlying issue - never disable or mock away failing tests.

## Notes for GitHub Coding Agent
- This codebase follows clean architecture principles
- All classes are well-documented with Javadoc
- Extensive test coverage provides good examples of expected behavior
- Focus on precision and correctness over performance optimizations
- Maintain immutability patterns where established
- Use existing patterns when adding new functionality