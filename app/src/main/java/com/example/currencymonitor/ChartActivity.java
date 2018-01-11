package com.example.currencymonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.currencymonitor.data.db.Flags;


/**
 * Created by l1maginaire on 1/7/18.
 */

public class ChartActivity extends AppCompatActivity {
    private static final String CURRENCY_ID = "ID";
    private static String test;

    public static Intent newIntent(Context packageContext, String base) {
        Intent intent = new Intent(packageContext, ChartActivity.class);
        intent.putExtra(CURRENCY_ID, base);
        test = base;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
