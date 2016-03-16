package com.afodevelop.chronoschedule.controllers.sqliteControllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.afodevelop.chronoschedule.common.OrmCache;
import com.afodevelop.chronoschedule.model.Shift;
import com.afodevelop.chronoschedule.model.User;
import com.afodevelop.chronoschedule.model.UserShift;

import java.util.ArrayList;

/**
 * Created by alex on 15/03/16.
 */
public class SQLiteAssistant {

    // Private instantiation of the object
    private static SQLiteAssistant ourInstance = new SQLiteAssistant();

    // This getter provides the single existant instance
    public static SQLiteAssistant getInstance() {
        return ourInstance;
    }

    /**
     * SQLiteAssistant is a singleton, so it has a private constructor
     */
    private SQLiteAssistant() {}

    //ClassWide Declarations

    //CONSTANTS
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

    protected static final String DB_UPDATE_USERS = "" +
            "DROP TABLE IF EXISTS " + DB_USER_TABLE +"; ";
    protected static final String DB_UPDATE_SHIFTS = "" +
            "DROP TABLE IF EXISTS " + DB_SHIFT_TABLE +"; ";
    protected static final String DB_UPDATE_USERSHIFTS = "" +
            "DROP TABLE IF EXISTS " + DB_USERSHIFT_TABLE +"; ";


    //Logic Variables
    private Context context;
    private SQLiteHelper sQLiteHelper;
    private SQLiteDatabase db;

    /**
     * This is an internal SQLiteOpenHelper class!!
     * Tihs one is the core class when SQLite speaking...
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

    // LOGIC & METHODS

    /**
     * This method acts as a class initializator. It initializes the context and instantiates
     * The internal SQliteHelper instance with that same context.
     */
    public void initialize(Context c) throws Exception {
        // We set the context
        if (context == null){
            context = c;
            //we instantiate the SqliteHelper
            sQLiteHelper = new SQLiteHelper(context);
        } else {
            throw new Exception("Context already initialized on RSSFeedAssistant");
        }
    }

    /**
     * Open Database connection
     */
    public void openDb(){
        db = sQLiteHelper.getWritableDatabase();
    }

    /**
     * Close Database connection
     */
    public void closeDb(){
        sQLiteHelper.close();
    }

