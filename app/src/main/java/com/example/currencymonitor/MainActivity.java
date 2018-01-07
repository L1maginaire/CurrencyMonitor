package com.example.currencymonitor;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currencymonitor.data.FixerAPI;
import com.example.currencymonitor.data.MetaCurr;
import com.example.currencymonitor.data.Rates;
import com.example.currencymonitor.data.db.CurrencyDBHelper;
import com.example.currencymonitor.di.components.CurrencyComponent;
import com.example.currencymonitor.di.components.DaggerCurrencyComponent;
import com.example.currencymonitor.di.modules.ContextModule;
import com.example.currencymonitor.utils.Adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

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

import com.example.currencymonitor.data.CurrencyData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private SQLiteDatabase mDb;
    private CurrencyDBHelper dbHelper;
    private TextView last_update;
    private Adapter mAdapter;
    private RecyclerView recyclerView;
    private EditText entryfield;
    private ArrayList<CurrencyData> list;
    private FixerAPI fixerAPI;
    private GregorianCalendar mLastUpdateTime;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        last_update = findViewById(R.id.last_update_date);
        list = new ArrayList<>();

        dbHelper = new CurrencyDBHelper(MainActivity.this);
        mDb = dbHelper.getWritableDatabase();
        if (dbExists(mDb))
            mDb.delete(TABLE_NAME, null, null);
        if (!isOnline()) {
            Toast.makeText(this, "Please, check your Internet connection.", Toast.LENGTH_LONG).show(); //todo: broadcast
            this.finish();
            return;
        }

        CurrencyComponent daggerRandomUserComponent = DaggerCurrencyComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
        fixerAPI = daggerRandomUserComponent.getCurrencyService();
//        String[] sequence = new String[]{"EUR", "USD", "JPY", "GBP", "CHF", "AUD", "CAD", "SEK"}; //todo: rx
        requestRX("EUR");

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        abc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAdapter.notifyDataSetChanged();
//            }
//        });

        entryfield = findViewById(R.id.entryfield);
        entryfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (number == ""||number.isEmpty())
                    number = "0.0";
                float coef = Float.valueOf(number);
                for (CurrencyData c : list) {
                    DecimalFormat df = new DecimalFormat("#.####"); //todo?
                    float value = Float.valueOf(df.format(c.getPrimaryRate() * coef));
                    c.setValue(value);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupAdapter() {
        mAdapter = new Adapter(this, list);
        recyclerView.setAdapter(mAdapter);
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

    private void bindData() {
        Cursor cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        } else {
            int[] myImageList = new int[]{R.drawable.european_union, R.drawable.united_states, R.drawable.japan,
                    R.drawable.united_kingdom, R.drawable.switzerland, R.drawable.australia, R.drawable.canada, R.drawable.sweden};

            for (int i = 1; i < cursor.getColumnCount(); i++) {
                CurrencyData data = new CurrencyData();
                float var = cursor.getFloat(i);
                if (var == 0.0)
                    continue;
                data.setPrimaryRate(var);
                data.setValue(var);
                data.setPic(myImageList[i - 1]);
                list.add(data);
            }
        }
        return;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean dbExists(SQLiteDatabase mDb) {
        Cursor cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
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
                                last_update.setText("Last update: " + android.text.format.DateFormat.format("dd-yyyy-MM hh:mm",
                                        mLastUpdateTime));
//                        setupSharedPreferences();
                                bindData();
                                setupAdapter();
                            }
                        })
        );
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
        mLastUpdateTime = new GregorianCalendar();
        Log.d(TAG, "dBinsert: " + String.valueOf(l));
    }
}