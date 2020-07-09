package com.dbutility;

import java.sql.*;

public class TablesCreator {
    public static void CreateTables() throws Exception {
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {

//            stmt.executeUpdate("CREATE TABLE Stores ("
//                    + "id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, "
//                    + "name VARCHAR(255), "
//                    + "address VARCHAR(255))");
//
//            stmt.executeUpdate("CREATE TABLE Products ("
//                    + "id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, "
//                    + "barcode VARCHAR(255), "
//                    + "productName VARCHAR(255))");
//
//            stmt.executeUpdate("CREATE TABLE StoresProducts ("
//                    + "id INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, "
//                    + "storeId INTEGER, "
//                    + "productId INTEGER,"
//                    + "price DECIMAL(13,2),"
//                    + "CONSTRAINT FK_StoreId FOREIGN KEY (storeId)"
//                    + "REFERENCES Stores(id),"
//                    + "CONSTRAINT FK_ProductId FOREIGN KEY (productId)"
//                    + "REFERENCES Products(id))");
//
//            stmt.executeUpdate("INSERT INTO Products VALUES (1, '111', 'Bread')");
//            stmt.executeUpdate("INSERT INTO Products VALUES ('112', 'Eggs')");
//
//            stmt.executeUpdate("INSERT INTO Stores VALUES (1, 'Superstore', 'Raleigh street, Winnipeg')");
//            stmt.executeUpdate("INSERT INTO Stores VALUES (2, 'Costco', 'Nairn street, Winnipeg')");

//            ResultSet resultSet = stmt.executeQuery("SELECT * FROM Stores");
//            while (resultSet.next()){
//                System.out.println(resultSet.getString("name"));
//            }
//            resultSet.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
