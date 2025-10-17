/*
 * Created on 19 Mar 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

import com.mercateo.common.util.annotations.NonNull;

/**
 * Handles conversion of monetary amounts between different currencies.
 *
 * This class manages a collection of exchange rates and provides methods
 * to convert between any supported currencies. Exchange rates can be specified
 * with arbitrary base currencies, and synthetic cross-rates are computed on-the-fly
 * using the shortest path between currencies (limited to 4 hops).
 */
public class FrozenCurrencyConverter implements CurrencyConverter {
    private static final int MAX_HOPS = 4;
    private static final RoundingMode DIVISION_ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int DIVISION_SCALE = 10;
    
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> rates;

    /**
     * Creates a new currency converter with the specified exchange rates.
     *
     * @throws IllegalStateException if duplicate exchange rates are provided
     */
    public FrozenCurrencyConverter(Collection<ExchangeRate> rateCollection) throws IllegalStateException {
        super();
        this.rates = new EnumMap<>(ConvertableCurrency.class);
        rateCollection.forEach(this::addExchangeRate);
        // Also add inverted rates for bidirectional lookup
        rateCollection.stream().map(ExchangeRate::invert)
        .forEach(rate -> computeExchangeRateIfAbsent(rate.getBaseCurrency(), rate.getQuoteCurrency(), x -> rate));
    }


