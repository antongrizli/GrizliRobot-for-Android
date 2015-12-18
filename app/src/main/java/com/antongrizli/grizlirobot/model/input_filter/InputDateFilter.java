package com.antongrizli.grizlirobot.model.input_filter;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import com.antongrizli.grizlirobot.exception.IncorrectDate;

import java.util.Calendar;

/**
 * Created by Антон on 24.11.2015.
 */
public class InputDateFilter implements InputFilter {
    private int day = 0;
    private int month = 0;
    private int year = 0;
    private int current_year = Calendar.getInstance().get(Calendar.YEAR);
    /*
    Необходимо фильтровать  только 8 символов т.к. дд.мм.гггг
    сравнивать первые два символа на диапазон от 01 до 31
    втрорые два числа от 01 до 12
    и остальные четыре числа от 2000 до текущий год
     */
    private static final String SEARCH_TAB = "search";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Log.d(SEARCH_TAB, "Source=" + source + " dest=" + dest);
        StringBuilder newText = new StringBuilder(dest).replace(dstart, dend, source.toString());
        Log.d(SEARCH_TAB, "newText=" + newText);
        if (newText.length() < 9) {
            if (newText.length() == 2) {
                day = Integer.parseInt(newText.substring(0, 2));
                if (day >= 1 && day <= 31) {
                    return null;
                } else {
                    return "";
                }
            }
            if (newText.length() == 4) {
                month = Integer.parseInt(newText.substring(2, 4));
                if (month >=1 && month <= 12 && day<=dayPerMonth(month)){
                    return null;
                }
                return "";
            }
            if (newText.length() == 8) {
                year = Integer.parseInt(newText.substring(4, 8));
                Log.d(SEARCH_TAB, "year=" + year);
                if (month == 2 && day <= 29 && leapYear(year)) {
                    return null;
                }
                if (year >= 2000 && year <= current_year) {
                    return null;
                } else return "";
            }
            return null;
        } else return "";


    }

    private boolean leapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    private int dayPerMonth(int month) {
        return (int) (28 + (month + Math.floor(month / 8)) % 2 + 2 % month + 2 * Math.floor(1 / month));
    }
}
