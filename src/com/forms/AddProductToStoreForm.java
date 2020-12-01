package com.forms;

import com.dbutility.DbConnection;
import com.repository.StoreRepository;
import com.repository.StoreRepositoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static javax.swing.JOptionPane.showMessageDialog;


/**
 * Class represents a Add Product to Store form. Contains a JTable with all products available for adding to the store. Before adding the product to the store, entering price is required.
 */
public class AddProductToStoreForm {
    public JPanel addProductForm;
    private JTable productsTable;
    private JButton selectBtn;
    private JTextField priceTextBox;
    private JFrame frame;

    AddProductToStoreForm productForm;
    private int storeId;


    public AddProductToStoreForm(int storeId) {
        this.storeId = storeId;

        frame = new JFrame("Select Products");
        frame.setContentPane(this.addProductForm);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        selectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = 0;
                int rowIndex = productsTable.getSelectedRow();
                int columnIndex = productsTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    if (!priceTextBox.getText().isEmpty()) {
                        try {
                            float price = Float.parseFloat(priceTextBox.getText());
                            productId = (int) productsTable.getModel().getValueAt(rowIndex, 0);
                            StoreRepository storeRepository = new StoreRepositoryDao(new DbConnection().getConnection());

                            if (storeRepository.getProduct(storeId, productId, price) == null) {
                                storeRepository.saveProduct(storeId, productId, price);
                            } else {
                                storeRepository.updateProduct(storeId, productId, price);
                                showMessageDialog(null, "Changed the price of existing product.");
                            }
                        } catch (NumberFormatException ex) {
                            showMessageDialog(null, "Price must be a digit format.");
                        }
                    } else {
                        showMessageDialog(null, "Enter item price.");
                    }
                }
            }
        });
    }


    /**
     * Adds all products to the JTable.
     */
    public void populateTable() {
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"Id", "Product Name", "Barcode"}, 0);

            ResultSet resultSet = stmt.executeQuery("SELECT * FROM Products");
            while (resultSet.next()) {
                int i = resultSet.getInt("id");
                String d = resultSet.getString("productName");
                String e = resultSet.getString("barcode");
                model.addRow(new Object[]{i, d, e});
            }
            resultSet.close();

            productsTable.setModel(model);
            productsTable.getColumnModel().getColumn(0).setPreferredWidth(1);
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

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void setAddProductForm(AddProductToStoreForm productForm) {
        this.productForm = productForm;
    }
}
