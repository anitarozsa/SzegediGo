package com.example.szegedimenetrend;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Schedule implements Parcelable {
    private String scheduleNumber;
    private String routeName;
    private List<String> stops;

    public Schedule() {
        // Firebase-nek szükséges
    }

    public Schedule(String scheduleNumber, String routeName, List<String> stops) {
        this.scheduleNumber = scheduleNumber;
        this.routeName = routeName;
        this.stops = stops;
    }

    // --- Parcelable konstruktor ---
    protected Schedule(Parcel in) {
        scheduleNumber = in.readString();
        routeName = in.readString();
        stops = in.createStringArrayList(); // List<String> olvasása
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(scheduleNumber);
        parcel.writeString(routeName);
        parcel.writeStringList(stops);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // --- Getterek és setterek ---
    public String getScheduleNumber() {
        return scheduleNumber;
    }

    public void setScheduleNumber(String scheduleNumber) {
        this.scheduleNumber = scheduleNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<String> getStops() {
        return stops;
    }

    public void setStops(List<String> stops) {
        this.stops = stops;
    }
}
