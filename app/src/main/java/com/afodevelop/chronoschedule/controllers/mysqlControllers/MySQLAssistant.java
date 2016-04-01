package com.afodevelop.chronoschedule.controllers.mysqlControllers;

import android.util.Log;

import com.afodevelop.chronoschedule.model.JdbcException;
import com.afodevelop.chronoschedule.model.ORMCache;
import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.afodevelop.chronoschedule.model.UserShift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * This class encapsulates all method related with SQL CRUD execution methods
 * against MySQL database. It uses a MySQLConnectorFactory to retrieve actual
 * connections to database as needed.
 *
 * @author Alejandro Olivan Alvarez
 */
public class MySQLAssistant {

    //CONSTANTS
    private static MySQLAssistant ourInstance = null;
    private static final String USERS_SELECT = "SELECT * FROM Users";
    private static final String USER_INSERT = "INSERT INTO Users " +
            "(userDni, userName, realName, pass, admin) VALUES (?, ?, ?, ?, ?)";
    private static final String USER_UPDATE = "UPDATE Users SET " +
            "userDni = ?, userName = ?, realName = ?, pass = ?, admin = ? " +
            "WHERE idUser = ?;";
    private static final String USER_DELETE = "DELETE FROM Users WHERE userName = ?;";
    private static final String SHIFTS_SELECT = "SELECT * FROM Shifts";
    private static final String SHIFT_INSERT = "INSERT INTO Shifts " +
            "(name, startTime, endTime, color) VALUES (?, ?, ?, ?)";
    private static final String SHIFT_UPDATE = "UPDATE Shifts SET " +
            "name = ?, startTime = ?, endTime = ?, color = ? " +
            "WHERE idShift = ?;";
    private static final String SHIFT_DELETE = "DELETE FROM Shifts WHERE idShift = ?;";
    private static final String USER_SHIFTS_SELECT = "SELECT * FROM UserShifts";
    private static final String USERSHIFT_INSERT = "INSERT INTO UserShifts " +
            "(idUser, idShift, date) VALUES (?, ?, ?)";
    private static final String USERSHIFT_UPDATE = "UPDATE UserShifts SET " +
            "idShift = ? " +
            "WHERE idUser = ? AND date = ?;";
    private static final String USERSHIFT_DELETE = "DELETE FROM UserShifts " +
            "WHERE idUser = ? AND date = ?;";

    //CLASSWIDE VARIABLES
    private boolean initialized = false;
    private Connection mySQLConnection;
    private MySQLConnectorFactory mySQLConnectorFactory;

    //CONSTRUCTOR
    private MySQLAssistant(){}

    //LOGIC

    /**
     * This class is asingleton class... and this is the preceptive get instance method
     *
     * @author Alejandro Olivan Alvarez
     * @return A unique/single MySQLAssistant class instance.
     */
    public static MySQLAssistant getInstance(){
        if (ourInstance == null){
            ourInstance = new MySQLAssistant();
        }
        return ourInstance;
    }

    /**
     * This method initializes the MySQLAssistant with a MySQLConnectionFactory
     *
     * @author Alejandro Olivan Alvarez
     * @param factory a pre-configured MySQLConnectorFactory
     */
    public void initialize(MySQLConnectorFactory factory) throws JdbcException {
        if (factory != null) {
            if (mySQLConnectorFactory == null) {
                initialized = true;
                mySQLConnectorFactory = factory;
            } else {
                throw new JdbcException("MySQLConnectorFactory already initialized on MySQLAssistant");
            }
        } else {
            throw new JdbcException("Cannot be initialized with a null MySQLConnectorFactory");
        }
    }

    /**
     * This method allow to query wether the Assistant has been properly
     * initialized.
     *
     * @author Alejandro Olivan Alvarez
     * @return eiter true or false
     */
    public boolean isInitialized(){
        return initialized;
    }

