package com.afodevelop.chronoschedule.controllers.mysqlControllers;

import android.util.Log;

import com.afodevelop.chronoschedule.common.JdbcException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by alex on 1/03/16.
 */

public class MySQLConnectorFactory {


    // CLASSWIDE VARIABLES
    Connection mySQLConnection = null;
    String url, dbUser, dbUserPassword;
    boolean initialized = false;


    //CONSTRUCTOR
    public MySQLConnectorFactory(
            String host, String port, String dbName, String dbUser, String dbUserPassword)
            throws JdbcException {

        if ( host != null && port != null && dbName != null && dbUser != null
                && dbUserPassword != null){
            url = "jdbc:mysql://" + host + ":" +	port + "/" + dbName;
            initialized = true;
        } else {
            Log.e("JDBC", "null argument provided for jdbc url assembling");
            throw new JdbcException ("While assembling MySQL connection jdbc url:" +
                    " null value parameter detected.");
        }
    }

    // LOGIC METHODS

    /**
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void establishConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        mySQLConnection = DriverManager.getConnection(url, dbUser, dbUserPassword);

    }

    /**
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getInstance() throws SQLException, ClassNotFoundException {

        if (initialized) {
            if (mySQLConnection == null) {
                establishConnection();
            }
            return mySQLConnection;
        } else {
            return null;
        }
    }

    // GETTERS AND SETTERS
    public boolean isInitialized() {
        return initialized;
    }

    public String getUrl() {
        return url;
    }

    public String getDbUser(){
        return dbUser;
    }

    public String getDbUserPassword() {
        return dbUserPassword;
    }
}
