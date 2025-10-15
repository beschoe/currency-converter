/*
 * Created on 25.06.2010
 *
 * author Sandra.Bsiri
 */
package com.mercateo.common.i18n;

import com.mercateo.common.util.annotations.NonNullByDefault;

@NonNullByDefault
public enum MercateoEmailType {
    SERVICE, SUPPLIER, JOBS, INVESTOR, OPERATIONS, CATALOG, SERVICE_UNITE;

    public final String getEmailPrefix() {
        return this.name().toLowerCase();
    }
}
