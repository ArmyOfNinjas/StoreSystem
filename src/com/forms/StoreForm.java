package com.forms;

import com.dbutility.DbConnection;
import com.entity.Product;
import com.repository.StoreRepository;
import com.repository.StoreRepositoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Collection;

import static javax.swing.JOptionPane.showMessageDialog;


/**
 * Class represents a Store form. Contains a JTable with all products listed in the specific store. Contains buttons for adding, deleting the list of products.
 * If AddProduct button is clicked - the new form will pop up with the list of all products available for adding.
 */
public class StoreForm {
    public JPanel storePanel;
    private JLabel storeName;
    private JTable storeProductsTable;
    private JButton addProductBtn;
    private JButton removeBtn;
    private JButton refresh_Btn;

    AddProductToStoreForm productForm;
    private int storeId;

    public StoreForm() {
        addProductBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Select Products");
                productForm = new AddProductToStoreForm(storeId);
                productForm.setFrame(frame);
                productForm.populateTable();

            }
        });
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = 0;
                int rowIndex = storeProductsTable.getSelectedRow();
                int columnIndex = storeProductsTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    float price = 0;
                    productId = (int) storeProductsTable.getModel().getValueAt(rowIndex, 0);
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
        refresh_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateTable();
            }
        });
    }

    /**
     * Adds all products to the JTable with the price for each product.
     */
    public void populateTable() {

        try (Connection conn = new DbConnection().getConnection()) {
            StoreRepository storeRepository = new StoreRepositoryDao(conn);
            Collection<Product> list = storeRepository.getAllProducts(storeId);
            DefaultTableModel model = new DefaultTableModel(new String[]{"Id", "Product Name", "Barcode", "Price"}, 0);

            for (Product product : list) {
                int i = product.getId();
                String e = product.getBarCode();
                String d = product.getProductName();
                String f = "" + product.getPrice();
                model.addRow(new Object[]{i, d, e, f});
            }

            storeProductsTable.setModel(model);
            storeProductsTable.getColumnModel().getColumn(0).setPreferredWidth(1);
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
