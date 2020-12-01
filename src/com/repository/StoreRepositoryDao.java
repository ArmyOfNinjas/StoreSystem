package com.repository;

import com.dbutility.DbConnection;
import com.entity.Product;
import com.entity.Store;
import com.logger.CustomGenericLogger;
import com.logger.CustomLogger;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Class represents a store service. Contains all necessary methods for CRUD system.
 */
public class StoreRepositoryDao implements StoreRepository {

    private Connection connection;
    private String sql;
    private Statement statement;

    public StoreRepositoryDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Saves store in the database.
     *
     * @param store
     * @return Store
     */
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
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Store not found. " + e.getMessage());
        }

        return store;
    }

    /**
     * Updates store in the database.
     *
     * @param store
     * @return Store
     */
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


    /**
     * Deletes store with specific id from the database.
     *
     * @param id
     * @return boolean
     */
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

    /**
     * Finds store in the database by the id.
     *
     * @param id
     * @return Store
     */
    @Override
    public Store get(int id) {
        Store store = null;
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

    /**
     * Finds collection of all stores in the database.
     *
     * @return Collection<Store>
     */
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

    /**
     * Saves product with specific id in the specific store and adds price.
     *
     * @param storeId
     * @param productId
     * @param price
     * @return id
     */
    @Override
    public int saveProduct(int storeId, int productId, float price) {
        sql = "INSERT INTO storesproducts (storeId, productId, price) VALUES (?,?,?)";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, storeId);
                preparedStatement.setInt(2, productId);
                preparedStatement.setFloat(3, price);
                int affectedRows = preparedStatement.executeUpdate();

                Product product = getProduct(storeId, productId, price);
                Store store = get(storeId);

                if (affectedRows == 0) throw new SQLException("Creating store-product failed, no rows affected.");
                CustomLogger.log(product.getProductName() + " - Product successfully added to the store: " + store.getName() + " on " + store.getAddress());

                CustomGenericLogger<Product> customProductLogger = new CustomGenericLogger<>();
                customProductLogger.log(product.toString() + " price = $" + price, store, product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return productId;
    }

    /**
     * Finds a specific product in the store by id.
     *
     * @param storeId
     * @param productId
     * @param price
     * @return
     */
    @Override
    public Product getProduct(int storeId, int productId, float price) {
        Product product = null;
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
            CustomLogger.log("Product not found. Product ID= " + productId + e.getMessage());
        }

        return product;
    }

    /**
     * Finds id of the product in the storesproducts table in database.
     *
     * @param storeId
     * @param productId
     * @param price
     * @return
     */
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
            CustomLogger.log("Product not found. Product ID= " + productId + e.getMessage());
        }

        return id;
    }

    /**
     * Updates product price. Uses two parallel threads for searching in database for a performance enhancement.
     *
     * @param storeId
     * @param productId
     * @param price
     * @return
     */
    @Override
    public int updateProduct(int storeId, int productId, float price) {
        sql = "UPDATE storesproducts SET price=? WHERE storeId=? AND productId=?";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setFloat(1, price);
                preparedStatement.setInt(2, storeId);
                preparedStatement.setInt(3, productId);
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 0) throw new SQLException("Creating product failed, no rows affected.");
                CustomLogger.log("Product with this ID=" + productId + " successfully updated for Store ID=" + storeId);

                final Product[] product = {null};
                final Store[] store = {null};

                Thread thread1 = new Thread(() -> product[0] = getProduct(storeId, productId, price));
                Thread thread2 = new Thread(() -> store[0] = get(storeId));

                thread1.start();
                thread2.start();

                thread1.join();
                thread2.join();

                CustomGenericLogger<Product> customProductLogger = new CustomGenericLogger<>();
                customProductLogger.log(product[0].toString() + " price = $" + price, store[0], product[0]);
            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. Product ID= " + productId + e.getMessage());
        }

        return productId;
    }


    /**
     * Removes product from the Store.
     *
     * @param id
     * @return
     */
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
            CustomLogger.log("Product not found. Product ID= " + id + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Finds all products added to the store.
     *
     * @param storeId
     * @return
     */
    @Override
    public Collection<Product> getAllProducts(int storeId) {
        List<Product> list = new ArrayList<>();
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT products.id, products.barcode, products.productName, sp.price " +
                    "FROM stores_and_products.products products \n" +
                    "INNER JOIN stores_and_products.storesproducts sp\n" +
                    "ON sp.productId = products.id\n" +
                    "WHERE sp.storeId = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, storeId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int i = resultSet.getInt("id");
                String e = resultSet.getString("barcode");
                String d = resultSet.getString("productName");
                String f = resultSet.getString("price");
                Product product = new Product(i, e, d);
                product.setPrice(Float.parseFloat(f));
                list.add(product);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}
