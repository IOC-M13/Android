package com.afodevelop.chronoschedule.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class handles a relational <-> object-based translation, mapping and instance caching
 *
 * @author Alejandro Olivan Alvarez
 */
public class ORMCache {

    // CLASS-WIDE VARIABLES
    private LinkedHashMap<Integer,User> usersMap;
    private LinkedHashMap<Integer,Shift> shiftsMap;
    private ArrayList<UserShift> userShiftsList;

    // CONSTRUCTOR
    public ORMCache(){
        usersMap = new LinkedHashMap<>();
        shiftsMap = new LinkedHashMap<>();
        userShiftsList = new ArrayList<>();
    }

    // LOGIC
    /**
     * Initialize the Shifts ArrayList cache directly by passing an already filled
     * Shifts collection
     *
     * @author Alejandro Olivan Alvarez
     * @param users A collection of Shifts to be stored in cache
     */
    public void setUsersList (ArrayList<User> users){
        usersMap.clear();
        for (User u: users){
            usersMap.put(u.getIdUser(),u);
        }
    }

    /**
     * Initialize the Shifts ArrayList cache directly by passing an already filled
     * Shifts collection
     *
     * @author Alejandro Olivan Alvarez
     * @param shifts A collection of Shifts to be stored in cache
     */
    public void setShiftsList (ArrayList<Shift> shifts){
        shiftsMap.clear();
        for(Shift s: shifts){
            shiftsMap.put(s.getIdShift(),s);
        }
    }

    /**
     * Add a new UserShift instance too the cache
     *
     * @author Alejandro Olivan Alvarez
     * @param idUser An int idUser of the referenced User Instance
     * @param idShift An int idShift of the referenced Shift Instance
     * @param date the sql date for that journey
     */
    public void addUserShift (int idUser, int idShift, Date date) {
        userShiftsList.add(new UserShift(
                usersMap.get(idUser),
                shiftsMap.get(idShift),
                date
        ));
    }

    /**
     * Get userMap class variable getter
     *
     * @author Alejandro Olivan Alvarez
     * @return the class ArrayList User class variable
     */
    public ArrayList<User> getUsersList() {
        return new ArrayList<>(usersMap.values());
    }

    /**
     * Get shiftMap class variable getter
     *
     * @author Alejandro Olivan Alvarez
     * @return the class ArrayList Shift class variable
     */
    public ArrayList<Shift> getShiftsList() {
        return new ArrayList<>(shiftsMap.values());
    }

    /**
     * Get userShiftsList class variable getter
     *
     * @author Alejandro Olivan Alvarez
     * @return the class ArrayList UserShift class variable
     */
    public ArrayList<UserShift> getUserShiftsList() {
        return userShiftsList;
    }
}
