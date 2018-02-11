package com.example.currencymonitor.utils;

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
    private Context context;
    private ArrayList<CurrencyData> dataList;

    public Adapter(Context context, ArrayList<CurrencyData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclers_single_item, parent, false);
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        if (dataList == null || dataList.size() == 0)
            return;
        float val = (float) dataList.get(position).getValue();
        int res = (int) dataList.get(position).getPic();
        String tag = (String) dataList.get(position).getTag();
        holder.textView.setText(String.valueOf(val));
        holder.mImageView.setImageResource(res);
        holder.mImageView.setTag(tag);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
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
//            Intent intent = ChartActivity.newIntent(context, (String) mImageView.getTag());
//            context.startActivity(intent);
//            context.startActivity(ChartActivity.newIntent(context, mImageView.getId()));
        }
    }
}