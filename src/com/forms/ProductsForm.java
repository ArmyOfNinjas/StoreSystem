package com.forms;

import com.dbutility.DbConnection;
import com.entity.Product;
import com.repository.ProductRepository;
import com.repository.ProductRepositoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ProductsForm{
    private JTable ProductsTable;
    private JButton addProductsButton;
    public JPanel panelProducts;
    private JTextField txtBox_Name;
    private JTextField txtBox_Address;
    private JButton DeleteProductsButton;

    public ProductsForm() {
        addProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtBox_Address.getText().equals("") && !txtBox_Name.getText().equals("")){
                    ProductRepository productRepository = new ProductRepositoryDao(new DbConnection().getConnection());
                    productRepository.save(new Product(null, txtBox_Address.getText(), txtBox_Name.getText()));
                    PopulateTable();
                }
            }
        });
        DeleteProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = 0;
                int rowIndex = ProductsTable.getSelectedRow();
                int columnIndex = ProductsTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    productId = (int) ProductsTable.getModel().getValueAt(rowIndex, 0);
                    ProductRepository productRepository = new ProductRepositoryDao(new DbConnection().getConnection());
                    productRepository.delete(productId);
                    PopulateTable();
                }
            }
        });
    }



    public void PopulateTable(){
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"Id","Product Name", "Barcode"}, 0);
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM Products");
            while(resultSet.next())
            {
                int i = resultSet.getInt("id");
                String d = resultSet.getString("productName");
                String e = resultSet.getString("barcode");
                model.addRow(new Object[]{i, d, e});
            }
            resultSet.close();

            ProductsTable.setModel(model);
            ProductsTable.getColumnModel().getColumn(0).setPreferredWidth(1);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
