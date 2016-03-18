package com.afodevelop.chronoschedule.controllers.mysqlControllers;

import android.util.Log;

import com.afodevelop.chronoschedule.controllers.ormControllers.ORMCache;
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
    private static MySQLAssistant ourInstance = null;
    private boolean initialized;
    private Connection mySQLConnection;
    private MySQLConnectorFactory mySQLConnectorFactory;

    //CONSTRUCTOR
    private MySQLAssistant(){}



    //LOGIC

    /**
     * Singleton get instance method
     * @return
     */
    public static MySQLAssistant getInstance(){
        if (ourInstance == null){
            ourInstance = new MySQLAssistant();
        }
        return ourInstance;
    }

    /**
     * This method initializes the MySQLAssistant with a MySQLConnectionFactory
     * @param factory a pre-configured MySQLConnectorFactory
     */
    public void initialize(MySQLConnectorFactory factory){
        initialized = true;
        this.mySQLConnectorFactory = factory;
    }

    /**
     * This method allow to query wether the Assistant has been properly
     * initialized.
     * @return eiter true or false
     */
    public boolean isInitialized(){
        return initialized;
    }

    /**
     *
     * @param sqlQuery
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private ResultSet queryDatabase(String sqlQuery) throws SQLException, ClassNotFoundException,
            JdbcException {
        if (initialized) {
            openConnection();
            Statement st = mySQLConnection.createStatement();
            ResultSet rs = st.executeQuery(sqlQuery);
            Log.d("JDBC", "Executed query: " + sqlQuery);
            return rs;
        } else {
            throw new JdbcException("Uninitialized MySQLAssitant usage attempt");
        }
    }

    /**
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ArrayList<Shift> getShiftsResultSet() throws SQLException, ClassNotFoundException,
            JdbcException {

        ResultSet shifts = queryDatabase(SHIFTS_SELECT);
        ArrayList<Shift> result = new ArrayList<>();

        while (shifts.next()){
            result.add(new Shift(
                    shifts.getInt(1),
                    shifts.getString(2),
                    shifts.getString(3),
                    shifts.getString(4),
                    shifts.getString(5)
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
    public ArrayList<User> getUserResultSet() throws SQLException, ClassNotFoundException, JdbcException {

        ResultSet users = queryDatabase(USERS_SELECT);
        ArrayList<User> result = new ArrayList<>();

        while (users.next()){
            result.add(new User(
                    users.getInt(1),
                    users.getInt(6),
                    users.getString(2),
                    users.getString(3),
                    users.getString(4),
                    users.getString(5)
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
    public ORMCache InitializeUserShiftCache(ORMCache cache)
            throws SQLException, ClassNotFoundException, JdbcException {
        ResultSet userShifts = queryDatabase(USER_SHIFTS_SELECT);

        while (userShifts.next()){
            cache.addUserShift(userShifts.getInt(1),userShifts.getInt(2),userShifts.getDate(3));
        }
        closeConnection();

        return cache;
    }

    /**
     *
     * @return
     */
    public boolean checkConnectivity() throws JdbcException {
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
    public void openConnection() throws SQLException, ClassNotFoundException, JdbcException {
        if (initialized) {
        Log.d("JDBC","openning database");
        mySQLConnection = mySQLConnectorFactory.getConnection();
        } else {
            throw new JdbcException("Uninitialized MySQLAssitant usage attempt");
        }
    }

    /**
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException, JdbcException {
        if (initialized) {
            Log.d("JDBC","closing database");
            mySQLConnection.close();
        } else {
            throw new JdbcException("Uninitialized MySQLAssitant usage attempt");
        }
    }


    // SETTERS AND GETTERS


}
