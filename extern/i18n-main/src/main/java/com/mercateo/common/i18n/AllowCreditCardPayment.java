package com.mercateo.common.i18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.mercateo.common.util.annotations.NonNullByDefault;

@NonNullByDefault
public final class AllowCreditCardPayment {

    private final boolean isCreditCardPaymentAllowed;

    private final List<CreditCardProvider> creditCardProviders;

    private AllowCreditCardPayment(final boolean isCreditCardPaymentAllowed,
            final List<CreditCardProvider> creditCardProviders) {
        this.isCreditCardPaymentAllowed = isCreditCardPaymentAllowed;
        this.creditCardProviders = creditCardProviders;
    }

    public static AllowCreditCardPayment createAllowed(
            final List<CreditCardProvider> creditCardProviders) {
        return new AllowCreditCardPayment(true, creditCardProviders);
    }

    public static AllowCreditCardPayment createAllowedWithVisaMaestroMasterCard() {
        return createAllowed(Arrays.asList(new CreditCardProvider[] {
                CreditCardProvider.VISA, CreditCardProvider.MAESTRO,
                CreditCardProvider.MASTERCARD }));
    }

    public static AllowCreditCardPayment createAllowedWithVisaMasterCard() {
        return createAllowed(Arrays.asList(new CreditCardProvider[] { CreditCardProvider.VISA,
                CreditCardProvider.MASTERCARD }));
    }

    public static AllowCreditCardPayment createAllowedWithVisaMasterCardAmericanExpress() {
        return createAllowed(Arrays.asList(new CreditCardProvider[] { CreditCardProvider.VISA,
                CreditCardProvider.MASTERCARD, CreditCardProvider.AMERICAN_EXPRESS }));
    }

    public static AllowCreditCardPayment createNotAllowed() {
        return new AllowCreditCardPayment(false, Collections.emptyList());
    }

    public boolean isCreditCardPaymentAllowed() {
        return this.isCreditCardPaymentAllowed;
    }

    public boolean isProvider(CreditCardProvider provider) {
        return this.creditCardProviders.contains(provider);
    }

    @Override
    public String toString() {
        return "AllowCreditCardPayment [isCreditCardPaymentAllowed=" + isCreditCardPaymentAllowed
                + ", creditCardProviders=" + creditCardProviders + "]";
    }
}