    @SuppressWarnings("null")
    private void addExchangeRate(@NonNull ExchangeRate rate) {
        ExchangeRate knownRate = rates.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class))
                .put(rate.getQuoteCurrency(),  rate);
        if(knownRate != null && ! knownRate.equals(rate))
            throw new IllegalStateException("conflicting rates " + rate + " and " + knownRate);
    }


    /* (non-Javadoc)
     * @see com.mercateo.common.currency.CurrencyConverter#convertToPrice(com.mercateo.common.currency.Money, com.mercateo.common.currency.ConvertableCurrency)
     */
    @Override
    public Money convertToPrice(Money fromAmount, ConvertableCurrency toCurrency) throws IllegalArgumentException {
        return convert(fromAmount, toCurrency, DecimalPlacesStrategy.TO_PRICE, toCurrency.getRoundingMode());
    }

    /* (non-Javadoc)
     * @see com.mercateo.common.currency.CurrencyConverter#convertProportionally(com.mercateo.common.currency.Money, com.mercateo.common.currency.ConvertableCurrency)
     */
    @Override
    public Money convertProportionally(Money fromAmount, ConvertableCurrency toCurrency) throws IllegalArgumentException {
        return convert(fromAmount, toCurrency, DecimalPlacesStrategy.PROPORTIONAL, toCurrency.getRoundingMode());
    }

    /* (non-Javadoc)
     * @see com.mercateo.common.currency.CurrencyConverter#convert(com.mercateo.common.currency.Money, com.mercateo.common.currency.ConvertableCurrency, com.mercateo.common.currency.DecimalPlacesStrategy, java.math.RoundingMode)
     */
    @Override
    public Money convert(Money fromAmount, ConvertableCurrency toCurrency, DecimalPlacesStrategy decimalPlacesStrategy, RoundingMode roundingMode) throws IllegalArgumentException {
        if(fromAmount.getCurrency().equals(toCurrency)) {
            final int requiredScale = decimalPlacesStrategy.getRequiredScale(fromAmount, toCurrency);
            if(requiredScale == fromAmount.getAmount().scale())
                return fromAmount;
            else
                return new Money(fromAmount.getAmount().setScale(requiredScale, roundingMode), toCurrency);
        }
        ExchangeRate exchangeRate = getExchangeRate(fromAmount.getCurrency(), toCurrency);
        return exchangeRate.convert(fromAmount, decimalPlacesStrategy, roundingMode);
    }

    /* (non-Javadoc)
     * @see com.mercateo.common.currency.CurrencyConverter#getExchangeRate(com.mercateo.common.currency.ConvertableCurrency, com.mercateo.common.currency.ConvertableCurrency)
     */
    @Override
    public ExchangeRate getExchangeRate(ConvertableCurrency fromCurrency, ConvertableCurrency toCurrency) throws IllegalArgumentException {
        return computeExchangeRateIfAbsent(fromCurrency, toCurrency,
                fromCurrency == toCurrency ? ExchangeRate::identity : x -> calculateRate(fromCurrency, toCurrency));
    }

    @SuppressWarnings("null")
    private ExchangeRate calculateRate(ConvertableCurrency fromCurrency, ConvertableCurrency toCurrency) {
        // Try direct rate first
        ExchangeRate directRate = getDirectRate(fromCurrency, toCurrency);
        if (directRate != null) {
            return directRate;
        }
        
        // Find shortest path using BFS
        List<ConvertableCurrency> path = findShortestPath(fromCurrency, toCurrency);
        if (path == null) {
            throw new IllegalArgumentException("No exchange rate path found from " + fromCurrency + " to " + toCurrency);
        }
        
        // Compose exchange rates along the path
        return composePath(path);
    }
    
    private ExchangeRate getDirectRate(ConvertableCurrency from, ConvertableCurrency to) {
        EnumMap<ConvertableCurrency, ExchangeRate> fromRates = rates.get(from);
        return fromRates != null ? fromRates.get(to) : null;
    }
    
    /**
     * Finds the shortest path between two currencies using BFS.
     * Returns null if no path exists or if the path exceeds MAX_HOPS.
     */
    private List<ConvertableCurrency> findShortestPath(ConvertableCurrency from, ConvertableCurrency to) {
        Deque<List<ConvertableCurrency>> queue = new ArrayDeque<>();
        EnumSet<ConvertableCurrency> visited = EnumSet.of(from);
        
        List<ConvertableCurrency> initial = new ArrayList<>();
        initial.add(from);
        queue.add(initial);
        
        while (!queue.isEmpty()) {
            List<ConvertableCurrency> path = queue.removeFirst();
            ConvertableCurrency current = path.get(path.size() - 1);
            
            // Check hop limit
            if (path.size() > MAX_HOPS) {
                continue;
            }
            
            // Get neighbors (currencies we can convert to from current)
            EnumMap<ConvertableCurrency, ExchangeRate> neighbors = rates.get(current);
            if (neighbors == null) {
                continue;
            }
            
            // Process neighbors in deterministic order (enum ordinal)
            for (ConvertableCurrency next : ConvertableCurrency.values()) {
                if (!neighbors.containsKey(next)) {
                    continue;
                }
                
                if (next == to) {
                    // Found target - construct and return path
                    List<ConvertableCurrency> resultPath = new ArrayList<>(path);
                    resultPath.add(next);
                    return resultPath;
                }
                
                if (!visited.contains(next)) {
                    visited.add(next);
                    List<ConvertableCurrency> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    queue.add(newPath);
                }
            }
        }
        
        return null; // No path found
    }
    
    /**
     * Composes exchange rates along a path of currencies.
     */
    private ExchangeRate composePath(List<ConvertableCurrency> path) {
        ExchangeRate composed = getDirectRate(path.get(0), path.get(1));
        
        for (int i = 1; i < path.size() - 1; i++) {
            ExchangeRate nextRate = getDirectRate(path.get(i), path.get(i + 1));
            composed = composeRates(composed, nextRate);
        }
        
        return composed;
    }
    
    /**
     * Composes two exchange rates: if we have A->B and B->C, returns A->C.
     */
    private ExchangeRate composeRates(ExchangeRate first, ExchangeRate second) {
        // first: base1 -> quote1
        // second: base2 -> quote2
        // Requirement: quote1.currency == base2.currency
        if (!first.getQuoteCurrency().equals(second.getBaseCurrency())) {
            throw new IllegalArgumentException("Cannot compose rates: currencies don't match");
        }
        
        // We want to create a rate A->C from A->B and B->C
        // Mathematically: if X units of A = Y units of B, and Y units of B = Z units of C,
        // then X units of A = Z units of C
        
        // Scale the second rate so its base matches first's quote
        // first: base1 -> quote1
        // second: base2 -> quote2
        // We need: quote1.amount == base2.amount for direct composition
        
        // Calculate the scaling factor
        BigDecimal scaleFactor = first.getQuoteValue().getAmount()
                .divide(second.getBaseValue().getAmount(), DIVISION_SCALE, DIVISION_ROUNDING_MODE);
        
        // Scale second's quote by the scale factor
        Money scaledQuote = new Money(
            second.getQuoteValue().getAmount().multiply(scaleFactor),
            second.getQuoteCurrency()
        );
        
        // Now we can compose: first.base -> scaledQuote
        return new ExchangeRate(first.getBaseValue(), scaledQuote);
    }


    private ExchangeRate computeExchangeRateIfAbsent(ConvertableCurrency fromCurrency,
            ConvertableCurrency toCurrency, Function<ConvertableCurrency, ExchangeRate> rateCalculator) {
        return rates.computeIfAbsent(fromCurrency, x -> new EnumMap<>(ConvertableCurrency.class))
        .computeIfAbsent(toCurrency, rateCalculator);
    }

    @Override
    public String toString() {
        return "MoneyExchange [rates=" + rates + "]";
    }
}