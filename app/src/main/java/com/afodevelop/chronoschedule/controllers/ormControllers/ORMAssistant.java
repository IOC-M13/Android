package com.afodevelop.chronoschedule.controllers.ormControllers;

import android.util.Log;

import com.afodevelop.chronoschedule.controllers.mysqlControllers.JdbcException;
import com.afodevelop.chronoschedule.controllers.mysqlControllers.MySQLAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteAssistant;
import com.afodevelop.chronoschedule.controllers.sqliteControllers.SQLiteException;
import com.afodevelop.chronoschedule.model.ORMCache;

import java.sql.SQLException;

/**
 * Created by alex on 18/03/16.
 */
public class ORMAssistant {

    // CONSTANTS
    private static ORMAssistant ourInstance = null;

    // CLASS-WIDE VARIABLES
    private boolean initialized = false;
    private MySQLAssistant mySQLAssistant;
    private SQLiteAssistant sqLiteAssistant;

    // CONSTRUCTOR

    /**
     * ORMAssistant is a singleton class
     */
    private ORMAssistant() {}

    // LOGIC

    /**
     * Singleton style instance getter
     * @return the single ORMAssistant object.
     */
    public static ORMAssistant getInstance(){
        if (ourInstance == null){
            ourInstance = new ORMAssistant();
        }
        return ourInstance;
    }

    public void initialize(MySQLAssistant my, SQLiteAssistant sq) throws OrmException {
        if (my != null && sq != null){
            if (mySQLAssistant == null && sqLiteAssistant == null){
                mySQLAssistant = my;
                sqLiteAssistant = sq;
                initialized = true;
            } else {
                Log.d("ORMinitialize","Twice initialization attempt aborted.");
                throw new OrmException("MySQL or SQLite Assistants already initialized on ORMAssistant.");
            }
        } else {
            Log.d("ORMinitialize","Null param initialization handled.");
            Log.d("ORMinitialize","my = null: " + (my == null));
            Log.d("ORMinitialize","sq = null: " + (sq == null));
            throw new OrmException("Cannot be initialized with null MySQL or SQLite Assistants.");
        }
    }

    /**
     * This method triggers MySQL -> SQLite sync
     */
    public void launchResync() throws OrmException,
            SQLiteException, JdbcException, SQLException, ClassNotFoundException {
        if (initialized) {
            Log.d("launchResync", "resync called");
            ORMCache dataCache = new ORMCache();
            // Read and Cache MySQL data in memory
            dataCache.setShiftsList(mySQLAssistant.getShiftsResultSet());
            dataCache.setUsersList(mySQLAssistant.getUserResultSet());
            dataCache = mySQLAssistant.InitializeUserShiftCache(dataCache);

            //Dump cached data into SQLite
            sqLiteAssistant.persistOrmCache(dataCache);

        } else {
            Log.d("LaunchResync","exception thrown by uninitialized usage.");
            throw new OrmException("Resync impossible without previous class initilialization.");
        }

    }

    public boolean isInitialized(){
        return initialized;
    }

    // SETTERS AND GETTERS


}
