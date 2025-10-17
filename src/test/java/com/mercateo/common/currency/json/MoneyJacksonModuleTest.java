package com.mercateo.common.currency.json;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercateo.common.currency.ConvertableCurrency;
import com.mercateo.common.currency.ExchangeRate;
import com.mercateo.common.currency.Money;

@SuppressWarnings("null")
public class MoneyJacksonModuleTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new MoneyJacksonModule());
    }

    @Test
    public void shouldSerializeAndDeserializeMoney() throws Exception {
        Money money = new Money(new BigDecimal("99.99"), ConvertableCurrency.EUR);

        String json = objectMapper.writeValueAsString(money);
        Money deserialized = objectMapper.readValue(json, Money.class);

        assertThat(deserialized).isEqualTo(money);
        assertThat(deserialized.getAmount()).isEqualTo(money.getAmount());
        assertThat(deserialized.getCurrency()).isEqualTo(money.getCurrency());
    }

    @Test
    public void shouldSerializeAndDeserializeExchangeRate() throws Exception {
        Money baseValue = new Money(BigDecimal.ONE, ConvertableCurrency.EUR);
        Money quoteValue = new Money(new BigDecimal("1.12"), ConvertableCurrency.USD);
        ExchangeRate exchangeRate = new ExchangeRate(baseValue, quoteValue);

        String json = objectMapper.writeValueAsString(exchangeRate);
        ExchangeRate deserialized = objectMapper.readValue(json, ExchangeRate.class);

        assertThat(json).contains("baseValue");
        assertThat(json).contains("quoteValue");
        assertThat(json).doesNotContain("rateValue");
        assertThat(deserialized).isEqualTo(exchangeRate);
    }

    @Test
    public void shouldSerializeComposedExchangeRate_withoutExposingCalculationDetails() throws Exception {
        // Create a composed exchange rate (simulating what synthetic cross-rates produce)
        Money baseValue = new Money(BigDecimal.ONE, ConvertableCurrency.USD);
        Money quoteValue = new Money(new BigDecimal("0.7728"), ConvertableCurrency.GBP);
        ExchangeRate composedRate = new ExchangeRate(baseValue, quoteValue);

        String json = objectMapper.writeValueAsString(composedRate);
        ExchangeRate deserialized = objectMapper.readValue(json, ExchangeRate.class);

        // Verify JSON structure: should not expose internal rateValue or composition details
        assertThat(json).contains("baseValue");
        assertThat(json).contains("quoteValue");
        assertThat(json).doesNotContain("rateValue");
        assertThat(json).doesNotContain("synthetic");
        assertThat(json).doesNotContain("path");
        
        // Verify deserialized rate is equivalent
        assertThat(deserialized).isEqualTo(composedRate);
        assertThat(deserialized.getRateValue().getAmount()).isEqualByComparingTo(composedRate.getRateValue().getAmount());
    }
}