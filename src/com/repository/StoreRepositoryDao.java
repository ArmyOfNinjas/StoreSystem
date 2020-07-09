package com.repository;

import com.entity.Product;
import com.entity.Store;
import com.logger.CustomLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class StoreRepositoryDao implements StoreRepository {

    private Connection connection;
    private String sql;
    private Statement statement;


    public StoreRepositoryDao(Connection connection)  {
        this.connection=connection;
    }

    @Override
    public Store save(Store store) {

        if (!store.isNew()) return update(store);

        sql = "INSERT INTO stores (name, address) VALUES (?,?)";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, store.getAddress());
                preparedStatement.setString(2, store.getName());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 0) throw new SQLException("Creating product failed, no rows affected.");
                CustomLogger.log(store.getName() + " - Store successfully added");
            }

            //connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Store not found. " + e.getMessage());
        }

        return store;
    }

    private Store update(Store store) {
        sql = "UPDATE stores SET barcode=?, productName=? WHERE id=?";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, store.getName());
                preparedStatement.setString(2, store.getAddress());
                preparedStatement.setInt(3, store.getId());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 0) throw new SQLException("Creating store failed, no rows affected.");
                CustomLogger.log(store.getName() + " - Store successfully updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Store not found. " + e.getMessage());
        }

        return store;
    }


    @Override
    public boolean delete(int id) {
        sql = "DELETE FROM stores WHERE id=?";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, id);

                int affectedRows = preparedStatement.executeUpdate();
                CustomLogger.log(id + " - Store with this ID successfully deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Store not found. " + e.getMessage());
        }

        return true;
    }

    @Override
    public Store get(int id) {
        Store store=null;
        sql = "SELECT * FROM stores WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                store = new Store(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address")
                );
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Store not found. " + e.getMessage());
        }

        return store;
    }

    @Override
    public Collection<Store> getAll() {
        sql = "SELECT * FROM stores ORDER BY ID";
        List<Store> stores = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                stores.add(new Store(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Store not found. " + e.getMessage());
        }

        return stores;
    }

    @Override
    public int saveProduct(int storeId, int productId, float price) {
        sql = "INSERT INTO storesproducts (storeId, productId, price) VALUES (?,?,?)";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, storeId);
                preparedStatement.setInt(2, productId);
                preparedStatement.setFloat(3, price);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 0) throw new SQLException("Creating store-product failed, no rows affected.");
                CustomLogger.log(productId + " - Product with this ID successfully added");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return productId;
    }

    @Override
    public Product getProduct(int storeId, int productId, float price) {
        Product product=null;
        sql = "SELECT products.id, products.barcode, products.productName FROM stores_and_products.products products \n" +
                "INNER JOIN stores_and_products.storesproducts sp\n" +
                "ON sp.productId = products.id\n" +
                "WHERE sp.storeId = ? AND sp.productId = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, storeId);
            preparedStatement.setInt(2, productId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                product = new Product(
                        rs.getInt("id"),
                        rs.getString("barcode"),
                        rs.getString("productName")
                );
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return product;
    }

    @Override
    public int getProductId(int storeId, int productId, float price) {
        int id = 0;
        sql = "SELECT id\n" +
                "FROM stores_and_products.storesproducts  \n" +
                "WHERE storeId = ? AND productId = ?\n" +
                "limit 1";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, storeId);
            preparedStatement.setInt(2, productId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                      id = rs.getInt("id");
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return id;
    }

    @Override
    public float updateProduct(int storeId, int productId, float price) {
        sql = "UPDATE storesproducts SET price=? WHERE storeId=? AND productId=?";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setFloat(1, price);
                preparedStatement.setInt(2, storeId);
                preparedStatement.setInt(3, productId);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 0) throw new SQLException("Creating product failed, no rows affected.");
                CustomLogger.log(productId + " - Product with this ID successfully updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return price;
    }


    @Override
    public boolean removeProduct(int id) {
        sql = "DELETE FROM storesproducts WHERE id=?";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                CustomLogger.log(id + " - Product with this ID successfully deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public Collection<Product> getAllProducts() {
        return null;
    }


}
