package com.example.currencymonitor.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.currencymonitor.data.db.CurrencyContract.Entry.TABLE_NAME;

public class CurrencyDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cmonitor.db";
    private static final int DATABASE_VERSION = 1;

    public CurrencyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_WEATHER_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        CurrencyContract.Entry.CURRENCY         + " STRING NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_EUR       + " DOUBLE NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_USD       + " DOUBLE NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_JPY       + " DOUBLE NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_GBP       + " DOUBLE NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_CHF       + " DOUBLE NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_AUD       + " DOUBLE NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_CAD       + " DOUBLE NOT NULL, " +
                        CurrencyContract.Entry.COLUMN_SEK       + " DOUBLE NOT NULL" +
                        "); ";
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}