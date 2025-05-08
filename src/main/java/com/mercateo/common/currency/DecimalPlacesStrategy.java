package com.mercateo.common.currency;

/**
 * Defines strategies for determining the number of decimal places to use in currency conversions.
 * Different contexts (like invoices or internal calculations) may require different precision rules.
 */
public enum DecimalPlacesStrategy {
    /**
     * Strategy for general calculations that preserves relative decimal precision.
     * Adjusts scale based on the difference between source and target currency default scales.
     */
    PROPORTIONAL {
        @Override
        public int getRequiredScale(Money convertedMoney, ConvertableCurrency targetCurrency) {
            return convertedMoney.getAmount().scale()
                    + targetCurrency.getDefaultScale()
                    - convertedMoney .getCurrency().getDefaultScale();
        }
    },
    /**
     * Strategy for invoice formatting that uses the target currency's default scale.
     * Ensures consistent display of monetary amounts on invoices.
     */
    FOR_INVOICE {
        @Override
        public int getRequiredScale(Money convertedMoney, ConvertableCurrency targetCurrency) {
            return targetCurrency.getDefaultScale();
        }
    };

    /**
     * Determines the required scale (decimal places) for a currency conversion.
     *
     * @param convertedMoney The money amount being converted
     * @param targetCurrency The target currency for the conversion
     * @return The number of decimal places to use in the result
     */
    public abstract int getRequiredScale(Money convertedMoney, ConvertableCurrency targetCurrency);
}
