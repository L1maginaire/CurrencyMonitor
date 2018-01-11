package com.example.currencymonitor.utils;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by l1maginaire on 1/11/18.
 */

public class OnItemSelectedListenerImplementation implements AdapterView.OnItemSelectedListener{
    private Context mContext;

    public OnItemSelectedListenerImplementation(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        Toast.makeText(mContext, "Position = " + position, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0){}
}
