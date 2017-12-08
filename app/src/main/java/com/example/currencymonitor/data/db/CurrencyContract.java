package com.example.currencymonitor.data.db;

import android.provider.BaseColumns;

public class CurrencyContract {
    public static final class Entry implements BaseColumns {
        public static final String TABLE_NAME = "rates";
        public static final String CURRENCY= "curr";
        public static final String COLUMN_JSON = "json";
        public static final String COLUMN_DATE = "date";


    }
}