    /**
     * This method allows to execute general queries against the database.
     * The main target of this method is code optimization since it will be reused often.
     *
     * @author Alejandro Olivan Alvarez
     * @param sqlQuery A string with the SQL query we want to launch at the DB
     * @return a ResultSet with the DB reply on the query
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
     * This method queries the db for available Shifts in DB. it handles result set
     * reply generating a collection of Shift Instances.
     *
     * @author Alejandro Olivan Alvarez
     * @return an ArrayList containing all possible Shift instances
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ArrayList<Shift> getShiftsResultSet() throws SQLException, ClassNotFoundException,
            JdbcException {
        if (initialized) {
            ResultSet shifts = queryDatabase(SHIFTS_SELECT);
            ArrayList<Shift> result = new ArrayList<>();

            while (shifts.next()) {
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
            if (size > 0) {
                Log.d("JDBC", size + "Shift entries retrieved from database ");
                return result;
            } else {
                Log.d("JDBC", "No Shift entries could be retrieved from database ");
                return null;
            }
        } else {
            throw new JdbcException("Uninitialized MySQLAssitant usage attempt");
        }
    }

    /**
     * This method queries the db for available users in DB. it handles result set
     * reply generating a collection of user Instances.
     *
     * @author Alejandro Olivan Alvarez
     * @return an ArrayList containing all possible User instances
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ArrayList<User> getUserResultSet() throws SQLException, ClassNotFoundException, JdbcException {
        if (initialized) {
            ResultSet users = queryDatabase(USERS_SELECT);
            ArrayList<User> result = new ArrayList<>();

            while (users.next()) {
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
            if (size > 0) {
                Log.d("JDBC", size + "User entries retrieved from database ");
                return result;
            } else {
                Log.d("JDBC", "No User entries could be retrieved from database ");
                return null;
            }
        } else {
            throw new JdbcException("Uninitialized MySQLAssitant usage attempt");
        }
    }

    /**
     * This method generates a Memory cache of available users as User instances
     * within an ORMCache object.
     *
     * @author Alejandro Olivan Alvarez
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ORMCache InitializeUserShiftCache(ORMCache cache)
            throws SQLException, ClassNotFoundException, JdbcException {
        if (initialized){
            ResultSet userShifts = queryDatabase(USER_SHIFTS_SELECT);

            while (userShifts.next()){
                cache.addUserShift(userShifts.getInt(1), userShifts.getInt(2),
                        userShifts.getDate(3));
            }
            closeConnection();

            return cache;
        } else {
            throw new JdbcException("Uninitialized MySQLAssistant usage attempt");
        }
    }

    /**
     * This method performs a connectivity test against the SQL server
     *
     * @author Alejandro Olivan Alvarez
     * @return a boolean indicating connectivity is possible (true) or not (false)
     */
    public boolean checkConnectivity() throws JdbcException {
        if (initialized) {
            try {
                openConnection();
                closeConnection();
                Log.d("JDBC","Check connectivity success! returning true.");
                return true;
            } catch (SQLException e) {
                Log.w("JDBC", "While checking MySQL connectivity: caught SQLException");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                Log.w("JDBC", "While checking MySQL connectivity: caught ClassNotFoundException");
                e.printStackTrace();
            }
            Log.d("JDBC", "Check connectivity failure! returning false.");
            return false;
        } else {
            throw new JdbcException("Uninitialized MySQLAssitant usage attempt");
        }
    }

