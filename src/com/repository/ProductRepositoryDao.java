package com.repository;

import com.entity.Product;
import com.logger.CustomLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class represents a product service. Contains all necessary methods for CRUD system.
 */
public class ProductRepositoryDao implements ProductRepository {

    private Connection connection;
    private String sql;
    private Statement statement;


    public ProductRepositoryDao(Connection connection)  {
        this.connection=connection;
    }

    /**
     * Saves product in the database.
     * @param product
     * @return Product
     */
    @Override
    public Product save(Product product) {

        if (!product.isNew()) return update(product);

        sql = "INSERT INTO products (barcode, productName) VALUES (?,?)";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, product.getBarCode());
                preparedStatement.setString(2, product.getProductName());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 0) throw new SQLException("Creating product failed, no rows affected.");
                CustomLogger.log(product.getProductName() + " - Product successfully added");
            }

            //connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return product;
    }

    /**
     * Updates product in the database.
     * @param product
     * @return Product
     */
    private Product update(Product product) {
        sql = "UPDATE products SET barcode=?, productName=? WHERE id=?";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, product.getBarCode());
                preparedStatement.setString(2, product.getProductName());
                preparedStatement.setInt(3, product.getId());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows == 0) throw new SQLException("Creating product failed, no rows affected.");
                CustomLogger.log(product.getProductName() + " - Product successfully updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return product;
    }


    /**
     * Deletes product from the database.
     * @param id
     * @return boolean
     */
    @Override
    public boolean delete(int id) {
        sql = "DELETE FROM products WHERE id=?";
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, id);

                int affectedRows = preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Finds product by id in database.
     * @param id
     * @return
     */
    @Override
    public Product get(int id) {
        Product product=null;
        sql = "SELECT * FROM products WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                product = new Product(
                        rs.getInt("id"),
                        rs.getString("barCode"),
                        rs.getString("productName")
                );
            }
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    /**
     * Finds all products in database.
     * @return
     */
    @Override
    public Collection<Product> getAll() {
        sql = "SELECT * FROM products ORDER BY ID";
        List<Product> products = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("productName"),
                        rs.getString("barCode")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            //log.warn("Product not found. {}",e.getMessage(),e);
            CustomLogger.log("Product not found. " + e.getMessage());
        }

        return products;
    }


}
