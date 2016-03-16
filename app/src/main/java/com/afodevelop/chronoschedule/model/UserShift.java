package com.afodevelop.chronoschedule.model;

import java.sql.Date;

/**
 * Created by alex on 2/03/16.
 */
public class UserShift {

    //CLASSWIDE VARIABLES
    private User user;
    private Shift shift;
    private Date date;

    //CONSTRUCTORS
    public UserShift() {}

    public UserShift(User user, Shift shift, Date date){
        this.user = user;
        this.shift = shift;
        this.date = date;
    }

    //SETTERS AND GETTERS

    public User getuser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }
}
