package com.example.currencymonitor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.currencymonitor.data.FixerAPI;
import com.example.currencymonitor.data.MetaCurr;
import com.example.currencymonitor.data.Rates;
import com.example.currencymonitor.data.db.CurrencyDBHelper;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_AUD;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_CAD;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_CHF;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_EUR;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_GBP;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_JPY;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_SEK;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_USD;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.CURRENCY;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.TABLE_NAME;

public class SplashActivity extends AppCompatActivity {
    public static long mLastUpdateTime;
    private final static String TAG = MainActivity.class.getSimpleName();
    private SQLiteDatabase mDb;
    private CurrencyDBHelper dbHelper;
    private int counter = 0;
    private FixerAPI fixerAPI;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new CurrencyDBHelper(SplashActivity.this);
        mDb = dbHelper.getWritableDatabase();
        if (dbExists(mDb))
            mDb.delete(TABLE_NAME, null, null);
        if (!isOnline()) {
            Toast.makeText(this, "Check you Internet connection!", Toast.LENGTH_LONG).show(); //todo: broadcast
            return;
        }
        fixerAPI = App.getApi();
//        String[] sequence = new String[]{"EUR", "USD", "JPY", "GBP", "CHF", "AUD", "CAD", "SEK"}; //todo mainthread
//        for (String s:sequence) {
            requestRX("EUR");
//        }
//        startActivity(new Intent(SplashActivity.this, MainActivity.class));

    }

    private void requestRX(final String currecy) {
        mCompositeDisposable.add(fixerAPI.getData(currecy)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<MetaCurr, Rates>() {
                    @Override
                    public Rates apply(
                            @NonNull final MetaCurr data)
                            throws Exception {
                        return data.getRates();
                    }
                })
                .subscribe(new Consumer<Rates>() {
                    @Override
                    public void accept(
                            @NonNull final Rates rates)
                            throws Exception {
                        dBinsert(rates, currecy);
                    }
                })
        );
    }

    public boolean dbExists(SQLiteDatabase mDb) {
        Cursor cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }


    private void dBinsert(Rates rates, String base) {
        ContentValues cv = new ContentValues();
        cv.put(CURRENCY, base);
        cv.put(COLUMN_EUR, rates.getEUR());
        cv.put(COLUMN_USD, rates.getUSD());
        cv.put(COLUMN_JPY, rates.getJPY());
        cv.put(COLUMN_GBP, rates.getGBP());
        cv.put(COLUMN_CHF, rates.getCHF());
        cv.put(COLUMN_AUD, rates.getAUD());
        cv.put(COLUMN_CAD, rates.getCAD());
        cv.put(COLUMN_SEK, rates.getSEK());
        long l = mDb.insert(TABLE_NAME, null, cv);
        Log.d(TAG, "dBinsert: "+String.valueOf(l));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
