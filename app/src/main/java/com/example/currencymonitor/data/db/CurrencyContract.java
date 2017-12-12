package com.example.currencymonitor.data.db;

import android.provider.BaseColumns;

public class CurrencyContract {
    public static final class Entry implements BaseColumns {
        public static final String TABLE_NAME = "rates";
        public static final String CURRENCY= "curr";
        public static final String COLUMN_EUR = "eur";
        public static final String COLUMN_USD = "usd";
        public static final String COLUMN_JPY = "jpy";
        public static final String COLUMN_GBP = "gbp";
        public static final String COLUMN_CHF = "chf";
        public static final String COLUMN_AUD = "aud";
        public static final String COLUMN_CAD = "cad";
        public static final String COLUMN_SEK = "sek";
    }
}