    /**
     * Insert a single user into database
     * @param user an User object which has to be persisted
     * @return long with row ID or -1 if error
     */
    private long putUser(User user){
        // Load data from argument
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_USER_IDUSER, user.getIdUser());
        newValues.put(KEY_USER_USERDNI, user.getDniUser());
        newValues.put(KEY_USER_USERNAME, user.getUserName());
        newValues.put(KEY_USER_REALNAME, user.getRealName());
        newValues.put(KEY_USER_PASS, user.getPass());
        newValues.put(KEY_USER_ADMIN, user.getAdmin());
        // return row ID
        return db.insert(DB_USER_TABLE, null, newValues);
    }

    /**
     * Insert a single shift into database
     * @param shift a Shift object which has to be persisted
     * @return ong with row ID or -1 if error
     */
    private long putShift(Shift shift){
        // Load data from argument
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_SHIFT_IDSHIFT, shift.getIdShift());
        newValues.put(KEY_SHIFT_NAME, shift.getName());
        newValues.put(KEY_SHIFT_STARTTIME, shift.getStartTime());
        newValues.put(KEY_SHIFT_ENDTIME, shift.getEndTime());
        newValues.put(KEY_SHIFT_COLOR, shift.getColor());
        // return row ID
        return db.insert(DB_SHIFT_TABLE, null, newValues);
    }

    /**
     * Insert a single userShift into database
     * @param userShift a UserShift object which has to be persisted
     * @return ong with row ID or -1 if error
     */
    private long putUserShift(UserShift userShift){
        // Load data from argument
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_USERSHIFT_IDUSER, userShift.getuser().getIdUser());
        newValues.put(KEY_USERSHIFT_IDSHIFT, userShift.getShift().getIdShift());
        newValues.put(KEY_USERSHIFT_DATE, userShift.getDate().toString());
        // return row ID
        return db.insert(DB_SHIFT_TABLE, null, newValues);
    }

    /**
     * This method inserts a row in the table with passed arguments
     * @param user an User object to be persisted
     * @return
     */
    public long insertUser(User user){
        openDb();
        long result =  putUser(user);
        closeDb();
        return result;
    }

    /**
     * This method inserts a row in the table with passed arguments
     * @param shift a Shift object to be persisted
     * @return
     */
    public long insertShift(Shift shift){
        openDb();
        long result =  putShift(shift);
        closeDb();
        return result;
    }

    /**
     * This method inserts a row in the table with passed arguments
     * @param userShift a UserShift object to be persisted
     * @return
     */
    public long insertUserShift(UserShift userShift){
        openDb();
        long result =  putUserShift(userShift);
        closeDb();
        return result;
    }

    /**
     * Insert an array of users into database
     * @param users an array of User objects to be persisited
     * @return an array of longs with all inserted row ids
     */
    public ArrayList<Long> insertUsers(ArrayList<User> users){
        ArrayList<Long> result = new ArrayList<>();
        openDb();
        for (User u: users) {
            result.add(putUser(u));
        }
        closeDb();
        return result;
    }

    /**
     * Insert an array of Shifts into database
     * @param shifts an array of Shift objects to be persisited
     * @return an array of longs with all inserted row ids
     */
    public ArrayList<Long> insertShifts(ArrayList<Shift> shifts){
        ArrayList<Long> result = new ArrayList<>();
        openDb();
        for (Shift s: shifts) {
            result.add(putShift(s));
        }
        closeDb();
        return result;
    }


    /**
     * Insert an array of UserShifts into database
     * @param userShifts an array of UserShift objects to be persisited
     * @return an array of longs with all inserted row ids
     */
    public ArrayList<Long> insertUserShifts(ArrayList<UserShift> userShifts) {
        ArrayList<Long> result = new ArrayList<>();
        openDb();
        for (UserShift us : userShifts) {
            result.add(putUserShift(us));
        }
        closeDb();
        return result;
    }

    /**
     * Insert a whole ORMCache objects container into DB
     * @param cache
     */
    private void persistOrmCache(OrmCache cache){
        insertUsers(cache.getUsersList());
        insertShifts(cache.getShiftsList());
        insertUserShifts(cache.getUserShiftsList());
    }

        /**
         * This method returns a Cursor pointing to all recorded Users in DB
         * @return
         */
    public ArrayList<User> getAllUsers(){
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
            closeDb();
            return result;
        }else {
            closeDb();
            return null;
        }
    }

    /**
     * This method returns a Cursor pointing to all recorded Shifts in DB
     * @return
     */
    public ArrayList<Shift> getAllShifts(){
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
            closeDb();
            return result;
        }else {
            closeDb();
            return null;
        }
    }

    /**
     * This method returns a cursor with the rows containing certain items by key field.
     * Theoretically, just a single row in resulting cursor should be present...
     * @param userName
     * @return
     */
    public ArrayList<User> getUsersByUserName(String userName){
        ArrayList<User> result = new ArrayList<>();
        Cursor dbRows;
        openDb();
        dbRows = db.query(true, DB_USER_TABLE, new String[] {
                KEY_USER_IDUSER,
                KEY_USER_USERDNI,
                KEY_USER_USERNAME,
                KEY_USER_REALNAME,
                KEY_USER_PASS,
                KEY_USER_ADMIN
        }, KEY_USER_USERNAME + " = " + userName, null, null, null, null, null);

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
            closeDb();
            return result;
        } else {
            closeDb();
            return null;
        }
    }

    /**
     * This method returns a cursor with the rows containing certain items by key field.
     * Theoretically, just a single row in resulting cursor should be present...
     * @param idShift
     * @return
     */
    public ArrayList<Shift> getShiftsById(int idShift){
        ArrayList<Shift> result = new ArrayList<>();
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
                result.add(new Shift(
                        dbRows.getInt(0),
                        dbRows.getString(1),
                        dbRows.getString(2),
                        dbRows.getString(3),
                        dbRows.getString(4)
                ));
            } while (dbRows.moveToNext());
            closeDb();
            return result;
        } else {
            closeDb();
            return null;
        }
    }

}
