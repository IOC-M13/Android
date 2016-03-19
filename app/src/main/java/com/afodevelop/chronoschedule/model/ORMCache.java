package com.afodevelop.chronoschedule.model;

import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.afodevelop.chronoschedule.model.UserShift;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by alex on 14/03/16.
 */
public class ORMCache {

    // CLASS-WIDE VARIABLES
    private ArrayList<User> usersList;
    private ArrayList<Shift> shiftsList;
    private ArrayList<UserShift> userShiftsList;

    // CONSTANTS

    // CONSTRUCTOR
    public ORMCache(){
        usersList = new ArrayList<>();
        shiftsList = new ArrayList<>();
        userShiftsList = new ArrayList<>();
    }

    // LOGIC
    public void addUser (User newUser){
        usersList.add(newUser);
    }

    public void setUsersList (ArrayList<User> users){
        usersList = users;
    }

    public void delUser (int idUser){
        usersList.remove(idUser);
    }

    public void clearUsers (){
        userShiftsList.clear();
        usersList.clear();
    }

    public void addShift (Shift newShift){
        shiftsList.add(newShift);
    }

    public void setShiftsList (ArrayList<Shift> shifts){
        shiftsList = shifts;
    }

    public void delShift (int idShift){
        shiftsList.remove(idShift);
    }

    public void clearShifts (){
        userShiftsList.clear();
        shiftsList.clear();
    }

    public void addUserShift (int idUser, int idShift, Date date){
        //UserShift newUserShift = new UserShift();
        //newUserShift.setUser(usersList.get(idUser - 1));
        //newUserShift.setShift(shiftsList.get(idShift - 1));
        //newUserShift.setDate(date);

        userShiftsList.add(new UserShift(
                usersList.get(idUser - 1),
                shiftsList.get(idShift - 1),
                date
        ));
    }

    public void clearUserShifts (){
        userShiftsList.clear();
    }

    public void clear(){
        userShiftsList.clear();
        usersList.clear();
        shiftsList.clear();
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public ArrayList<Shift> getShiftsList() {
        return shiftsList;
    }

    public ArrayList<UserShift> getUserShiftsList() {
        return userShiftsList;
    }
}
