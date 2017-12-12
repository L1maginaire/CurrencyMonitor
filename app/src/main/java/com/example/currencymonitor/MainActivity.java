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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currencymonitor.data.db.CurrencyDBHelper;

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
        last_update = findViewById(R.id.last_update_date);
        myImageList = new int[]{R.drawable.european_union, R.drawable.united_states, R.drawable.japan, R.drawable.united_kingdom, R.drawable.switzerland,
                R.drawable.australia, R.drawable.canada, R.drawable.sweden};
        dbHelper = new CurrencyDBHelper(MainActivity.this);
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{"eur", "usd", "jpy", "gbp", "chf", "aud", "cad", "sek"}, "_id=?", new String[]{"1"}, null, null, null);
        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this, cursor);
        recyclerView.setAdapter(mAdapter);
        last_update.setText("Last update: " + android.text.format.DateFormat.format("dd-yyyy-MM hh:mm", SplashActivity.mLastUpdateTime));
        last_update.requestFocus();
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

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mEditText;
        private ImageView mImageView;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mEditText = (TextView) itemView.findViewById(R.id.single_item_edittext);
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

        public void setData(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.single_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            if (!mCursor.moveToFirst())
                return;
            double val = (Double) mCursor.getDouble(position);
            if (val == 0.0) {
                val = 1.0;
            }
            holder.mEditText.setText(String.valueOf(val));
            holder.mImageView.setImageResource(myImageList[position]);
        }

        @Override
        public int getItemCount() {
            return 7;
        }
    }
}
