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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import static javax.swing.JOptionPane.showMessageDialog;

public class AddProductForm {
    public JPanel AddProductForm;
    private JTable ProductsTable;
    private JButton SelectBtn;
    private JTextField PriceTextBox;

    private JFrame frame;
    private int storeId;
    AddProductForm productForm;


    public AddProductForm(int storeId) {
        this.storeId = storeId;

        frame = new JFrame("Select Products");
        frame.setContentPane(this.AddProductForm);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        SelectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = 0;
                int rowIndex = ProductsTable.getSelectedRow();
                int columnIndex = ProductsTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    if (!PriceTextBox.getText().isEmpty()){
                        float price = Float.parseFloat(PriceTextBox.getText());
                        productId = (int) ProductsTable.getModel().getValueAt(rowIndex, 0);
                        StoreRepository storeRepository = new StoreRepositoryDao(new DbConnection().getConnection());

                        if (storeRepository.getProduct(storeId, productId, price) == null){
                            storeRepository.saveProduct(storeId, productId, price);
                        } else{
                            storeRepository.updateProduct(storeId, productId, price);
                            showMessageDialog(null, "Changed the price of existing product.");
                        }
                    } else{
                        showMessageDialog(null,"Enter item price.");
                    }
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

    public void setAddProductForm(AddProductForm productForm){
        this.productForm = productForm;
    }
}
