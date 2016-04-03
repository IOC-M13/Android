package com.afodevelop.chronoschedule.controllers.mysqlControllers;

import android.util.Log;

import com.afodevelop.chronoschedule.model.JdbcException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is an auxiliar class of MySQLAssistant class.
 * It takes care of lower level connectivity details and management.
 *
 * @author Alejandro Olivan Alvarez
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
            Log.d("JDBC", "Initialized for: " + dbUrl);
        } else {
            Log.e("JDBC", "null argument provided for jdbc url assembling");
            throw new JdbcException ("While assembling MySQL connection jdbc url:" +
                    " null value parameter detected.");
        }
    }

    // LOGIC METHODS

    /**
     * This method initializes or renews the internal class connection to the
     * database, so it can be later required and used.
     * It will also provide a method to re-establish connection if the current
     * connection times-out.
     *
     * @author Alejandro Olivan Alvarez
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private void establishConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Log.d("establishConn URL", dbUrl);
        Log.d("establishConn User", dbUser);
        Log.d("establishConn Pass", dbUserPassword);
        mySQLConnection = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword);
        Log.d("establishConn stat","is closed = " + mySQLConnection.isClosed());
    }

    /**
     * This method returns a Connection class instance that points to the database.
     *
     * @author Alejandro Olivan Alvarez
     * @return A Connection to the MySQL server
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

    /**
     * Allows for initialization check
     *
     * @author Alejandro Olivan Alvarez
     * @return a boolean with initialization status boolean variable
     */
    public boolean isInitialized(){
        return initialized;
    }
}
