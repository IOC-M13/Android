package com.afodevelop.chronoschedule.controllers.mysqlControllers;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by alex on 1/03/16.
 */

public class MySQLConnectorFactory {


    // CLASSWIDE VARIABLES
    Connection mySQLConnection = null;
    String dbHost, dbPort, dbName, dbUser, dbUserPassword, dbUrl;
    boolean initialized = false;


    //CONSTRUCTOR
    public MySQLConnectorFactory(
            String host, String port, String name, String user, String pass)
            throws JdbcException {

        if ( host != null && port != null && name != null && user != null && pass != null){
            this.dbHost = host;
            this.dbPort = port;
            this.dbName = name;
            this.dbUser = user;
            this.dbUserPassword = pass;
            this.dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
            this.initialized = true;
            Log.d("JDBC", "URL: " + dbUrl);
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
        Log.d("establishConn URL", dbUrl);
        Log.d("establishConn User", dbUser);
        Log.d("establishConn Pass", dbUserPassword);
        mySQLConnection = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword);
        Log.d("establishConn conn","" + mySQLConnection.isClosed());
    }

    /**
     *
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getConnection() throws SQLException, ClassNotFoundException {

        if (initialized) {
            if (mySQLConnection == null || mySQLConnection.isClosed()) {
                Log.d("getConnection","is NULL, must reconnect");
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
        return dbUrl;
    }

    public String getDbUser(){
        return dbUser;
    }

    public String getDbUserPassword() {
        return dbUserPassword;
    }
}
