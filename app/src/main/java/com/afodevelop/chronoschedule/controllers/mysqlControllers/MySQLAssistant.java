package com.afodevelop.chronoschedule.controllers.mysqlControllers;

import android.util.Log;

import com.afodevelop.chronoschedule.models.Shift;
import com.afodevelop.chronoschedule.models.User;
import com.afodevelop.chronoschedule.models.UserShift;

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
    private static final String USERS_SELECT = "SELECT * FROM 'mydb'.'Ususarios'";
    private static final String SHIFTS_SELECT = "SELECT * FROM 'mydb'.'Turnos'";
    private static final String USER_SHIFTS_SELECT = "SELECT * FROM 'mydb'.'TurnoUsuarios'";

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
            Shift shift = new Shift();
            shift.setIdShift(shifts.getInt(0));
            shift.setName(shifts.getString(1));
            shift.setStartTime(shifts.getString(2));
            shift.setEndTime(shifts.getString(3));
            result.add(shift);
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
            User user = new User();
            user.setIdUser(users.getInt(0));
            user.setDniUser(users.getString(1));
            user.setUserName(users.getString(2));
            user.setRealName(users.getString(3));
            user.setPass(users.getString(4));
            user.setAdmin(users.getInt(5));
            result.add(user);
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
    public ArrayList<UserShift> getUserShiftsResultSet()
            throws SQLException, ClassNotFoundException {

        ResultSet userShifts = queryDatabase(USER_SHIFTS_SELECT);
        ArrayList<UserShift> result = new ArrayList<>();

        while (userShifts.next()){
            UserShift userShift = new UserShift();
            userShift.setIdUser(userShifts.getInt(0));
            userShift.setIdShift(userShifts.getInt(1));
            userShift.setDate(userShifts.getString(2));

            result.add(userShift);
        }
        closeConnection();

        int size = result.size();
        if (size > 0){
            Log.d("JDBC", size + "UserShifts entries retrieved from database ");
            return result;
        } else {
            Log.d("JDBC","No UserShifts entries could be retrieved from database ");
            return null;
        }
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
