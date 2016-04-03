package com.afodevelop.chronoschedule.controllers.sqliteControllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.afodevelop.chronoschedule.model.ORMCache;
import com.afodevelop.chronoschedule.model.SQLiteException;
import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.afodevelop.chronoschedule.model.UserShift;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class encapsulates all needed methods to interface with local SQLite database.
 *
 * @author Alejandro Olivan Alvarez
 */
public class SQLiteAssistant {

    //CONSTANTS
    private static SQLiteAssistant ourInstance = null;
    protected static final int VERSION = 1;
    protected static final String TAG = "SQLConnection";
    protected static final String DB_NAME = "BDChronoSchedule";
    protected static final String DB_USER_TABLE = "Users";
    protected static final String KEY_USER_IDUSER = "idUser";
    protected static final String KEY_USER_USERDNI = "userDni";
    protected static final String KEY_USER_USERNAME = "userName";
    protected static final String KEY_USER_REALNAME= "realName";
    protected static final String KEY_USER_PASS = "pass";
    protected static final String KEY_USER_ADMIN = "admin";
    protected static final String DB_CREATE_USERS = "" +
            "CREATE TABLE " + DB_USER_TABLE + " ( "
            + KEY_USER_IDUSER + " INTEGER PRIMARY KEY, "
            + KEY_USER_USERDNI + " TEXT, "
            + KEY_USER_USERNAME + " TEXT, "
            + KEY_USER_REALNAME + " TEXT, "
            + KEY_USER_PASS + " TEXT , "
            + KEY_USER_ADMIN + " INTEGER );";
    protected static final String DB_CLEAR_USERS = "" +
            "DELETE FROM " + DB_USER_TABLE +";";
    protected static final String DB_SHIFT_TABLE = "Shifts";
    protected static final String KEY_SHIFT_IDSHIFT = "idShift";
    protected static final String KEY_SHIFT_NAME = "name";
    protected static final String KEY_SHIFT_STARTTIME = "startTime";
    protected static final String KEY_SHIFT_ENDTIME = "endTime";
    protected static final String KEY_SHIFT_COLOR = "color";
    protected static final String DB_CREATE_SHIFTS = "" +
            "CREATE TABLE " + DB_SHIFT_TABLE + " ( "
            + KEY_SHIFT_IDSHIFT + " INTEGER PRIMARY KEY, "
            + KEY_SHIFT_NAME + " TEXT, "
            + KEY_SHIFT_STARTTIME + " TEXT, "
            + KEY_SHIFT_ENDTIME + " TEXT, "
            + KEY_SHIFT_COLOR + " TEXT );";
    protected static final String DB_CLEAR_SHIFTS = "" +
            "DELETE FROM " + DB_SHIFT_TABLE +";";
    protected static final String DB_USERSHIFT_TABLE = "UserShifts";
    protected static final String KEY_USERSHIFT_IDUSER = "idUser";
    protected static final String KEY_USERSHIFT_IDSHIFT = "idShift";
    protected static final String KEY_USERSHIFT_DATE = "date";
    protected static final String DB_CREATE_USERSHIFTS = "" +
            "CREATE TABLE " + DB_USERSHIFT_TABLE + " ( "
            + KEY_USERSHIFT_IDUSER + " INTEGER, "
            + KEY_USERSHIFT_IDSHIFT + " INTEGER, "
            + KEY_USERSHIFT_DATE + " TEXT, "
            + "PRIMARY KEY ("
            + KEY_USERSHIFT_IDUSER + ", "
            + KEY_USERSHIFT_IDSHIFT + ", "
            + KEY_USERSHIFT_DATE + "));";
    protected static final String DB_CLEAR_USERSSHIFTS = "" +
            "DELETE FROM " + DB_USERSHIFT_TABLE +";";
    protected static final String DB_UPDATE_USERS = "" +
            "DROP TABLE IF EXISTS " + DB_USER_TABLE +"; ";
    protected static final String DB_UPDATE_SHIFTS = "" +
            "DROP TABLE IF EXISTS " + DB_SHIFT_TABLE +"; ";
    protected static final String DB_UPDATE_USERSHIFTS = "" +
            "DROP TABLE IF EXISTS " + DB_USERSHIFT_TABLE +"; ";

