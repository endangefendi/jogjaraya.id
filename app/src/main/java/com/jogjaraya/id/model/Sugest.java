package com.jogjaraya.id.model;

public class Sugest {

    private String suggestion, value;

    public Sugest(String suggestion, String value) {
        this.suggestion = suggestion;
        this.value = value;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}