package com.example.planatrip;

public class CurrentTripObject {

    private String nameOfTrip;
    private Integer tripID;

    public CurrentTripObject(String nameOfTrip, Integer tripID) {
        this.nameOfTrip = nameOfTrip;
        this.tripID = tripID;
    }

    public String getNameOfTrip() {
        return nameOfTrip;
    }

    public Integer getTripID() {
        return tripID;
    }

}
