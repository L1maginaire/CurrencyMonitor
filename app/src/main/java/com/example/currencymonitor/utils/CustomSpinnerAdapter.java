package com.example.currencymonitor.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currencymonitor.R;
import com.example.currencymonitor.data.Flags;

import java.util.List;

/**
 * Created by l1maginaire on 1/11/18.
 */

public class CustomSpinnerAdapter extends ArrayAdapter<Flags> {
    private Context mContext;
    List<Flags> flags;

    public CustomSpinnerAdapter(Context context, int textViewResourceId,
                                List<Flags> flags) {
        super(context, textViewResourceId, flags);
        mContext = context;
        this.flags = flags;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView,
                              ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row, parent, false);
        TextView label = (TextView) row.findViewById(R.id.currencyspinner);
        label.setText(flags.get(position).toString());
        ImageView icon = (ImageView) row.findViewById(R.id.icon);
        icon.setImageDrawable(icon.getResources().getDrawable(flags.get(position).getValue()));
        return row;
    }
}