    /**
     * Handle SQL DCL query to persist a User instance in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param newUser the User instance to be persisted on DB
     * @throws SQLException
     * @throws JdbcException
     * @throws ClassNotFoundException
     */
    public void insertUser(User newUser) throws SQLException, JdbcException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(USER_INSERT);
        preparedStmt.setString(1, newUser.getDniUser());
        preparedStmt.setString(2, newUser.getUserName());
        preparedStmt.setString(3, newUser.getRealName());
        preparedStmt.setString(4, newUser.getPass());
        preparedStmt.setInt(5, newUser.getAdmin());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DML query to delete a User in DB
     *
     * @author Alejandro Olivan Alvare
     * @param targetUser the User instance actually mapped on DB
     * @throws SQLException
     * @throws JdbcException
     * @throws ClassNotFoundException
     */
    public void deleteUser(User targetUser) throws SQLException, JdbcException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(USER_DELETE);
        preparedStmt.setString(1, targetUser.getUserName());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DML query to update a User data in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param targetUser the User instance actually mapped on DB
     * @throws SQLException
     * @throws JdbcException
     * @throws ClassNotFoundException
     */
    public void updateUser(User targetUser) throws SQLException, JdbcException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(USER_UPDATE);
        preparedStmt.setString(1, targetUser.getDniUser());
        preparedStmt.setString(2, targetUser.getUserName());
        preparedStmt.setString(3, targetUser.getRealName());
        preparedStmt.setString(4, targetUser.getPass());
        preparedStmt.setInt(5, targetUser.getAdmin());
        preparedStmt.setInt(6, targetUser.getIdUser());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DCL query to persist a UserShift instance in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param s the UserShift instance to be persisted on DB
     * @throws JdbcException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void insertUserShift(UserShift s) throws JdbcException, SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(USERSHIFT_INSERT);
        preparedStmt.setInt(1, s.getUser().getIdUser());
        preparedStmt.setInt(2, s.getShift().getIdShift());
        preparedStmt.setDate(3, s.getDate());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DML query to delete a UserShift in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param s the UserShift instance actually mapped on DB
     * @throws JdbcException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void deleteUserShift(UserShift s) throws JdbcException, SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(USERSHIFT_DELETE);
        preparedStmt.setInt(1, s.getUser().getIdUser());
        preparedStmt.setDate(2, s.getDate());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DML query to update a UserShift data in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param s the UserShift instance actually mapped on DB
     * @throws JdbcException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void updateUserShift(UserShift s) throws JdbcException, SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(USERSHIFT_UPDATE);
        preparedStmt.setInt(1, s.getShift().getIdShift());
        preparedStmt.setInt(2, s.getUser().getIdUser());
        preparedStmt.setDate(3, s.getDate());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DCL query to persist a Shift instance in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param newShift the Shift instance to be persisted on DB
     * @throws JdbcException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void insertShift(Shift newShift) throws JdbcException, SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(SHIFT_INSERT);
        preparedStmt.setString(1, newShift.getName());
        preparedStmt.setTime(2, java.sql.Time.valueOf(newShift.getStartTime()));
        preparedStmt.setTime(3, java.sql.Time.valueOf(newShift.getEndTime()));
        preparedStmt.setString(4, newShift.getColor());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DML query to delete a Shift in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param targetShift the Shift instance actually mapped on DB
     * @throws JdbcException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void deleteShift(Shift targetShift) throws JdbcException, SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(SHIFT_DELETE);
        preparedStmt.setInt(1, targetShift.getIdShift());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * Handle SQL DML query to update a Shift data in DB
     *
     * @author Alejandro Olivan Alvarez
     * @param targetShift the Shift instance actually mapped on DB
     * @throws JdbcException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void updateShift(Shift targetShift) throws JdbcException, SQLException,
            ClassNotFoundException {
        openConnection();
        PreparedStatement preparedStmt = mySQLConnection.prepareStatement(SHIFT_UPDATE);
        preparedStmt.setString(1, targetShift.getName());
        preparedStmt.setTime(2, java.sql.Time.valueOf(targetShift.getStartTime()));
        preparedStmt.setTime(3, java.sql.Time.valueOf(targetShift.getEndTime()));
        preparedStmt.setString(4, targetShift.getColor());
        preparedStmt.setInt(5, targetShift.getIdShift());
        // execute the java preparedstatement
        preparedStmt.executeUpdate();
        closeConnection();
    }

    /**
     * This method orders a connection with database has to be obtained
     *
     * @author Alejandro Olivan Alvarez
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
     * This method orders a connection closing with database
     *
     * @author Alejandro Olivan Alvarez
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
}

