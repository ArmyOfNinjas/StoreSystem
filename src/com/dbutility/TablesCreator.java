package com.dbutility;

import java.sql.*;

/**
 * Utility class with a single method that creates tables in the database.
 */
public class TablesCreator {
    public static void CreateTables() throws Exception {
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Stores ("
                    + "id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                    + "name VARCHAR(255), "
                    + "address VARCHAR(255))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Products ("
                    + "id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                    + "barcode VARCHAR(255), "
                    + "productName VARCHAR(255))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS StoresProducts ("
                    + "id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, "
                    + "storeId INTEGER, "
                    + "productId INTEGER,"
                    + "price DECIMAL(13,2),"
                    + "CONSTRAINT FK_StoreId FOREIGN KEY (storeId)"
                    + "REFERENCES Stores(id) ON DELETE CASCADE,"
                    + "CONSTRAINT FK_ProductId FOREIGN KEY (productId)"
                    + "REFERENCES Products(id) ON DELETE CASCADE)");
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
