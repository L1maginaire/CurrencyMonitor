package com.example.currencymonitor.data;

/**
 * Created by l1maginaire on 1/12/18.
 */

public class Pair {
    public final Integer from;
    public final Integer where;
    public Pair(Integer from, Integer where) {
        this.from = from;
        this.where = where;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getWhere() {
        return where;
    }
}