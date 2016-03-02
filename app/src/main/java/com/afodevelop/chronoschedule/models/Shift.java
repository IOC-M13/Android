package com.afodevelop.chronoschedule.models;

/**
 * Created by alex on 2/03/16.
 */
public class Shift {

    //CLASSWIDE VARIABLES
    private int idShift;
    private String name, startTime, endTime;

    //CONSTRUCTORS
    public Shift() {}

    public Shift(int id, String name, String strTime, String endTime){
        this.idShift = id;
        this.name = name;
        this.startTime = strTime;
        this.endTime = endTime;
    }

    //SETTERS AND GETTERS

    public int getIdShift() {
        return idShift;
    }

    public void setIdShift(int idShift) {
        this.idShift = idShift;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
