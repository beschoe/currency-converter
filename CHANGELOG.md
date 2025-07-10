# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Version 0.0.5

### Fixed
- Improve javadoc for CurrencyConverter.convertToPrice
- Improve test names

## Version 0.0.4

### Added
- **CurrencyConverter Interface**: Extracted interface from existing implementation for better abstraction
- **FrozenCurrencyConverter**: Renamed original implementation having the immutable rates
- **UpdateableCurrencyConverter**: New implementation that avoids propagating providers
- **Money Comparable Interface**: Money class now implements Comparable for sorting and comparison operations
- **Comprehensive Unit Tests**: Added unit tests for Money comparison logic and UpdateableCurrencyConverter

### Changed
- **Breaking**: Renamed original CurrencyConverter implementation class (now FixedRatesCurrencyConverter)
- **Dependency**: Added Mockito dependency for improved testing capabilities

## Version 0.0.3

### Changed
- **Method Naming**: Renamed `convertForInvoice` to `convertToPrice` for better clarity
- **JSON Serialization**: Enhanced JSON handling by ignoring `rateValue` in serialization

## Version 0.0.2

### Added
- **Decimal Places Strategy**: Added support for invoice-specific and calculation-specific decimal places
- **Enhanced Currency Conversion**: Refactored currency conversion methods for better precision control

### Changed
- **DecimalPlacesStrategy**: Renamed enum value from `FOR_CALCULATIONS` to `PROPORTIONALLY`
- **API Structure**: Moved `convert` method from Money class to ExchangeRate class for better organization

### Fixed
- **Same Currency Conversion**: Fixed decimal places handling when converting to the same currency
- **Code Quality**: Suppressed null warnings where appropriate

## Version 0.0.1

### Added
- **Initial Implementation**: Currency converter for a single bank
- **Core Classes**: Money, ExchangeRate, ConvertableCurrency, DecimalPlacesStrategy
- **JSON Support**: Jackson serialization/deserialization support
- **Documentation**: README and basic Javadoc