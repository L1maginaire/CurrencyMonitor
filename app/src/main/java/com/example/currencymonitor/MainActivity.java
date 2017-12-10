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

import static android.provider.BaseColumns._ID;
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
    private int[] myImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myImageList = new int[]{R.drawable.european_union, R.drawable.united_states, R.drawable.japan, R.drawable.united_kingdom, R.drawable.switzerland,
        R.drawable.australia, R.drawable.canada, R.drawable.sweden};
        dbHelper = new CurrencyDBHelper(MainActivity.this);
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{"eur", "usd", "jpy", "gbp", "chf", "aud", "cad", "sek"}, "_id=?",new String[]{"2"},null,null,null);
//        Cursor cursor = mDb.query(TABLE_NAME, null, "_ID = 1", null, null, null, null);
        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this, cursor);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        /*MenuItem item = menu.findItem(R.id.mspinner);
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
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                        case 3:
                            mAdapter = new Adapter(MainActivity.this, table.get("GBP"));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                        case 4:
                            mAdapter = new Adapter(MainActivity.this, table.get("CHF"));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                        case 5:
                            mAdapter = new Adapter(MainActivity.this, table.get("AUD"));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                        case 6:
                            mAdapter = new Adapter(MainActivity.this, table.get("CAD"));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                        case 7:
                            mAdapter = new Adapter(MainActivity.this, table.get("SEK"));
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            Log.i("abc", String.valueOf(position));
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });*/

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
        private TextView mTitleTextView;
        private ImageView mImageView;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.single_item_textview);
            mImageView = (ImageView) itemView.findViewById(R.id.single_item_imageview);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        private Context mContext;
        Cursor mCursor;

        public Adapter(Context context, Cursor cursor) {
            mCursor = cursor;
            mContext = context;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.single_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            if (!mCursor.moveToFirst())
                return;
            double val = (Double) mCursor.getDouble(position);
            if(val == 0.0){
                //TODO: HIDE
            }
            holder.mTitleTextView.setText(String.valueOf(val));
            holder.mImageView.setImageResource(myImageList[position]);
        }

        @Override
        public int getItemCount() {
            return 8;
        }
    }
}
