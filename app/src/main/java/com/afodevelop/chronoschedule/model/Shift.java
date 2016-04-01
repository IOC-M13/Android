package com.afodevelop.chronoschedule.model;

/**
 * This class is a MODEL CLASS.
 * It models a Shift. It contains its mapped attributes as name, id, color, times, and so
 *
 * @author Alejandro Olivan Alvarez
 */
public class Shift {

    //CLASS-WIDE VARIABLES
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
    /**
     * Gets the ID attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @return an int containing the ID attribute of this User instance
     */
    public int getIdShift() {
        return idShift;
    }

    /**
     * Gets the name attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string with the Shift name value
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @param name a string with the Shift name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the start time attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string with the Shift start time value
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @param startTime a string with the Shift start time value
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string with the Shift end time value
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @param endTime a string with the Shift end time value
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the color attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string with the Shift hex color value
     */
    public String getColor(){
        return color;
    }

    /**
     * Sets the color attribute for this Shift instance
     *
     * @author Alejandro Olivan Alvarez
     * @param color a string with the Shift hex color value
     */
    public void setColor(String color){
        this.color = color;
    }
}
