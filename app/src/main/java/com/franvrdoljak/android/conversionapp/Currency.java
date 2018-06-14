package com.franvrdoljak.android.conversionapp;

/**
 * Created by Fran Vrdoljak on 6/14/2018.
 */

public class Currency {
    private String currency_code;
    private int unit_value;
    private Double buyin_rate;
    private Double median_rate;
    private Double selling_rate;

    public Currency(String currency_code, int unit_value, Double buyin_rate, Double median_rate, Double selling_rate) {
        this.currency_code = currency_code;
        this.unit_value = unit_value;
        this.buyin_rate = buyin_rate;
        this.median_rate = median_rate;
        this.selling_rate = selling_rate;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public int getUnit_value() {
        return unit_value;
    }

    public Double getBuyin_rate() {
        return buyin_rate;
    }

    public Double getMedian_rate() {
        return median_rate;
    }

    public Double getSelling_rate() {
        return selling_rate;
    }
}
