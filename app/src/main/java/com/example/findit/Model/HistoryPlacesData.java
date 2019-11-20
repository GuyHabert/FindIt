package com.example.findit.Model;

import java.util.ArrayList;

public class HistoryPlacesData {
    public ArrayList<PlacesData> getPlacesData() {
        return placesData;
    }

    public void setPlacesData(ArrayList<PlacesData> placesData) {
        this.placesData = placesData;
    }

    ArrayList<PlacesData> placesData = new ArrayList<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String date;

}
