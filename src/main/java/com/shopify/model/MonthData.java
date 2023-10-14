package com.shopify.model;

public class MonthData {
    private String monthName;
    private int monthNumber;

    public MonthData(String monthName, int monthNumber) {
        this.monthName = monthName;
        this.monthNumber = monthNumber;
    }

    public String getMonthName() {
        return monthName;
    }

    public int getMonthNumber() {
        return monthNumber;
    }
}

