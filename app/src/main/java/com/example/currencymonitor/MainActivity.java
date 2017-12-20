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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currencymonitor.data.db.CurrencyDBHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.example.currencymonitor.data.db.CurrencyContract.Entry.TABLE_NAME;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SQLiteDatabase mDb;
    private CurrencyDBHelper dbHelper;
    private TextView last_update;
    private Adapter mAdapter;
    private RecyclerView recyclerView;
    private EditText entryfield;
    private ArrayList<CurrencyData> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        last_update = findViewById(R.id.last_update_date);
        dbHelper = new CurrencyDBHelper(MainActivity.this);
        mDb = dbHelper.getReadableDatabase();
        Cursor cursor = mDb.query(TABLE_NAME, null, null, null, null, null, null);
        list = new ArrayList<>();
        bindData(cursor);
        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter(this, list);
        recyclerView.setAdapter(mAdapter);
        last_update.setText("Last update: " + android.text.format.DateFormat.format("dd-yyyy-MM hh:mm", SplashActivity.mLastUpdateTime));
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
                double coef = Double.valueOf(s.toString());
                for (CurrencyData c : list) {
                    c.setValue(coef);
                }
                entryfield.clearFocus();//todo: ?
                mAdapter.notifyDataSetChanged();
            }
        });
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
        private TextView textView;
        private ImageView mImageView;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textView = (TextView) itemView.findViewById(R.id.result);
            mImageView = (ImageView) itemView.findViewById(R.id.single_item_imageview);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        private Context mContext;
        ArrayList<CurrencyData> list;

        public Adapter(Context context, ArrayList<CurrencyData> list) {
            mContext = context;
            this.list = list;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.single_item, parent, false);
            return new Holder(view);
        }


        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            if (list == null || list.size() == 0)
                return;
            double val = (Double) list.get(position).getValue();
            int res = (Integer) list.get(position).getPic();
            holder.textView.setText(String.valueOf(val));
            holder.mImageView.setImageResource(res);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private void bindData(Cursor c) {
        if (c == null || !c.moveToFirst()) {
            return;
        } else {
            int[] myImageList = new int[]{R.drawable.european_union, R.drawable.united_states, R.drawable.japan, R.drawable.united_kingdom, R.drawable.switzerland,
                    R.drawable.australia, R.drawable.canada, R.drawable.sweden};

            for (int i = 0; i < myImageList.length
                    ; i++) {
                CurrencyData data = new CurrencyData();
                data.setCoefficient((c.getDouble(i)) == 0.0 ? 1.0 : c.getDouble(i));
                data.setValue(c.getDouble(i) * 1/*//todo*/);
                data.setPic(myImageList[i]);
                list.add(data);
            }
        }
        return;
    }
}