package com.example.currencymonitor.data.db;

/**
 * Created by l1maginaire on 1/12/18.
 */

public class Pair {
    public final Integer x;
    public final Integer y;
    public Pair(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }
}