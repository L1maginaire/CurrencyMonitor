package com.example.currencymonitor;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.currencymonitor.data.MetaCurr;
import com.example.currencymonitor.data.db.CurrencyDBHelper;

import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_AUD;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.COLUMN_DATE;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.CURRENCY;
import static com.example.currencymonitor.data.db.CurrencyContract.Entry.TABLE_NAME;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = /*MainActivity.class.getSimpleName()*/"abc";
    private SQLiteDatabase mDb;
    CurrencyDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        extraction("USD");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //TODO: https://developer.android.com/training/appbar/setting-up.html
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    void extraction(String query) {
        App.getApi().getData(query).enqueue(new Callback<MetaCurr>() {
            @Override
            public void onResponse(Call<MetaCurr> call, Response<MetaCurr> response) {
                if (response.isSuccessful() || response.body() != null) {
                    dbHelper = new CurrencyDBHelper(MainActivity.this);
                    mDb = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(CURRENCY, response.body().getBase());
                    cv.put(COLUMN_AUD, response.body().getRates().g);
                    long f = mDb.insert(TABLE_NAME, null, cv);
                    Toast.makeText(MainActivity.this, "sucess", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, getS());

                } else {
                    try {
                        Log.d(TAG, response.body().getBase().toString());
                        Toast.makeText(MainActivity.this, ":(", Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MetaCurr> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getS() {
        String s = null;
        Cursor cursor = mDb.query(TABLE_NAME, null,null, null, null, null, null);
        boolean abc = cursor.moveToFirst(); // <-- First call
        s = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
        /*
        if (cursor != null)
        {
            if(cursor.moveToFirst())
            {
                s = (cursor.getString(1));
            }
        }
        */mDb.close();
        // return user
        return s;

    }
}
