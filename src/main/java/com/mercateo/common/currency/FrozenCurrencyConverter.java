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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

import com.mercateo.common.util.annotations.NonNull;

/**
 * Handles conversion of monetary amounts between different currencies.
 *
 * This class manages a collection of exchange rates and provides methods
 * to convert between any supported currencies. Supports unrestricted currency
 * pairs and computes synthetic cross-rates on-the-fly when direct rates are unavailable.
 */
public class FrozenCurrencyConverter implements CurrencyConverter {
    private static final int MAX_HOPS = 4;
    private static final int DIVISION_SCALE = 10;
    private static final RoundingMode DIVISION_ROUNDING_MODE = RoundingMode.HALF_EVEN;
    
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> directRates;
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> rates;

    /**
     * Creates a new currency converter with the specified exchange rates.
     * Supports unrestricted currency pairs - rates no longer need to share a common base.
     *
     * @throws IllegalStateException if duplicate exchange rates are provided
     */
    public FrozenCurrencyConverter(Collection<ExchangeRate> rateCollection) throws IllegalStateException {
        super();
        this.directRates = new EnumMap<>(ConvertableCurrency.class);
        this.rates = new EnumMap<>(ConvertableCurrency.class);
        
        // Store all direct rates in a bidirectional map
        rateCollection.forEach(this::addExchangeRate);
        
        // Also add inverted rates as direct rates for path finding
        rateCollection.stream()
            .map(ExchangeRate::invert)
            .forEach(rate -> {
                directRates.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class))
                    .putIfAbsent(rate.getQuoteCurrency(), rate);
                computeExchangeRateIfAbsent(rate.getBaseCurrency(), rate.getQuoteCurrency(), x -> rate);
            });
    }


    @SuppressWarnings("null")
    private void addExchangeRate(@NonNull ExchangeRate rate) {
        // Add to directRates for path finding
        ExchangeRate knownDirectRate = directRates.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class))
                .put(rate.getQuoteCurrency(), rate);
        if(knownDirectRate != null && !knownDirectRate.equals(rate))
            throw new IllegalStateException("conflicting rates " + rate + " and " + knownDirectRate);
        
        // Also add to rates for caching
        ExchangeRate knownRate = rates.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class))
                .put(rate.getQuoteCurrency(), rate);
        if(knownRate != null && !knownRate.equals(rate))
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
                fromCurrency == toCurrency ? ExchangeRate::identity : x -> calculateDerivedRate(fromCurrency, toCurrency));
    }

    @SuppressWarnings("null")
    private ExchangeRate calculateDerivedRate(ConvertableCurrency fromCurrency, ConvertableCurrency toCurrency) {
        // Check if we have a direct rate first (direct rate takes precedence)
        EnumMap<ConvertableCurrency, ExchangeRate> fromRates = directRates.get(fromCurrency);
        if (fromRates != null) {
            ExchangeRate directRate = fromRates.get(toCurrency);
            if (directRate != null) {
                return directRate;
            }
        }
        
        // No direct rate found, find shortest path and compose synthetic rate
        List<ExchangeRate> path = findShortestPath(fromCurrency, toCurrency);
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("No exchange rate path found from " + fromCurrency + " to " + toCurrency);
        }
        
        return composePath(path);
    }
    
    /**
     * Finds the shortest path between two currencies using BFS.
     * Returns null if no path exists within the hop limit.
     */
    private List<ExchangeRate> findShortestPath(ConvertableCurrency from, ConvertableCurrency to) {
        // BFS to find shortest path
        Queue<PathNode> queue = new ArrayDeque<>();
        EnumSet<ConvertableCurrency> visited = EnumSet.noneOf(ConvertableCurrency.class);
        
        queue.add(new PathNode(from, null, null));
        visited.add(from);
        
        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            
            // Check hop limit
            if (current.getPathLength() >= MAX_HOPS) {
                continue;
            }
            
            // Get all outgoing edges from current currency
            EnumMap<ConvertableCurrency, ExchangeRate> outgoingRates = directRates.get(current.currency);
            if (outgoingRates == null) {
                continue;
            }
            
            for (ExchangeRate rate : outgoingRates.values()) {
                ConvertableCurrency nextCurrency = rate.getQuoteCurrency();
                
                // Found target
                if (nextCurrency == to) {
                    return buildPath(current, rate);
                }
                
                // Continue search if not visited
                if (!visited.contains(nextCurrency)) {
                    visited.add(nextCurrency);
                    queue.add(new PathNode(nextCurrency, current, rate));
                }
            }
        }
        
        return null; // No path found
    }
    
    /**
     * Builds the path from the target back to the source.
     */
    private List<ExchangeRate> buildPath(PathNode target, ExchangeRate finalRate) {
        List<ExchangeRate> path = new ArrayList<>();
        PathNode current = target;
        
        // Add final rate
        path.add(finalRate);
        
        // Walk back through parent chain
        while (current.parent != null) {
            path.add(0, current.rate);
            current = current.parent;
        }
        
        return path;
    }
    
    /**
     * Composes a synthetic exchange rate from a path of exchange rates.
     * Uses the baseValue/quoteValue amounts to preserve precision similar to the old withBase approach.
     */
    private ExchangeRate composePath(List<ExchangeRate> path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }
        
        if (path.size() == 1) {
            return path.get(0);
        }
        
        // For paths that connect through common intermediates with matching base values,
        // we can use the old withBase() approach for better precision
        // This requires that rates share the same base amount (e.g., both "1 EUR")
        
        // Check if this is a simple 2-hop path where we can use withBase logic
        // Path like: A->B->C where B is the intermediate with matching base values
        if (path.size() == 2) {
            ExchangeRate firstRate = path.get(0);
            ExchangeRate secondRate = path.get(1);
            
            // Check if they connect through a common intermediate with matching base values
            if (firstRate.getQuoteCurrency() == secondRate.getBaseCurrency() &&
                firstRate.getQuoteValue().equals(secondRate.getBaseValue())) {
                // Use withBase approach: ExchangeRate(firstBase, secondQuote)
                return new ExchangeRate(firstRate.getBaseValue(), secondRate.getQuoteValue());
            }
        }
        
        // General case: compute composite rate by multiplying rate values
        ConvertableCurrency baseCurrency = path.get(0).getBaseCurrency();
        ConvertableCurrency quoteCurrency = path.get(path.size() - 1).getQuoteCurrency();
        
        BigDecimal compositeRate = BigDecimal.ONE;
        for (ExchangeRate rate : path) {
            compositeRate = compositeRate.multiply(rate.getRateValue().getAmount());
        }
        
        Money baseValue = new Money(BigDecimal.ONE, baseCurrency);
        Money quoteValue = new Money(compositeRate, quoteCurrency);
        
        return new ExchangeRate(baseValue, quoteValue);
    }
    
    /**
     * Helper class to track path during BFS traversal.
     */
    private static class PathNode {
        final ConvertableCurrency currency;
        final PathNode parent;
        final ExchangeRate rate;
        
        PathNode(ConvertableCurrency currency, PathNode parent, ExchangeRate rate) {
            this.currency = currency;
            this.parent = parent;
            this.rate = rate;
        }
        
        int getPathLength() {
            int length = 0;
            PathNode current = this;
            while (current.parent != null) {
                length++;
                current = current.parent;
            }
            return length;
        }
    }


    private ExchangeRate computeExchangeRateIfAbsent(ConvertableCurrency fromCurrency,
            ConvertableCurrency toCurrency, Function<ConvertableCurrency, ExchangeRate> rateCalculator) {
        return rates.computeIfAbsent(fromCurrency, x -> new EnumMap<>(ConvertableCurrency.class))
        .computeIfAbsent(toCurrency, rateCalculator);
    }

    @Override
    public String toString() {
        return "FrozenCurrencyConverter [directRates=" + directRates + "]";
    }
}