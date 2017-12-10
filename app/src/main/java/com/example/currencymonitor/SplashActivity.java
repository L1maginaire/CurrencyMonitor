package com.example.currencymonitor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.currencymonitor.data.MetaCurr;
import com.example.currencymonitor.data.db.CurrencyDBHelper;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_AUD;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_CAD;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_CHF;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_DATE;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_EUR;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_GBP;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_JPY;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_SEK;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_USD;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.CURRENCY;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.TABLE_NAME;

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = /*MainActivity.class.getSimpleName()*/"abc";
    private SQLiteDatabase mDb;
    private CurrencyDBHelper dbHelper;
    private int counter = 0;
    private static String[] sequence;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new CurrencyDBHelper(SplashActivity.this);
        mDb = dbHelper.getWritableDatabase();
        sequence = new String[]{"USD", "JPY", "GBP", "CHF", "AUD", "CAD", "SEK"};
        extraction("EUR");
        //TODO: prevention of middle-crack
    }

    void extraction(String query) {
        App.getApi().getData(query).enqueue(new Callback<MetaCurr>() {
            @Override
            public void onResponse(Call<MetaCurr> call, Response<MetaCurr> response) {
                if (response.isSuccessful() || response.body() != null) {
                    insert(response.body());
                    if (counter == 8) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        extraction(sequence[counter - 1]);
                    }
                } else {
                    //TODO: error_cases
                    try {
                        Log.d(TAG, response.body().getBase().toString());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MetaCurr> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(SplashActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insert(MetaCurr meta) {
        ContentValues cv = new ContentValues();
        cv.put(CURRENCY, meta.getBase());
        cv.put(COLUMN_DATE, System.currentTimeMillis());
        cv.put(COLUMN_EUR, meta.getRates().getEUR());
        cv.put(COLUMN_USD, meta.getRates().getUSD());
        cv.put(COLUMN_JPY, meta.getRates().getJPY());
        cv.put(COLUMN_GBP, meta.getRates().getGBP());
        cv.put(COLUMN_CHF, meta.getRates().getCHF());
        cv.put(COLUMN_AUD, meta.getRates().getAUD());
        cv.put(COLUMN_CAD, meta.getRates().getCAD());
        cv.put(COLUMN_SEK, meta.getRates().getSEK());
        long l = mDb.insert(TABLE_NAME, null, cv);//TODO: close?
        if (l >= 0)
            counter++;
    }
}
