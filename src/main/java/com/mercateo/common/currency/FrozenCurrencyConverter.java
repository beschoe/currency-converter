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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;

import com.mercateo.common.util.annotations.NonNull;
import com.mercateo.common.util.annotations.Nullable;

/**
 * Handles conversion of monetary amounts between different currencies.
 *
 * This class manages a collection of exchange rates and provides methods
 * to convert between any supported currencies. Supports arbitrary currency
 * pairs and computes synthetic cross-rates on demand via shortest-path finding.
 */
public class FrozenCurrencyConverter implements CurrencyConverter {
    private static final int MAX_PATH_HOPS = 4;
    
    private final EnumMap<ConvertableCurrency, ExchangeRate> directRates;
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> rates;
    private final EnumMap<ConvertableCurrency, EnumMap<ConvertableCurrency, ExchangeRate>> adjacencyList;

    /**
     * Creates a new currency converter with the specified exchange rates.
     *
     * @throws IllegalStateException if duplicate exchange rates are provided
     */
    public FrozenCurrencyConverter(Collection<ExchangeRate> rateCollection) throws IllegalStateException {
        super();
        this.adjacencyList = new EnumMap<>(ConvertableCurrency.class);
        this.directRates = new EnumMap<>(ConvertableCurrency.class);
        this.rates = new EnumMap<>(ConvertableCurrency.class);
        
        // Build adjacency list for arbitrary pairs
        rateCollection.forEach(this::addToAdjacencyList);
        
        // Maintain backward compatibility: store rates with quote as key for old base-currency logic
        rateCollection.forEach(rate -> {
            if (rate.getBaseCurrency() == rate.getQuoteCurrency()) {
                return; // Skip identity rates
            }
            ExchangeRate existing = directRates.put(rate.getQuoteCurrency(), rate);
            if (existing != null && !existing.equals(rate)) {
                // Only throw if different rates for same quote currency with compatible bases
                if (existing.getBaseCurrency().equals(rate.getBaseCurrency())) {
                    throw new IllegalStateException("conflicting rates " + rate + " and " + existing);
                }
            }
        });
        
        // Cache direct rates and their inverses
        rateCollection.forEach(this::addExchangeRate);
        rateCollection.stream().map(ExchangeRate::invert)
            .forEach(rate -> computeExchangeRateIfAbsent(rate.getBaseCurrency(), rate.getQuoteCurrency(), x -> rate));
    }


