package com.aspl.steel.pojo;

/**
 *  Created by Arnab Kar on 5/3/16.
 */
public class MonthPOJO {
    String spinnerText,value;
    public MonthPOJO(String spinnerText,String value) {
        this.spinnerText = spinnerText;
        this.value = value;
    }
    public String getSpinnerText() {
        return spinnerText;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return spinnerText;
    }
}
