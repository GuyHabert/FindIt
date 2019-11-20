package com.example.findit.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class PlacesData implements Parcelable {
    String placeName;
    LatLng placeLatLng;
    int count;

    public PlacesData(){

    }
    public PlacesData(Parcel in) {
        placeName = in.readString();
        placeLatLng = in.readParcelable(LatLng.class.getClassLoader());
        count = in.readInt();
    }

    public static final Creator<PlacesData> CREATOR = new Creator<PlacesData>() {
        @Override
        public PlacesData createFromParcel(Parcel in) {
            return new PlacesData(in);
        }

        @Override
        public PlacesData[] newArray(int size) {
            return new PlacesData[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public LatLng getPlaceLatLng() {
        return placeLatLng;
    }

    public void setPlaceLatLng(LatLng placeLatLng) {
        this.placeLatLng = placeLatLng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeName);
        parcel.writeParcelable(placeLatLng, i);
        parcel.writeInt(count);
    }
}
