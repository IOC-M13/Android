package com.afodevelop.chronoschedule.model;

/**
 * Created by alex on 2/03/16.
 */
public class User {

    //CLASSWIDE VARIABLES
    private int idUser, admin;
    private String dniUser, userName, realName, pass;


    //CONSTRUCTORS
    public User() {}

    public User(int id, int admin, String dni, String uName, String rName, String pass) {
        this.idUser = id;
        this.admin = admin;
        this.dniUser = dni;
        this.userName = uName;
        this.realName = rName;
        this.pass = pass;
    }

    // LOGIC
    public boolean isAdmin(){
        return (admin == 1);
    }

    //SETTERS AND GETTERS

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getDniUser() {
        return dniUser;
    }

    public void setDniUser(String dniUser) {
        this.dniUser = dniUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
