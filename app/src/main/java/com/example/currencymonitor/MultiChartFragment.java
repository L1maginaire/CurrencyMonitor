package com.example.currencymonitor;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.currencymonitor.data.FixerAPI;
import com.example.currencymonitor.data.MetaCurr;
import com.example.currencymonitor.data.db.Flags;
import com.example.currencymonitor.di.components.CurrencyComponent;
import com.example.currencymonitor.di.components.DaggerCurrencyComponent;
import com.example.currencymonitor.di.modules.ContextModule;
import com.example.currencymonitor.utils.CustomSpinnerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.support.v4.app.Fragment;
import android.widget.Spinner;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by l1maginaire on 1/6/18.
 */

public class MultiChartFragment extends Fragment {
    ArrayList<BarData> barData = new ArrayList<>();
    ArrayList<Float> floats;
    CompositeDisposable mCompositeDisposable;
    List<Flags> currencies = Arrays.asList(Flags.values());
    BarChart mChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.listview_chart, container, false);


        Spinner spinnerF = (Spinner) v.findViewById(R.id.from);
        Spinner spinnerW = (Spinner) v.findViewById(R.id.where);
        ArrayAdapter<Flags> adapter = new CustomSpinnerAdapter(getContext(), R.layout.row, currencies);



        spinnerW.setAdapter(adapter);
        spinnerF.setAdapter(adapter);

        rx.Observable<Integer> obs1 = RxAdapterView.itemSelections(spinnerF).skip(1);
        rx.Observable<Integer> obs2 = RxAdapterView.itemSelections(spinnerW).skip(1); //todo
        rx.Observable.combineLatest(obs1, obs2, (s, s2) -> {
            com.example.currencymonitor.data.db.Pair pair = new com.example.currencymonitor.data.db.Pair(s, s2);
            return pair;
        })
                .subscribeOn(mainThread())
                .subscribe(pair -> {
                    Log.v("spinner", pair.getX().toString());
                    Log.v("spinner", pair.getY().toString());
                });
        daysSequence("USD");
        ChartDataAdapter cda = new ChartDataAdapter(getContext(), barData);
        ListView lv = (ListView) v.findViewById(R.id.chartView);
        lv.setAdapter(cda);
        return v;
    }

    private class ChartDataAdapter extends ArrayAdapter<BarData> {

        public ChartDataAdapter(Context context, List<BarData> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BarData data = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);
                MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.marker_view);
                mv.setChartView(holder.chart);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            data.setValueTextColor(Color.BLACK);
            holder.chart.getDescription().setEnabled(false);
            holder.chart.setDrawGridBackground(false);

            XAxis xAxis = holder.chart.getXAxis();
            xAxis.setPosition(XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setLabelCount(5, false);
            rightAxis.setSpaceTop(15f);

            holder.chart.setData(data);
            holder.chart.setFitBars(true);
            holder.chart.animateY(700);

            Legend legend = holder.chart.getLegend();
            legend.setEnabled(false);

            return convertView;
        }

        private class ViewHolder {
            BarChart chart;
        }
    }

    private BarData generateData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < floats.size(); i++) {
            entries.add(new BarEntry(i, floats.get(i)));
        }

        BarDataSet d = new BarDataSet(entries, "Dates");
        d.setColor(Color.GRAY);
        d.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> new DecimalFormat("#.##").format(value));
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(d);

        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

    private void daysSequence(String base) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<Single<MetaCurr>> dataList = new ArrayList();
        Calendar calendar = new GregorianCalendar();

        CurrencyComponent daggerRandomUserComponent = DaggerCurrencyComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .build();
        FixerAPI fixerAPI = daggerRandomUserComponent.getCurrencyService();

        for (int i = 0; i < 10; i++) {
            Date result = calendar.getTime();
            dataList.add(fixerAPI.statistics(sdf.format(result), base));
            calendar.add(Calendar.DATE, -1);
        }

        ArrayList <MetaCurr> emptyList = new ArrayList<>();

        Single<List<MetaCurr>> n = Single.merge(dataList).buffer(Integer.MAX_VALUE).single(emptyList);

        mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(n
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    floats = new ArrayList<>();

                    for (MetaCurr m:list) {
                        float f = m.getRates().getAUD();
                        floats.add(f);
                    }
                    barData.add(generateData());
                })
        );
    }
}
