package com.afodevelop.chronoschedule.controllers.mysqlControllers;

import android.util.Log;

import com.afodevelop.chronoschedule.common.OrmCache;
import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by alex on 1/03/16.
 */
public class MySQLAssistant {


    //CONSTANTS
    private static final String USERS_SELECT = "SELECT * FROM Users";
    private static final String SHIFTS_SELECT = "SELECT * FROM Shifts";
    private static final String USER_SHIFTS_SELECT = "SELECT * FROM UserShifts";

    //CLASSWIDE VARIABLES
    private Connection mySQLConnection;
    private MySQLConnectorFactory mySQLConnectorFactory;

    //CONSTRUCTOR
    public MySQLAssistant(MySQLConnectorFactory factory){
        this.mySQLConnectorFactory = factory;
    }

    //LOGIC

    /**
     *
     * @param sqlQuery
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private ResultSet queryDatabase(String sqlQuery) throws SQLException, ClassNotFoundException {
        openConnection();
        Statement st = mySQLConnection.createStatement();
        ResultSet rs = st.executeQuery(sqlQuery);
        Log.d("JDBC","Executed query: " + sqlQuery );
        return rs;
    }

    /**
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ArrayList<Shift> getShiftsResultSet() throws SQLException, ClassNotFoundException {

        ResultSet shifts = queryDatabase(SHIFTS_SELECT);
        ArrayList<Shift> result = new ArrayList<>();

        while (shifts.next()){
            result.add(new Shift(
                    shifts.getInt(0),
                    shifts.getString(1),
                    shifts.getString(2),
                    shifts.getString(3),
                    shifts.getString(4)
            ));
        }
        closeConnection();

        int size = result.size();
        if (size > 0){
            Log.d("JDBC", size + "Shift entries retrieved from database ");
            return result;
        } else {
            Log.d("JDBC","No Shift entries could be retrieved from database ");
            return null;
        }
    }

    /**
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ArrayList<User> getUserResultSet() throws SQLException, ClassNotFoundException {

        ResultSet users = queryDatabase(USERS_SELECT);
        ArrayList<User> result = new ArrayList<>();

        while (users.next()){
            result.add(new User(
                    users.getInt(0),
                    users.getInt(5),
                    users.getString(1),
                    users.getString(2),
                    users.getString(3),
                    users.getString(4)
            ));
        }
        closeConnection();

        int size = result.size();
        if (size > 0){
            Log.d("JDBC", size + "User entries retrieved from database ");
            return result;
        } else {
            Log.d("JDBC","No User entries could be retrieved from database ");
            return null;
        }
    }

    /**
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public OrmCache InitializeUserShiftCache(OrmCache cache)
            throws SQLException, ClassNotFoundException {
        ResultSet userShifts = queryDatabase(USER_SHIFTS_SELECT);

        while (userShifts.next()){
            cache.addUserShift(userShifts.getInt(0),userShifts.getInt(1),userShifts.getDate(2));
        }
        closeConnection();
        return cache;
    }

    /**
     *
     * @return
     */
    public boolean checkConnectivity(){
        try {
            openConnection();
            closeConnection();
            return true;
        } catch (SQLException e) {
            Log.w("JDBC","While checking MySQL connectivity: caught SQLException");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.w("JDBC","While checking MySQL connectivity: caught ClassNotFoundException");
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void openConnection() throws SQLException, ClassNotFoundException {
        Log.d("JDBC","openning database");
       mySQLConnection = mySQLConnectorFactory.getInstance();
    }

    /**
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        Log.d("JDBC","closing database");
        mySQLConnection.close();
    }


    // SETTERS AND GETTERS


}
