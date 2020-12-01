package com.dbutility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Utility class for creating a Database connection.
 */
public class DbConnection {
    private static final String USER_NAME = "dbuser";
    private static final String PASS_WORD = "dbpassword";
    public static final String CONNECTION_STR = "jdbc:mysql://127.0.0.1:3306/stores_and_products";
    Connection connection;

    public DbConnection() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STR, USER_NAME, PASS_WORD);
        } catch (SQLException e) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE,null,e);
            e.printStackTrace();
        }
    }
        public Connection getConnection(){
            return connection;
        }
}

