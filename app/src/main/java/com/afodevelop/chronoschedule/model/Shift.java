package com.afodevelop.chronoschedule.model;

/**
 * Created by alex on 2/03/16.
 */
public class Shift {

    //CLASSWIDE VARIABLES
    private int idShift;
    private String name, startTime, endTime, color;

    //CONSTRUCTORS
    public Shift() {}

    public Shift(int id, String name, String strTime, String endTime, String color){
        this.idShift = id;
        this.name = name;
        this.startTime = strTime;
        this.endTime = endTime;
        this.color = color;
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

    public String getColor(){
        return color;
    }

    public void setColor(String color){
        this.color = color;
    }
}
