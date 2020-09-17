package com.example.velveteyebrows.providers;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

public class DecimalPriceFormatProvider {

    private static DecimalFormat _decimalFormat;

    public static DecimalFormat getDefaultDecimalFormat(){
        if(_decimalFormat == null){
            _decimalFormat = new DecimalFormat();
            _decimalFormat.setMinimumFractionDigits(0);
            _decimalFormat.setMaximumFractionDigits(2);
            _decimalFormat.setCurrency(Currency.getInstance(Locale.getDefault()));
        }

        return _decimalFormat;
    }

}
