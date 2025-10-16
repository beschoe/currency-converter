/*
 * Created on 19 Mar 2025
 *
 * author dimitry
 */
package com.mercateo.common.currency;

import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Queue;

import com.mercateo.common.util.annotations.NonNull;
import com.mercateo.common.util.annotations.Nullable;

/**
 * Handles conversion of monetary amounts between different currencies.
 *
 * This class manages a collection of exchange rates and provides methods
 * to convert between any supported currencies. It supports arbitrary currency
 * pairs and can compute synthetic cross-rates on demand by finding the shortest
 * path through available exchange rates.
 */
public class FrozenCurrencyConverter implements CurrencyConverter {
    private static final int MAX_HOPS = 4;
    
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> directRates;
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> cachedRates;

    /**
     * Creates a new currency converter with the specified exchange rates.
     * Supports arbitrary currency pairs without requiring a common base currency.
     *
     * @throws IllegalStateException if duplicate exchange rates are provided
     */
    public FrozenCurrencyConverter(Collection<ExchangeRate> rateCollection) throws IllegalStateException {
        super();
        this.directRates = new EnumMap<>(ConvertableCurrency.class);
        this.cachedRates = new EnumMap<>(ConvertableCurrency.class);
        
        // Store all direct rates and their inverses
        rateCollection.forEach(this::addExchangeRate);
        rateCollection.stream()
            .map(ExchangeRate::invert)
            .forEach(this::addExchangeRate);
    }

    @SuppressWarnings("null")
    private void addExchangeRate(@NonNull ExchangeRate rate) {
        EnumMap<ConvertableCurrency, ExchangeRate> fromMap = 
            directRates.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class));
        
        ExchangeRate knownRate = fromMap.get(rate.getQuoteCurrency());
        if (knownRate != null && !knownRate.equals(rate)) {
            throw new IllegalStateException("conflicting rates " + rate + " and " + knownRate);
        }
        fromMap.put(rate.getQuoteCurrency(), rate);
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
        // Same currency - return identity
        if (fromCurrency == toCurrency) {
            return ExchangeRate.identity(fromCurrency);
        }
        
        // Check cached rates first
        ExchangeRate cached = getCachedRate(fromCurrency, toCurrency);
        if (cached != null) {
            return cached;
        }
        
        // Try direct rate
        ExchangeRate direct = getDirectRate(fromCurrency, toCurrency);
        if (direct != null) {
            cacheRate(fromCurrency, toCurrency, direct);
            return direct;
        }
        
        // Try synthetic path
        ExchangeRate synthetic = findShortestPath(fromCurrency, toCurrency);
        if (synthetic != null) {
            cacheRate(fromCurrency, toCurrency, synthetic);
            return synthetic;
        }
        
        throw new IllegalArgumentException("No exchange rate path found from " + fromCurrency + " to " + toCurrency);
    }

    @Nullable
    private ExchangeRate getCachedRate(ConvertableCurrency from, ConvertableCurrency to) {
        EnumMap<ConvertableCurrency, ExchangeRate> fromMap = cachedRates.get(from);
        return fromMap != null ? fromMap.get(to) : null;
    }

    private void cacheRate(ConvertableCurrency from, ConvertableCurrency to, ExchangeRate rate) {
        cachedRates.computeIfAbsent(from, x -> new EnumMap<>(ConvertableCurrency.class))
            .put(to, rate);
    }

    @Nullable
    private ExchangeRate getDirectRate(ConvertableCurrency from, ConvertableCurrency to) {
        EnumMap<ConvertableCurrency, ExchangeRate> fromMap = directRates.get(from);
        return fromMap != null ? fromMap.get(to) : null;
    }

    /**
     * Finds the shortest path between two currencies using BFS.
     * Returns null if no path exists within the hop limit.
     */
    @Nullable
    private ExchangeRate findShortestPath(ConvertableCurrency from, ConvertableCurrency to) {
        Queue<PathNode> queue = new ArrayDeque<>();
        EnumSet<ConvertableCurrency> visited = EnumSet.of(from);
        queue.add(new PathNode(from, null, null, 0));
        
        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            
            // Check if we've exceeded hop limit
            if (current.hops >= MAX_HOPS) {
                continue;
            }
            
            // Get all neighbors (currencies we can convert to)
            EnumMap<ConvertableCurrency, ExchangeRate> neighbors = directRates.get(current.currency);
            if (neighbors == null) {
                continue;
            }
            
            // Process neighbors in deterministic order (enum natural order)
            for (ConvertableCurrency neighbor : ConvertableCurrency.values()) {
                ExchangeRate rate = neighbors.get(neighbor);
                if (rate == null) {
                    continue;
                }
                
                // Found target
                if (neighbor == to) {
                    return reconstructRate(current, rate);
                }
                
                // Add to queue if not visited
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(new PathNode(neighbor, current, rate, current.hops + 1));
                }
            }
        }
        
        return null;
    }

    private ExchangeRate reconstructRate(PathNode node, ExchangeRate finalRate) {
        List<ExchangeRate> path = new ArrayList<>();
        
        // Collect rates in reverse order
        path.add(finalRate);
        PathNode current = node;
        while (current.previousRate != null) {
            path.add(current.previousRate);
            current = current.previous;
        }
        
        // Compose rates in correct order (from end to start)
        ExchangeRate result = path.get(path.size() - 1);
        for (int i = path.size() - 2; i >= 0; i--) {
            result = result.compose(path.get(i));
        }
        
        return result;
    }

    /**
     * Helper class for BFS pathfinding
     */
    private static class PathNode {
        final ConvertableCurrency currency;
        final @Nullable PathNode previous;
        final @Nullable ExchangeRate previousRate;
        final int hops;
        
        PathNode(ConvertableCurrency currency, @Nullable PathNode previous, 
                @Nullable ExchangeRate previousRate, int hops) {
            this.currency = currency;
            this.previous = previous;
            this.previousRate = previousRate;
            this.hops = hops;
        }
    }

    @Override
    public String toString() {
        return "FrozenCurrencyConverter [directRates=" + directRates + "]";
    }
}