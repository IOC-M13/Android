package com.afodevelop.chronoschedule.model;

/**
 * A custom Exception to handle JDBC operation-related exceptions
 *
 * @author Alejandro Olivan Alvarez
 */
public class JdbcException extends Exception {

    public JdbcException(String s){
        super(s);
    }
}
