package com.example.currencymonitor.data;

public class CurrencyData implements java.io.Serializable{
    private int pic;
    private float primaryRate;
    private float value;
    private String tag;

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public float getPrimaryRate() {
        return primaryRate;
    }

    public void setPrimaryRate(float primaryRate) {
        this.primaryRate = primaryRate;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
