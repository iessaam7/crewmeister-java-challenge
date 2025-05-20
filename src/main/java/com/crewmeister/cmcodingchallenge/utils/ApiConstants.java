package com.crewmeister.cmcodingchallenge.utils;

/**
 * Contains all API-related constants grouped by functionality
 */
public final class ApiConstants {

    private ApiConstants() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static final class ExchangeRateAPI {
        public static final String BASE_URL = "https://api.statistiken.bundesbank.de/rest/data/BBEX3/D..EUR.BB.AC.000?detail=dataonly";
        // Column labels from the csv file
        public static final String QUOTE_CURRENCY_LABEL = "BBK_STD_CURRENCY";
        public static final String BASE_CURRENCY_LABEL = "BBK_ERX_PARTNER_CURRENCY";
        public static final String DATE_LABEL = "TIME_PERIOD";
        public static final String RATE_LABEL = "OBS_VALUE";
        public static final long DATA_REFRESH_RATE = 3600000;
        public static final String EURO_BASE_CURRENCY_CODE = "EUR";
    }
}

