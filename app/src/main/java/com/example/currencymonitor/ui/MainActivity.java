package com.example.currencymonitor.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currencymonitor.R;
import com.example.currencymonitor.di.components.DaggerCurrencyComponent;
import com.example.currencymonitor.interfaces.FixerAPI;
import com.example.currencymonitor.data.Rates;
import com.example.currencymonitor.data.db.CurrencyDBHelper;
import com.example.currencymonitor.data.Flags;
import com.example.currencymonitor.di.components.CurrencyComponent;
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
import com.example.currencymonitor.utils.TextWatcherWrapper;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String KEY_INDEX = "index";
    private static final DecimalFormat df = new DecimalFormat("#.####");

    private SQLiteDatabase database;
    private CurrencyDBHelper dbHelper;
    private TextView last_update;
    private Adapter adapter;
    private RecyclerView recyclerView;
    private EditText entryfield;
    private ArrayList<CurrencyData> dataList;
    private FixerAPI fixerAPI;
    private GregorianCalendar lastUpdateTime;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private boolean hideEU = true;
    private boolean hideUS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//todo: where?
        getSupportActionBar().hide();
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        dataList = new ArrayList<>();

//        if (savedInstanceState != null) {
//            dataList = (ArrayList<CurrencyData>) savedInstanceState.getSerializable(KEY_INDEX);
//        }

        setContentView(R.layout.activity_main);
        last_update = findViewById(R.id.last_update_date);

        dbHelper = new CurrencyDBHelper(MainActivity.this);
        database = dbHelper.getWritableDatabase();

        if (dbExists(database))
            database.delete(TABLE_NAME, null, null);
        if (!isOnline()) {
            Toast.makeText(this, "Please, check your Internet connection.", Toast.LENGTH_LONG).show(); //todo: broadcast
            this.finish(); //todo: broadcastreciever, RX
            return;
        }

        CurrencyComponent daggerRandomUserComponent = DaggerCurrencyComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
        fixerAPI = daggerRandomUserComponent.getCurrencyService();
        requestRX("EUR");

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


//        abc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adapter.notifyDataSetChanged();
//            }
//        });

        entryfield = findViewById(R.id.entryfield);
        entryfield.setRawInputType(Configuration.KEYBOARD_12KEY);
        entryfield.addTextChangedListener(new TextWatcherWrapper() {
            @Override
            public void afterTextChanged(Editable s) {
                float coef;
                try {
                    coef = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                    coef = 0f;
                }
                for (CurrencyData c : dataList) {
                    c.setValue(Float.valueOf(df.format(c.getPrimaryRate() * coef)));
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        });
        setupSharedPreferences();
        setupAdapter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_INDEX, dataList);
    }

    private void setupAdapter() {
        adapter = new Adapter(this, dataList);
        recyclerView.setAdapter(adapter);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) { //todo wtf with lifecycle?
        if (key.equals(getString(R.string.pref_show_eur))) {
            hideEU = sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_eur));
            dataList.remove(1);
//            mVisualizerView.setShowBass(sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_eur)));
        } else if (key.equals(getString(R.string.pref_show_usd))) {
            dataList.remove(2);
        } else if (key.equals(getString(R.string.pref_show_jpy))) {
        } else if (key.equals(getString(R.string.pref_show_gbp))) {
        } else if (key.equals(getString(R.string.pref_show_chf))) {
        } else if (key.equals(getString(R.string.pref_show_aud))) {
        } else if (key.equals(getString(R.string.pref_show_cad))) {
        } else if (key.equals(getString(R.string.pref_show_sek))) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void bindData() {
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        } else {
            Flags[] flags = Flags.values();
            for (int i = 1; i < cursor.getColumnCount(); i++) {
                CurrencyData data = new CurrencyData();
                float var = cursor.getFloat(i);
                if (var == 0.0)
                    continue;
                data.setPrimaryRate(var);
                data.setValue(var);
                data.setPic(flags[i - 1].getValue());
                data.setTag(flags[i - 1].toString());
                dataList.add(data);
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
        compositeDisposable.add(fixerAPI.getData(currecy)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(data -> data.getRates())
                        .subscribe(rates -> {
                            dBinsert(rates, currecy);
                            last_update.setText("Last update: " + android.text.format.DateFormat.format("dd-yyyy-MM hh:mm",
                                    lastUpdateTime));
//                        setupSharedPreferences();
                            bindData();
                            setupAdapter();
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
        long l = database.insert(TABLE_NAME, null, cv);
        lastUpdateTime = new GregorianCalendar();
        Log.d(TAG, "dBinsert: " + String.valueOf(l));
    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mVisualizerView.setShowBass(sharedPreferences.getBoolean(getString(R.string.pref_show_bass_key),
//                getResources().getBoolean(R.bool.pref_show_bass_default)));
//        mVisualizerView.setShowMid(sharedPreferences.getBoolean(getString(R.string.pref_show_mid_range_key),
//                getResources().getBoolean(R.bool.pref_show_mid_range_default)));
//        mVisualizerView.setShowTreble(sharedPreferences.getBoolean(getString(R.string.pref_show_treble_key),
//                getResources().getBoolean(R.bool.pref_show_treble_default)));
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }
}