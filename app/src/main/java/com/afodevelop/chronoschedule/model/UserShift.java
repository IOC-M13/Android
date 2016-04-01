package com.afodevelop.chronoschedule.model;

import java.sql.Date;

/**
 * This class is a MODEL CLASS.
 * It models a UserShift. It contains its mapped attributes as idUser, idShift, and date
 *
 * @author Alejandro Olivan Alvarez
 */
public class UserShift {

    //CLASS-WIDE VARIABLES
    private User user;
    private Shift shift;
    private Date date;

    //CONSTRUCTORS

    public UserShift(User user, Shift shift, Date date){
        this.user = user;
        this.shift = shift;
        this.date = date;
    }

    //SETTERS AND GETTERS

    /**
     * Gets the User of this UserShift instance
     *
     * @author Alejandro Olivan Alvarez
     * @return this UserShift instance User variable
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the User for this UserShift instance
     *
     * @author Alejandro Olivan Alvarez
     * @param user the User instance referenced by this UserShift
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets this UserShift instance date
     *
     * @author Alejandro Olivan Alvarez
     * @return The date of this UserShift as sql.Date instance
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the Shift of this UserShift instance
     *
     * @author Alejandro Olivan Alvarez
     * @return this UserShift instance Shift variable
     */
    public Shift getShift() {
        return shift;
    }

    /**
     * Sets the Shift for this UserShift instance
     *
     * @author Alejandro Olivan Alvarez
     * @param shift the Shift instance referenced by this UserShift
     */
    public void setShift(Shift shift) {
        this.shift = shift;
    }
}
