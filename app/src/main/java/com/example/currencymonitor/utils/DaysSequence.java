package com.example.currencymonitor.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by l1maginaire on 2/10/18.
 */

public class DaysSequence {
    private static final SimpleDateFormat dateFormatWide = new SimpleDateFormat("yyyy-MM-dd"); //todo объект
    private static final SimpleDateFormat dateFormatNarr = new SimpleDateFormat("dd/MM");

    private ArrayList<String> tableDates = new ArrayList<>();
    private ArrayList<String> retrofitDates = new ArrayList<>();


    public DaysSequence() {
        Calendar calendar = new GregorianCalendar();

        for (int i = 0; i < 8; i++) {
            Date result = calendar.getTime();
            tableDates.add(dateFormatNarr.format(result));
            retrofitDates.add(dateFormatWide.format(result));
            calendar.add(Calendar.DATE, -1);
        }
        Collections.reverse(tableDates);
        Collections.reverse(retrofitDates);
    }

    public ArrayList<String> getTableDates() {
        return tableDates;
    }

    public ArrayList<String> getRetrofitDates() {
        return retrofitDates;
    }
}