    @SuppressWarnings("null")
    private void addExchangeRate(@NonNull ExchangeRate rate) {
        ExchangeRate knownRate = rates.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class))
                .put(rate.getQuoteCurrency(), rate);
        if(knownRate != null && ! knownRate.equals(rate))
            throw new IllegalStateException("conflicting rates " + rate + " and " + knownRate);
    }

    @SuppressWarnings("null")
    private void addToAdjacencyList(@NonNull ExchangeRate rate) {
        // Add the directed edge
        adjacencyList.computeIfAbsent(rate.getBaseCurrency(), x -> new EnumMap<>(ConvertableCurrency.class))
                .put(rate.getQuoteCurrency(), rate);
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
                fromCurrency == toCurrency ? ExchangeRate::identity : x -> findExchangeRate(fromCurrency, toCurrency));
    }

    @SuppressWarnings("null")
    private ExchangeRate findExchangeRate(ConvertableCurrency fromCurrency, ConvertableCurrency toCurrency) {
        // Check for direct rate first (takes precedence)
        EnumMap<ConvertableCurrency, ExchangeRate> fromMap = adjacencyList.get(fromCurrency);
        if (fromMap != null) {
            ExchangeRate directRate = fromMap.get(toCurrency);
            if (directRate != null) {
                return directRate;
            }
        }
        
        // Try old base currency logic for backward compatibility
        ExchangeRate knownRate = directRates.get(toCurrency);
        ExchangeRate newBaseRate = directRates.get(fromCurrency);
        if (knownRate != null && newBaseRate != null) {
            try {
                return knownRate.withBase(newBaseRate);
            } catch (IllegalArgumentException e) {
                // Different bases, fall through to path finding
            }
        }
        
        // Find synthetic path via BFS
        List<ExchangeRate> path = findShortestPath(fromCurrency, toCurrency);
        if (path == null) {
            throw new IllegalArgumentException("No exchange rate path found from " + fromCurrency + " to " + toCurrency);
        }
        
        return composePath(path);
    }
    
    @Nullable
    private List<ExchangeRate> findShortestPath(ConvertableCurrency from, ConvertableCurrency to) {
        if (from == to) {
            return new ArrayList<>();
        }
        
        Queue<ConvertableCurrency> queue = new ArrayDeque<>();
        Map<ConvertableCurrency, ConvertableCurrency> parent = new LinkedHashMap<>();
        Map<ConvertableCurrency, ExchangeRate> edgeToNode = new LinkedHashMap<>();
        EnumSet<ConvertableCurrency> visited = EnumSet.of(from);
        
        queue.add(from);
        parent.put(from, null);
        
        int hopCount = 0;
        int nodesAtCurrentLevel = 1;
        int nodesAtNextLevel = 0;
        
        while (!queue.isEmpty() && hopCount < MAX_PATH_HOPS) {
            ConvertableCurrency current = queue.poll();
            nodesAtCurrentLevel--;
            
            if (current == to) {
                // Reconstruct path
                return reconstructPath(from, to, parent, edgeToNode);
            }
            
            // Explore neighbors
            EnumMap<ConvertableCurrency, ExchangeRate> neighbors = adjacencyList.get(current);
            if (neighbors != null) {
                for (Map.Entry<ConvertableCurrency, ExchangeRate> entry : neighbors.entrySet()) {
                    ConvertableCurrency next = entry.getKey();
                    if (!visited.contains(next)) {
                        visited.add(next);
                        parent.put(next, current);
                        edgeToNode.put(next, entry.getValue());
                        queue.add(next);
                        nodesAtNextLevel++;
                        
                        if (next == to) {
                            // Found target, reconstruct path immediately
                            return reconstructPath(from, to, parent, edgeToNode);
                        }
                    }
                }
            }
            
            if (nodesAtCurrentLevel == 0) {
                hopCount++;
                nodesAtCurrentLevel = nodesAtNextLevel;
                nodesAtNextLevel = 0;
            }
        }
        
        return null; // No path found
    }
    
    private List<ExchangeRate> reconstructPath(ConvertableCurrency from, ConvertableCurrency to,
            Map<ConvertableCurrency, ConvertableCurrency> parent, Map<ConvertableCurrency, ExchangeRate> edgeToNode) {
        List<ExchangeRate> path = new ArrayList<>();
        ConvertableCurrency current = to;
        
        while (current != from) {
            ExchangeRate edge = edgeToNode.get(current);
            path.add(0, edge);
            current = parent.get(current);
        }
        
        return path;
    }
    
    private ExchangeRate composePath(List<ExchangeRate> path) {
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Cannot compose empty path");
        }
        
        ExchangeRate result = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            result = composeRates(result, path.get(i));
        }
        
        return result;
    }
    
    private ExchangeRate composeRates(ExchangeRate first, ExchangeRate second) {
        // first: A -> B, second: B -> C, result: A -> C
        if (!first.getQuoteCurrency().equals(second.getBaseCurrency())) {
            throw new IllegalArgumentException("Cannot compose rates: currencies don't match");
        }
        
        // Calculate composed rate: if 1 A = X B and 1 B = Y C, then 1 A = X*Y C
        Money composedQuote = new Money(
            first.getRateValue().getAmount().multiply(second.getRateValue().getAmount()),
            second.getQuoteCurrency()
        );
        
        return new ExchangeRate(first.getBaseValue(), composedQuote);
    }


    private ExchangeRate computeExchangeRateIfAbsent(ConvertableCurrency fromCurrency,
            ConvertableCurrency toCurrency, Function<ConvertableCurrency, ExchangeRate> rateCalculator) {
        return rates.computeIfAbsent(fromCurrency, x -> new EnumMap<>(ConvertableCurrency.class))
        .computeIfAbsent(toCurrency, rateCalculator);
    }

    @Override
    public String toString() {
        return "MoneyExchange [rates=" + directRates + "]";
    }
}