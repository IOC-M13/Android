package com.afodevelop.chronoschedule.model;

/**
 * A custom Exception to handle SQLite operation-related exceptions
 *
 * @author Alejandro Olivan Alvarez
 */
public class SQLiteException extends Exception {

    public SQLiteException(String s){
        super(s);
    }
}
