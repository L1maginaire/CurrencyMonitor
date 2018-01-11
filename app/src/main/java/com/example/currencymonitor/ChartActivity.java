package com.example.currencymonitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.currencymonitor.data.db.Ids;

/**
 * Created by l1maginaire on 1/7/18.
 */

public class ChartActivity extends AppCompatActivity {
    private static final String CURRENCY_ID = "ID";
    private static int test;

    public static Intent newIntent(Context packageContext, int icon_id) {
        Intent intent = new Intent(packageContext, ChartActivity.class);
        intent.putExtra(CURRENCY_ID, icon_id);
        test = icon_id;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Integer data = getIntent().getIntExtra("DATA", 0);
        if (data== Ids.flags[2])
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
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