    // INTERNAL CLASSES

    /**
     * This is an internal SQLiteOpenHelper class!!
     * This one is the core class when SQLite speaking...
     *
     * @author Alejandro Olivan Alvarez
     */
    private class SQLiteHelper extends SQLiteOpenHelper { // <- START OF INNER CLASS

        //Declarations

        //Constructor
        public SQLiteHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        //Logic
        @Override
        public void onCreate(SQLiteDatabase db) {
            //Execute SQL DB creation commands
            db.execSQL(DB_CREATE_USERS);
            Log.d(TAG, DB_CREATE_USERS);
            db.execSQL(DB_CREATE_SHIFTS);
            Log.d(TAG, DB_CREATE_SHIFTS);
            db.execSQL(DB_CREATE_USERSHIFTS);
            Log.d(TAG, DB_CREATE_USERSHIFTS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version: " + oldVersion + " to new version: "
                    + newVersion + ". All data will be wiped out!");
            db.execSQL(DB_UPDATE_USERS);
            db.execSQL(DB_UPDATE_SHIFTS);
            db.execSQL(DB_UPDATE_USERSHIFTS);
            onCreate(db);
        }

    } // <- END OF INNER CLASS!!!

    // CLASS-WIDE VARIABLES
    private SQLiteHelper sQLiteHelper;
    private SQLiteDatabase db;
    private Context context;
    private boolean initialized = false;

    // CONSTRUCTOR
    /**
     * SQLiteAssistant is a singleton, so it has a private constructor
     * @author Alejandro Olivan Alvarez
     *
     */
    private SQLiteAssistant() {}


    // LOGIC & METHODS
    /**
     * This method acts as a class initializator. It initializes the context and instantiates
     * The internal SQliteHelper instance with that same context.
     *
     * @author Alejandro Olivan Alvarez
     */
    public void initialize(Context c) throws SQLiteException {
        // We set the context
        if (c != null){
            if (context == null) {
                context = c;
                //we instantiate the SqliteHelper
                sQLiteHelper = new SQLiteHelper(context);
                initialized = true;
            } else {
                throw new SQLiteException("Context already initialized on RSSFeedAssistant");
            }
        } else {
            throw new SQLiteException("Cannot be initialized with Null Context.");
        }
    }

    /**
     * This getter provides the single existent instance
     *
     * @author Alejandro Olivan Alvarez
     */
    public static SQLiteAssistant getInstance() {
        if (ourInstance == null) {
            ourInstance = new SQLiteAssistant();
        }
        return ourInstance;
    }

