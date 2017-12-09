package com.example.currencymonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.currencymonitor.data.db.CurrencyDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SQLiteDatabase mDb;
    private CurrencyDBHelper dbHelper;
    private TextView last_update;
    private Adapter mAdapter;
    private RecyclerView recyclerView;
    private LinkedHashMap<String, HashMap<String, Double>> table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        table = new LinkedHashMap<>();
        assembly();
        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this, table.get("EUR"));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.mspinner);
        Spinner spinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    switch (position){
                        case 0:
                            mAdapter = new Adapter(MainActivity.this, table.get("EUR"));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                        case 1:
                            mAdapter = new Adapter(MainActivity.this, table.get("USD"));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                        case 2:
                            mAdapter = new Adapter(MainActivity.this, table.get("JPY"));
                            mAdapter.notifyDataSetChanged();
                            break;
                        case 3:
                            mAdapter = new Adapter(MainActivity.this, table.get("GBP"));
                            mAdapter.notifyDataSetChanged();
                            break;
                        case 4:
                            mAdapter = new Adapter(MainActivity.this, table.get("CHF"));
                            mAdapter.notifyDataSetChanged();
                            break;
                        case 5:
                            mAdapter = new Adapter(MainActivity.this, table.get("AUD"));
                            mAdapter.notifyDataSetChanged();
                            break;
                        case 6:
                            mAdapter = new Adapter(MainActivity.this, table.get("CAD"));
                            mAdapter.notifyDataSetChanged();
                            break;
                        case 7:
                            mAdapter = new Adapter(MainActivity.this, table.get("SEK"));
                            mAdapter.notifyDataSetChanged();
                            break;
                    }
                }
                /*Toast.makeText(MainActivity.this, "Selected",
                        Toast.LENGTH_SHORT).show();*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });

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

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        List<String> list;
        private TextView mTitleTextView;
        private ImageView mImageView;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.single_item_textview);
            mImageView = (ImageView) itemView.findViewById(R.id.single_item_imageview);
        }

//        public void bindCrime(List<String> keys) {
//            this.keys = keys;
//            mTitleTextView.setText("S");
//
//        }

        @Override
        public void onClick(View v) {

        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        private ArrayList keys;
        private ArrayList values;
        private Context mContext;

        public Adapter(Context context, HashMap<String, Double> hashMap) {
            mContext = context;
            keys = new ArrayList(hashMap.keySet());
            values = new ArrayList(hashMap.values());
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.single_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            Double val = (Double) values.get(position);
            holder.mTitleTextView.setText(String.valueOf(val));
        }

        @Override
        public int getItemCount() {
            return keys.size();
        }
    }

    private void assembly(){
        dbHelper = new CurrencyDBHelper(MainActivity.this);
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);
        for (int i = 0; i<=8 /*TODO: откуда?*/; i++){
            if (!cursor.moveToPosition(i))
                continue;
            String base = cursor.getString(cursor.getColumnIndex(CURRENCY));
            HashMap<String, Double> val = new HashMap<>();
            val.put("EUR", cursor.getDouble(cursor.getColumnIndex(COLUMN_EUR)));
            val.put("USD", cursor.getDouble(cursor.getColumnIndex(COLUMN_USD)));
            val.put("JPY", cursor.getDouble(cursor.getColumnIndex(COLUMN_JPY)));
            val.put("GBP", cursor.getDouble(cursor.getColumnIndex(COLUMN_GBP)));
            val.put("CHF", cursor.getDouble(cursor.getColumnIndex(COLUMN_CHF)));
            val.put("AUD", cursor.getDouble(cursor.getColumnIndex(COLUMN_AUD)));
            val.put("CAD", cursor.getDouble(cursor.getColumnIndex(COLUMN_CAD)));
            val.put("SEK", cursor.getDouble(cursor.getColumnIndex(COLUMN_SEK)));
            table.put(base, val);
        }
        mDb.close();
    }
}
