package com.example.currencymonitor.utils;

import android.database.Cursor;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currencymonitor.data.CurrencyData;
import com.example.currencymonitor.R;

import java.util.ArrayList;

/**
 * Created by l1maginaire on 12/28/17.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
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

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
}