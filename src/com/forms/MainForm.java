package com.forms;

import com.dbutility.DbConnection;
import com.entity.Store;
import com.repository.StoreRepository;
import com.repository.StoreRepositoryDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class represents a Main form. Contains a JTable with all stores in the system. Contains buttons for adding, deleting the list of stores.
 * If Manage products button is clicked - the new form will pop up with the list of all products available for adding and deleting.
 */
public class MainForm {
    private JPanel panel1;
    public JPanel panelMain;
    private JLabel StoresList;
    private JTable StoresTable;
    private JTextField txtBox_Name;
    private JButton addStoresButton;
    private JTextField txtBox_Address;
    private JButton DeleteStoresButton;
    private JButton ManageProductsBtn;
    private JButton OpenStoreBtn;


    public MainForm() {
        addStoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtBox_Address.getText().equals("") && !txtBox_Name.getText().equals("")) {
                    StoreRepository storeRepository = new StoreRepositoryDao(new DbConnection().getConnection());
                    storeRepository.save(new Store(null, txtBox_Address.getText(), txtBox_Name.getText()));
                    populateTable();
                }
            }
        });
        DeleteStoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int storeId = 0;
                int rowIndex = StoresTable.getSelectedRow();
                int columnIndex = StoresTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    storeId = (int) StoresTable.getModel().getValueAt(rowIndex, 0);
                    StoreRepository storeRepository = new StoreRepositoryDao(new DbConnection().getConnection());
                    storeRepository.delete(storeId);
                    populateTable();
                }
            }
        });
        ManageProductsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductsForm productsForm = new ProductsForm();
                JFrame frame = new JFrame("Application");
                frame.setContentPane(productsForm.panelProducts);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.pack();
                frame.setTitle("All Products");
                frame.setVisible(true);

                productsForm.initPopulateTable();
            }
        });
        OpenStoreBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String storeName = "";
                int rowIndex = StoresTable.getSelectedRow();
                int columnIndex = StoresTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    storeName = (String) StoresTable.getModel().getValueAt(rowIndex, 1);
                    int storeId = (int) StoresTable.getModel().getValueAt(rowIndex, 0);

                    StoreForm storeForm = new StoreForm();
                    storeForm.setStoreId(storeId);
                    JFrame frame = new JFrame("Application");
                    frame.setLocationRelativeTo(null);
                    frame.setContentPane(storeForm.storePanel);
                    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    frame.pack();
                    frame.setTitle(storeName);
                    frame.setVisible(true);

                    storeForm.populateTable();
                }
            }
        });
    }

    /**
     * Adds all stores to the JTable.
     */
    public void populateTable() {
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {

            DefaultTableModel model = new DefaultTableModel(new String[]{"Id", "Store Name", "Address"}, 0);
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM Stores");
            while (resultSet.next()) {
                int i = resultSet.getInt("id");
                String d = resultSet.getString("name");
                String e = resultSet.getString("address");
                model.addRow(new Object[]{i, d, e});
            }
            resultSet.close();

            StoresTable.setModel(model);
            StoresTable.getColumnModel().getColumn(0).setPreferredWidth(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
