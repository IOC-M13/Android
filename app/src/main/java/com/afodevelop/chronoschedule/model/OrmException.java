package com.afodevelop.chronoschedule.model;

/**
 * A custom Exception to handle ORM operation-related exceptions
 *
 * @author Alejandro Olivan Alvarez
 */
public class OrmException extends Exception{

    public OrmException(String s){
        super(s);
    }
}