    /**
     * Open Database connection
     *
     * @author Alejandro Olivan Alvarez
     */
    public synchronized void openDb() throws SQLiteException {
        if (initialized) {
            if (db == null) {
                db = sQLiteHelper.getWritableDatabase();
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Close Database connection
     *
     * @author Alejandro Olivan Alvarez
     */
    public synchronized void closeDb() throws SQLiteException {
        if (initialized) {
            if (db != null && db.isOpen()) {
                sQLiteHelper.close();
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Insert a single user into database
     *
     * @author Alejandro Olivan Alvarez
     * @param user an User object which has to be persisted
     * @return long with row ID or -1 if error
     */
    private long putUser(User user) throws SQLiteException {
        if (initialized) {
            // Load data from argument
            ContentValues newValues = new ContentValues();
            newValues.put(KEY_USER_IDUSER, user.getIdUser());
            newValues.put(KEY_USER_USERDNI, user.getDniUser());
            newValues.put(KEY_USER_USERNAME, user.getUserName());
            newValues.put(KEY_USER_REALNAME, user.getRealName());
            newValues.put(KEY_USER_PASS, user.getPass());
            newValues.put(KEY_USER_ADMIN, user.getAdmin());
            // return row ID
            openDb();
            long result = db.insert(DB_USER_TABLE, null, newValues);
            return result;
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Insert a single shift into database
     *
     * @author Alejandro Olivan Alvarez
     * @param shift a Shift object which has to be persisted
     * @return ong with row ID or -1 if error
     */
    private long putShift(Shift shift) throws SQLiteException {
        if (initialized) {
            // Load data from argument
            ContentValues newValues = new ContentValues();
            newValues.put(KEY_SHIFT_IDSHIFT, shift.getIdShift());
            newValues.put(KEY_SHIFT_NAME, shift.getName());
            newValues.put(KEY_SHIFT_STARTTIME, shift.getStartTime());
            newValues.put(KEY_SHIFT_ENDTIME, shift.getEndTime());
            newValues.put(KEY_SHIFT_COLOR, shift.getColor());
            // return row ID
            openDb();
            long result = db.insert(DB_SHIFT_TABLE, null, newValues);
            return result;
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Insert a single userShift into database
     *
     * @author Alejandro Olivan Alvarez
     * @param userShift a UserShift object which has to be persisted
     * @return ong with row ID or -1 if error
     */
    private long putUserShift(UserShift userShift) throws SQLiteException {
        if (initialized) {
            // Load data from argument
            ContentValues newValues = new ContentValues();
            newValues.put(KEY_USERSHIFT_IDUSER, userShift.getUser().getIdUser());
            newValues.put(KEY_USERSHIFT_IDSHIFT, userShift.getShift().getIdShift());
            newValues.put(KEY_USERSHIFT_DATE, userShift.getDate().toString());
            // return row ID
            openDb();
            long result = db.insert(DB_USERSHIFT_TABLE, null, newValues);
            return result;
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Insert an array of users into database
     *
     * @author Alejandro Olivan Alvarez
     * @param users an array of User objects to be persisited
     * @return an array of longs with all inserted row ids
     */
    public ArrayList<Long> insertUsers(ArrayList<User> users) throws SQLiteException {
        if (initialized) {
            ArrayList<Long> result = new ArrayList<>();
            openDb();
            db.execSQL(DB_CLEAR_USERS);
            for (User u: users) {
                result.add(putUser(u));
            }
            return result;
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Insert an array of Shifts into database
     *
     * @author Alejandro Olivan Alvarez
     * @param shifts an array of Shift objects to be persisited
     * @return an array of longs with all inserted row ids
     */
    public ArrayList<Long> insertShifts(ArrayList<Shift> shifts) throws SQLiteException {
        if (initialized) {
            ArrayList<Long> result = new ArrayList<>();
            openDb();
            db.execSQL(DB_CLEAR_SHIFTS);
            for (Shift s: shifts) {
                result.add(putShift(s));
            }
            return result;
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Insert an array of UserShifts into database
     *
     * @author Alejandro Olivan Alvarez
     * @param userShifts an array of UserShift objects to be persisited
     * @return an array of longs with all inserted row ids
     */
    public ArrayList<Long> insertUserShifts(ArrayList<UserShift> userShifts) throws SQLiteException {
        if (initialized) {
            ArrayList<Long> result = new ArrayList<>();
            openDb();
            db.execSQL(DB_CLEAR_USERSSHIFTS);
            for (UserShift us : userShifts) {
                result.add(putUserShift(us));
            }
            return result;
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * Insert a whole ORMCache objects container into DB
     *
     * @author Alejandro Olivan Alvarez
     * @param cache an ORMCache instance to be persisted in DB
     */
    public void persistOrmCache(ORMCache cache) throws SQLiteException {
        if (initialized) {
            if (cache != null) {
                Log.d("persistOrmCache", "users: " + cache.getUsersList().size());
                insertUsers(cache.getUsersList());
                Log.d("persistOrmCache", "shifts: " + cache.getShiftsList().size());
                insertShifts(cache.getShiftsList());
                Log.d("persistOrmCache", "usershifts: " + cache.getUserShiftsList().size());
                insertUserShifts(cache.getUserShiftsList());
            } else {
                throw new SQLiteException("While persisting ORM cache: Null object was provided!");
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * This method returns a Cursor pointing to all recorded Users in DB
     *
     * @author Alejandro Olivan Alvarez
     * @return a collection with all possible User instances from DB
     */
    public ArrayList<User> getAllUsers() throws SQLiteException {
        if (initialized) {
            ArrayList<User> result = new ArrayList<>();
            Cursor dbRows;
            openDb();
            dbRows = db.query(DB_USER_TABLE, new String[]{
                    KEY_USER_IDUSER,
                    KEY_USER_USERDNI,
                    KEY_USER_USERNAME,
                    KEY_USER_REALNAME,
                    KEY_USER_PASS,
                    KEY_USER_ADMIN
            }, null, null, null, null, null);

            if (dbRows.moveToFirst()){
                do {
                    result.add(new User(
                            dbRows.getInt(0),
                            dbRows.getInt(5),
                            dbRows.getString(1),
                            dbRows.getString(2),
                            dbRows.getString(3),
                            dbRows.getString(4)
                    ));
                } while (dbRows.moveToNext());
                dbRows.close();
                return result;
            }else {
                dbRows.close();

                return null;
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * This method returns a Cursor pointing to all recorded Shifts in DB
     *
     * @author Alejandro Olivan Alvarez
     * @return a collection with all possible Shift instances from DB
     */
    public ArrayList<Shift> getAllShifts() throws SQLiteException {
        if (initialized) {
            ArrayList<Shift> result = new ArrayList<>();
            Cursor dbRows;
            openDb();
            dbRows = db.query(DB_SHIFT_TABLE, new String[]{
                    KEY_SHIFT_IDSHIFT,
                    KEY_SHIFT_NAME,
                    KEY_SHIFT_STARTTIME,
                    KEY_SHIFT_ENDTIME,
                    KEY_SHIFT_COLOR
            }, null, null, null, null, null);

            if (dbRows.moveToFirst()){
                do {
                    result.add(new Shift(
                            dbRows.getInt(0),
                            dbRows.getString(1),
                            dbRows.getString(2),
                            dbRows.getString(3),
                            dbRows.getString(4)
                    ));
                } while (dbRows.moveToNext());
                dbRows.close();
                return result;
            }else {
                dbRows.close();
                return null;
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * This method builds an array of strings with all shift names in DB
     *
     * @author Alejandro Olivan Alvarez
     * @return a string array with all shift names in DB
     * @throws SQLiteException
     */
    public String[] getShiftNames() throws SQLiteException {
        ArrayList<Shift> shifts = getAllShifts();
        if (!shifts.isEmpty()) {
            String[] result = new String[shifts.size()];
            for (int i = 0; i < shifts.size(); i++) {
                result[i] = shifts.get(i).getName();
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * This method builds an array of strings with all shift colors in DB
     *
     * @author Alejandro Olivan Alvarez
     * @return A string array with all colors of all shifts in DB
     * @throws SQLiteException
     */
    public String[] getShiftColors() throws SQLiteException {
        ArrayList<Shift> shifts = getAllShifts();
        if (!shifts.isEmpty()) {
            String[] result = new String[shifts.size()];
            for (int i = 0; i < shifts.size(); i++) {
                result[i] = shifts.get(i).getColor();
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * This method retrieves the color of an user work day by means of
     * asigned shift to him/her. The method needs the User object and the date.
     *
     * @author Alejandro Olivan Alvarez
     * @param u User to which query the calendar
     * @param d Date at which Shift color has to be queried.
     * @return The color attribute value as string of the first matching criteria Shift
     * @throws SQLiteException
     * @throws ParseException
     */
    public String getShiftColor(User u, Date d) throws SQLiteException, ParseException {
        UserShift userShift = getUserShift(u, d);
        if (userShift != null) {
            return userShift.getShift().getColor();
        } else {
            return null;
        }
    }

    /**
     * This method builds an array of strings with all usernames in DB
     *
     * @author Alejandro Olivan Alvarez
     * @return String full fith username strings
     * @throws SQLiteException
     */
    public String[] getUserNames() throws SQLiteException {
        ArrayList<User> users = getAllUsers();
        if (!users.isEmpty()) {
            String[] result = new String[users.size()];
            for (int i = 0; i < users.size(); i++) {
                result[i] = users.get(i).getUserName();
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * This method returns a cursor with the rows containing certain items by key field.
     * Theoretically, just a single row in resulting cursor should be present...
     *
     * @author Alejandro Olivan Alvarez
     * @param userName The userName to filter during the DB query
     * @return the first matching User found at DB
     */
    public User getUserByUserName(String userName) throws SQLiteException {
        if (initialized) {
            ArrayList<User> candidates = new ArrayList<>();
            Cursor dbRows;
            openDb();
            dbRows = db.query(true, DB_USER_TABLE, new String[] {
                    KEY_USER_IDUSER,
                    KEY_USER_USERDNI,
                    KEY_USER_USERNAME,
                    KEY_USER_REALNAME,
                    KEY_USER_PASS,
                    KEY_USER_ADMIN }, KEY_USER_USERNAME + " = '" + userName +"'", null, null, null, null, null);

            if (dbRows.moveToFirst()){
                do {
                    candidates.add(new User(
                            dbRows.getInt(0),
                            dbRows.getInt(5),
                            dbRows.getString(1),
                            dbRows.getString(2),
                            dbRows.getString(3),
                            dbRows.getString(4)
                    ));
                } while (dbRows.moveToNext());
                dbRows.close();
                if (!candidates.isEmpty()) {
                    return candidates.get(0);
                } else {
                    return null;
                }
            } else {
                dbRows.close();
                return null;
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * This method returns a cursor with the rows containing certain items by key field.
     * Theoretically, just a single row in resulting cursor should be present...
     *
     * @author Alejandro Olivan Alvarez
     * @param idShift the filtering ID value as int to filter Shifts
     * @return The first matching Shift obtained on query
     */
    public Shift getShiftById(int idShift) throws SQLiteException {
        if (initialized) {
            ArrayList<Shift> candidates = new ArrayList<>();
            Cursor dbRows;
            openDb();
            dbRows = db.query(true, DB_SHIFT_TABLE, new String[] {
                    KEY_SHIFT_IDSHIFT,
                    KEY_SHIFT_NAME,
                    KEY_SHIFT_STARTTIME,
                    KEY_SHIFT_ENDTIME,
                    KEY_SHIFT_COLOR
            }, KEY_SHIFT_IDSHIFT + " = " + idShift, null, null, null, null, null);

            if (dbRows.moveToFirst()){
                do {
                    candidates.add(new Shift(
                            dbRows.getInt(0),
                            dbRows.getString(1),
                            dbRows.getString(2),
                            dbRows.getString(3),
                            dbRows.getString(4)
                    ));
                } while (dbRows.moveToNext());
                dbRows.close();
                if (!candidates.isEmpty()) {
                    return candidates.get(0);
                } else {
                    return null;
                }
            } else {
                dbRows.close();
                return null;
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * This method returns an UserShift object from DB based on a query that has
     * User and date search filters. Those data are received as arguments
     *
     * @author Alejandro Olivan Alvarez
     * @param u the user we want to filter the search.
     * @param d the date at which we are looking for.
     * @return The resulting UserShift nstance of the search
     * @throws SQLiteException
     * @throws ParseException
     */
    public UserShift getUserShift(User u, Date d) throws SQLiteException, ParseException {
        if (initialized) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<UserShift> candidates = new ArrayList<>();
            Cursor dbRows;
            int userId = u.getIdUser();
            String date = df.format(d);

            openDb();
            dbRows = db.query(true, DB_USERSHIFT_TABLE, new String[] {
                    KEY_USERSHIFT_IDUSER,
                    KEY_USERSHIFT_IDSHIFT,
                    KEY_USERSHIFT_DATE
            }, KEY_USERSHIFT_IDUSER + " = " + userId + " AND " +
                    KEY_USERSHIFT_DATE + " = '" + date + "'", null, null, null, null, null);

            if (dbRows.moveToFirst()){
                do {
                    candidates.add(new UserShift(
                            u,
                            getShiftById(dbRows.getInt(1)),
                            new java.sql.Date(df.parse(dbRows.getString(2)).getTime())
                    ));
                } while (dbRows.moveToNext());
                dbRows.close();
                if (!candidates.isEmpty()) {
                    return candidates.get(0);
                } else {
                    return null;
                }
            } else {
                dbRows.close();
                return null;
            }
        } else {
            throw new SQLiteException("SQLiteAssistant still not initialized.");
        }
    }

    /**
     * This method allows external check of initialized condition of this instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a boolean indicating if the instance has been initialized (true) or not (false)
     */
    public boolean isInitialized(){
        return initialized;
    }
}
