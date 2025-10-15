package com.mercateo.common.i18n;

import java.io.Serializable;

public class CompanyRegister implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String registerId;

    private final String countryCode;

    private final String localizedRegisterName;

    private final String localizedRegisterNumberName;

    public CompanyRegister(String registerId, String countryCode, String localizedRegisterName,
            String localizedRegisterNumberName) {
        this.registerId = registerId;
        this.countryCode = countryCode;
        this.localizedRegisterName = localizedRegisterName;
        this.localizedRegisterNumberName = localizedRegisterNumberName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getRegisterId() {
        return registerId;
    }

    public String getLocalizedRegisterName() {
        return localizedRegisterName;
    }

    public String getLocalizedRegisterNumberName() {
        return localizedRegisterNumberName;
    }

    @Override
    public String toString() {
        return "CompanyRegister [registerId=" + registerId + ", countryCode=" + countryCode
                + ", localizedRegisterName=" + localizedRegisterName
                + ", localizedRegisterNumberName=" + localizedRegisterNumberName + "]";
    }

}
