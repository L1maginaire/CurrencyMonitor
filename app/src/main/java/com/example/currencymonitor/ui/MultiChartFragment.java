package com.example.currencymonitor.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.currencymonitor.data.MetaCurrency;
import com.example.currencymonitor.data.Pair;
import com.example.currencymonitor.R;
import com.example.currencymonitor.di.components.DaggerCurrencyComponent;
import com.example.currencymonitor.interfaces.FixerAPI;
import com.example.currencymonitor.data.Flags;
import com.example.currencymonitor.di.components.CurrencyComponent;
import com.example.currencymonitor.di.modules.ContextModule;
import com.example.currencymonitor.utils.CustomSpinnerAdapter;
import com.example.currencymonitor.utils.DaysSequence;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.support.v4.app.Fragment;
import android.widget.Spinner;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * Created by l1maginaire on 1/6/18.
 */

public class MultiChartFragment extends Fragment {
    private ArrayList<Float> floats;
    private CompositeDisposable compositeDisposable;
    private List<Flags> currencies = Arrays.asList(Flags.values());
    private BarChart barChart;
    private FixerAPI fixerAPI;
    private DaysSequence daysSequence = new DaysSequence();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chart, container, false);
 //       setRetainInstance(true); // to prevent hiding on changing orientation

        barChart = (BarChart) v.findViewById(R.id.chartView);
        barChart.getDescription().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.marker_view);
        mv.setChartView(barChart);
        barChart.setMarker(mv);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(getResources().getColor(R.color.textbright));
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.textbright));
        xAxis.setValueFormatter((value, axis) -> daysSequence.getTableDates().get((int)value));

        Spinner spinnerF = (Spinner) v.findViewById(R.id.spinnerfrom);
        Spinner spinnerW = (Spinner) v.findViewById(R.id.spinnerto);
        ArrayAdapter<Flags> adapter = new CustomSpinnerAdapter(getContext(), R.layout.row, currencies);

        spinnerF.setAdapter(adapter);
        spinnerW.setAdapter(adapter);
        spinnerW.setSelection(1);

        CurrencyComponent daggerRandomUserComponent = DaggerCurrencyComponent.builder()
                .contextModule(new ContextModule(getContext()))
                .build();
        fixerAPI = daggerRandomUserComponent.getCurrencyService();

        rx.Observable<Integer> obs1 = RxAdapterView.itemSelections(spinnerF);
        rx.Observable<Integer> obs2 = RxAdapterView.itemSelections(spinnerW);
        rx.Observable.combineLatest(obs1, obs2, (intFrom, intWhere) -> {
            Pair pair = new Pair(intFrom, intWhere);
            return pair;
        })
                .subscribeOn(mainThread())
                .subscribe(pair -> {
                    Log.v("spinner", pair.getFrom().toString());
                    Log.v("spinner", pair.getWhere().toString());
                    requestStatistics(pair.getFrom(), pair.getWhere());
                });

        return v;
    }

    private BarData generateData(ArrayList<Float> list) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            entries.add(new BarEntry(i, list.get(i)));
        }

        BarDataSet d = new BarDataSet(entries, "Dates");
        d.setColor(getResources().getColor(R.color.barcolor));
        d.setValueTextColor(getResources().getColor(R.color.textbright));
        d.setValueTextSize(12f);
        d.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> new DecimalFormat("#.###").format(value));
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(d);

        BarData cd = new BarData(sets);
        cd.setBarWidth(0.9f);
        return cd;
    }

    private void requestStatistics(final int from, final int where) {
        if (from == where){
            barChart.setData(null);
            barChart.invalidate();
            return;
        }
        String base = currencies.get(from).toString();
        ArrayList<Single<MetaCurrency>> dataList = daysSequence(base);
        ArrayList <MetaCurrency> emptyList = new ArrayList<>();
        Single<List<MetaCurrency>> n = Single.merge(dataList).buffer(Integer.MAX_VALUE).single(emptyList); // todo: single?
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(n
                .subscribeOn(Schedulers.io())
//                .filter(new Predicate<List<MetaCurrency>>() {
//                    @Override
//                    public boolean test(List<MetaCurrency> metaCurrs) throws Exception {
//                        return metaCurrs.get();
//                    }
//                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    floats = new ArrayList<>();
                    for (MetaCurrency m:list) {
                        floats.add(adaptFunction(where, m));
                    }
                    barChart.setData(generateData(floats));
                    barChart.notifyDataSetChanged();
                    barChart.invalidate();
                })
        );
    }

    private ArrayList<Single<MetaCurrency>> daysSequence(String base) {
        ArrayList<Single<MetaCurrency>> meta = new ArrayList<>();
        for (int i = 0; i < daysSequence.getRetrofitDates().size(); i++) {
            meta.add(fixerAPI.statistics(daysSequence.getRetrofitDates().get(i), base));
        }
        return meta;
    }
    float adaptFunction(int x, MetaCurrency m){
        switch (x){
            case 0:
                return m.getRates().getEUR();
            case 1:
                return m.getRates().getUSD();
            case 2:
                return m.getRates().getJPY();
            case 3:
                return m.getRates().getGBP();
            case 4:
                return m.getRates().getCHF();
            case 5:
                return m.getRates().getAUD();
            case 6:
                return m.getRates().getCAD();
            case 7:
                return m.getRates().getSEK();
            default:
                return 0f;
        }
    }
}
