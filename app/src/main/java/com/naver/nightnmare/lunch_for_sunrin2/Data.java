package com.naver.nightnmare.lunch_for_sunrin2;

import com.google.gson.JsonArray;

public class Data {
    private String date;
    private JsonArray breakfast;
    private JsonArray lunch;
    private JsonArray dinner;

    public Data(String date, JsonArray breakfast, JsonArray lunch, JsonArray dinner) {
        this.date = date;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public JsonArray getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(JsonArray breakfast) {
        this.breakfast = breakfast;
    }

    public JsonArray getLunch() {
        return lunch;
    }

    public void setLunch(JsonArray lunch) {
        this.lunch = lunch;
    }

    public JsonArray getDinner() {
        return dinner;
    }

    public void setDinner(JsonArray dinner) {
        this.dinner = dinner;
    }
}
