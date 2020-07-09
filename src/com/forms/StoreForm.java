package com.forms;

import com.dbutility.DbConnection;
import com.repository.StoreRepository;
import com.repository.StoreRepositoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import static javax.swing.JOptionPane.showMessageDialog;

public class StoreForm {
    private JLabel StoreName;
    private JTable StoreProductsTable;
    private JButton AddProductBtn;
    public JPanel StoreForm;
    private JButton RemoveBtn;
    private JButton Refresh_Btn;

    AddProductForm productForm;
    private int storeId;


    public StoreForm() {
        AddProductBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Select Products");
                productForm = new AddProductForm(storeId);
                productForm.setFrame(frame);
                productForm.PopulateTable();

            }
        });
        RemoveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = 0;
                int rowIndex = StoreProductsTable.getSelectedRow();
                int columnIndex = StoreProductsTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    float price = 0;
                    productId = (int) StoreProductsTable.getModel().getValueAt(rowIndex, 0);
                    StoreRepository storeRepository = new StoreRepositoryDao(new DbConnection().getConnection());

                    int storesProductsId = storeRepository.getProductId(storeId, productId, price);
                    if (storesProductsId != 0) {
                        storeRepository.removeProduct(storesProductsId);
                        populateTable();
                    } else {
                        showMessageDialog(null, "Product does not exist in store.");
                    }
                }
            }
        });
        Refresh_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateTable();
            }
        });
    }

    //need to modify
    public void populateTable() {
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {
            DefaultTableModel model = new DefaultTableModel(new String[]{"Id", "Product Name", "Barcode", "Price"}, 0);

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
                model.addRow(new Object[]{i, d, e, f});
            }

            resultSet.close();

            StoreProductsTable.setModel(model);
            StoreProductsTable.getColumnModel().getColumn(0).setPreferredWidth(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
}
