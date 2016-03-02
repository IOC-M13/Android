package com.afodevelop.chronoschedule.models;

/**
 * Created by alex on 2/03/16.
 */
public class UserShift {

    //CLASSWIDE VARIABLES
    private int idUser, idShift;
    private String date;

    //CONSTRUCTORS
    public UserShift() {}

    public UserShift(int idUser, int idShift, String date){
        this.idUser = idUser;
        this.idShift = idShift;
        this.date = date;
    }

    //SETTERS AND GETTERS

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIdShift() {
        return idShift;
    }

    public void setIdShift(int idShift) {
        this.idShift = idShift;
    }
}
