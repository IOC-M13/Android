package com.afodevelop.chronoschedule.model;

/**
 * This class is a MODEL CLASS.
 * It models a User. It contains its mapped attributes as name, id, dni, admin, and so
 *
 * @author Alejandro Olivan Alvarez
 */
public class User {

    //CLASS-WIDE VARIABLES
    private int idUser, admin;
    private String dniUser, userName, realName, pass;


    //CONSTRUCTORS
    public User() {}

    public User(int id, int admin, String dni, String uName, String rName, String pass) {
        this.idUser = id;
        this.admin = admin;
        this.dniUser = dni;
        this.userName = uName;
        this.realName = rName;
        this.pass = pass;
    }

    public User(int id, boolean admin, String dni, String uName, String rName, String pass) {
        this.idUser = id;
        if (admin) {
            this.admin = 1;
        } else {
            this.admin = 0;
        }
        this.dniUser = dni;
        this.userName = uName;
        this.realName = rName;
        this.pass = pass;
    }

    // LOGIC
    /**
     *
     *
     * @author Alejandro Olivan Alvarez
     * @return
     */
    public boolean isAdmin(){
        return (admin == 1);
    }

    //SETTERS AND GETTERS
    /**
     * Gets the ID attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @return an int containing the idUser value
     */
    public int getIdUser() {
        return idUser;
    }

    /**
     * Gets the SQL value of admin field of this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @return an int 0 or 1 for either true or false admin user
     */
    public int getAdmin() {
        return admin;
    }

    /**
     * Sets if This User instance is or not an Admin right user.
     * It performs conversion to SQL int format 1 = true, 0 = false
     *
     * @author Alejandro Olivan Alvarez
     * @param admin a boolean determining if user has admin rigths (true) or not (false)
     */
    public void setAdmin(boolean admin)
    {
        if (admin) {
            this.admin = 1;
        } else {
            this.admin = 0;
        }
    }

    /**
     * Gets the DNI attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string containing the user DNI
     */
    public String getDniUser() {
        return dniUser;
    }

    /**
     * Sets the DNI attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @param dniUser a string containing the user DNI
     */
    public void setDniUser(String dniUser) {
        this.dniUser = dniUser;
    }

    /**
     * Gets the username attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string containing the user username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the real name attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string containing the user real name
     */
    public String getRealName() {
        return realName;
    }

    /**
     * Sets the real name attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @param realName a string containing the user real name
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * Gets the password attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @return a string containing the user password
     */
    public String getPass() {
        return pass;
    }

    /**
     * Sets the password attribute for this User instance
     *
     * @author Alejandro Olivan Alvarez
     * @param pass a string containing the user password
     */
    public void setPass(String pass) {
        this.pass = pass;
    }
}
