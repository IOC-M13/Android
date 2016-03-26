package com.afodevelop.chronoschedule.model;

import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.afodevelop.chronoschedule.model.UserShift;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by alex on 14/03/16.
 */
public class ORMCache {

    // CLASS-WIDE VARIABLES
    private LinkedHashMap<Integer,User> usersMap;
    private LinkedHashMap<Integer,Shift> shiftsMap;
    private ArrayList<UserShift> userShiftsList;

    // CONSTANTS

    // CONSTRUCTOR
    public ORMCache(){
        usersMap = new LinkedHashMap<>();
        shiftsMap = new LinkedHashMap<>();
        userShiftsList = new ArrayList<>();
    }

    // LOGIC
    public void addUser (User newUser){
        usersMap.put(newUser.getIdUser(), newUser);
    }

    public void setUsersList (ArrayList<User> users){
        usersMap.clear();
        for (User u: users){
            usersMap.put(u.getIdUser(),u);
        }
    }

    public void delUser (int idUser){
        usersMap.remove(idUser);
    }

    public void clearUsers (){
        userShiftsList.clear();
        usersMap.clear();
    }

    public void addShift (Shift newShift){
        shiftsMap.put(newShift.getIdShift(), newShift);
    }

    public void setShiftsList (ArrayList<Shift> shifts){
        shiftsMap.clear();
        for(Shift s: shifts){
            shiftsMap.put(s.getIdShift(),s);
        }
    }

    public void delShift (int idShift){
        shiftsMap.remove(idShift);
    }

    public void clearShifts (){
        userShiftsList.clear();
        shiftsMap.clear();
    }

    public void addUserShift (int idUser, int idShift, Date date){

        userShiftsList.add(new UserShift(
                usersMap.get(idUser),
                shiftsMap.get(idShift),
                date
        ));
    }

    public void clearUserShifts (){
        userShiftsList.clear();
    }

    public void clear(){
        userShiftsList.clear();
        usersMap.clear();
        shiftsMap.clear();
    }

    public ArrayList<User> getUsersList() {
        return new ArrayList<>(usersMap.values());
    }

    public ArrayList<Shift> getShiftsList() {
        return new ArrayList<>(shiftsMap.values());
    }

    public ArrayList<UserShift> getUserShiftsList() {
        return userShiftsList;
    }
}
