package com.example.financemanager.classes;

import android.content.Context;
import android.content.SharedPreferences;

public class CurrencyManager {

    private static final String PREFS_NAME = "currency_prefs";
    private static final String KEY_SYMBOL = "selected_currency_symbol";
    private static final String KEY_RATE = "selected_currency_rate";

    private Context context;
    private SharedPreferences prefs;

    public String[] currencyNames = {"USD", "PKR", "EUR", "GBP", "JPY", "CAD", "AUD", "AED", "INR", "CNY"};
    public String[] currencySymbols = {"$", "₨", "€", "£", "¥", "C$", "A$", "د.إ", "₹", "¥"};
    public double[] conversionRates = {1.0, 280.0, 0.91, 0.79, 150.0, 1.34, 1.48, 3.67, 83.0, 7.2};

    public CurrencyManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getCurrentSymbol() {
        return prefs.getString(KEY_SYMBOL, "$");
    }

    public double getCurrentRate() {
        return prefs.getFloat(KEY_RATE, 1.0f);
    }

    public void saveCurrency(String symbol, double rate) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SYMBOL, symbol);
        editor.putFloat(KEY_RATE, (float) rate);
        editor.apply();
    }
}

