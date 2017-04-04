package com.example.chen.ex9;

/**
 * Created by Chen on 2016/11/24.
 */

public class Indicator {
    private String name;
    private String value;

    public Indicator(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void SetValue(String value) {
        this.value = value;
    }
}
