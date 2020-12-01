package com.forms;

import com.dbutility.DbConnection;
import com.entity.Product;
import com.repository.ProductRepository;
import com.repository.ProductRepositoryDao;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class represents a Products form. Contains a JTable with all products available in the system and buttons for adding, deleting and sorting the list of products.
 */
public class ProductsForm {
    public JPanel panelProducts;
    public JTable productsTable;
    private JTextField txtBox_Name;
    private JTextField txtBox_Barcode;
    private JButton addProductsButton;
    private JButton deleteProductsButton;
    private JButton sortByProductNameButton;
    private JButton sortByBarcodeButton;
    private JTextField txtBox_Search;
    private JButton refreshBtn;

    private DefaultTableModel model;

    private boolean isSortedByName;
    private boolean isSortedByBarcode;

    public ProductsForm() {
        addProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtBox_Barcode.getText().equals("") && !txtBox_Name.getText().equals("")) {
                    ProductRepository productRepository = new ProductRepositoryDao(new DbConnection().getConnection());
                    productRepository.save(new Product(null, txtBox_Barcode.getText(), txtBox_Name.getText()));
                    initPopulateTable();
                    productsTable.setRowSorter(null);
                }
            }
        });
        deleteProductsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int productId = 0;
                int rowIndex = productsTable.getSelectedRow();
                int columnIndex = productsTable.getSelectedColumn();
                if (rowIndex != -1 && columnIndex != -1) {
                    productId = (int) productsTable.getModel().getValueAt(rowIndex, 0);
                    ProductRepository productRepository = new ProductRepositoryDao(new DbConnection().getConnection());
                    productRepository.delete(productId);
                    initPopulateTable();
                    productsTable.setRowSorter(null);
                }
            }
        });
        sortByProductNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSortedByName == false) {
                    Vector data = model.getDataVector();
                    Collections.sort(data, new ColumnSorter(1));
                    model.fireTableStructureChanged();
                    isSortedByName = true;
                    isSortedByBarcode = false;
                } else {
                    Vector data = model.getDataVector();
                    Collections.reverse(data);
                    model.fireTableStructureChanged();
                    isSortedByName = false;
                    isSortedByBarcode = false;
                }
            }
        });
        sortByBarcodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isSortedByBarcode == false) {
                    Vector data = model.getDataVector();
                    Collections.sort(data, new ColumnSorter(2));
                    model.fireTableStructureChanged();
                    isSortedByName = false;
                    isSortedByBarcode = true;
                } else {
                    Vector data = model.getDataVector();
                    Collections.reverse(data);
                    model.fireTableStructureChanged();
                    isSortedByName = false;
                    isSortedByBarcode = false;
                }
            }
        });
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initPopulateTable();
            }
        });

        initPopulateTable();


        txtBox_Search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(txtBox_Search.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(txtBox_Search.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(txtBox_Search.getText());
            }

            public void search(String str) {
                TableRowSorter sorter = new TableRowSorter<>(model);
                productsTable.setRowSorter(sorter);
                sorter.setSortsOnUpdates(true);
                if (str.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(str));
                }
            }
        });

    }


    /**
     * Inner class for table comparison of Vector objects is used for sorting by ProductName and by Barcode. Implements Comparator interface.
     */
    static class ColumnSorter implements Comparator {
        int colIndex;

        ColumnSorter(int colIndex) {
            this.colIndex = colIndex;
        }

        public int compare(Object a, Object b) {
            Vector v1 = (Vector) a;
            Vector v2 = (Vector) b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);

            if (o1 instanceof String && ((String) o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String) o2).length() == 0) {
                o2 = null;
            }

            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {

                return ((Comparable) o1).compareTo(o2);
            } else {

                return o1.toString().compareTo(o2.toString());
            }
        }
    }


    /**
     * Adds all products to the JTable and sorts all rows in the table by the product name using parallel stream sort.
     * Also uses lambda expression.
     */
    public void initPopulateTable() {
        try (Connection conn = new DbConnection().getConnection();
             Statement stmt = conn.createStatement()) {
            productsTable.setAutoCreateColumnsFromModel(true);
            model = new DefaultTableModel(new String[]{"Id", "Product Name", "Barcode"}, 0);
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM Products");

            List<Object[]> list = new ArrayList<>();
            while (resultSet.next()) {
                int i = resultSet.getInt("id");
                String p = resultSet.getString("productName");
                String b = resultSet.getString("barcode");
                list.add(new Object[]{i, p, b});
            }
            resultSet.close();

            list = list.parallelStream()
                    .sorted(new InitColumnSorter(1))
                    .collect(Collectors.toList());

            list.forEach(x -> model.addRow(x));

            productsTable.setModel(model);
            productsTable.getColumnModel().getColumn(0).setPreferredWidth(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Inner class for initial table comparison. Implements Comparator<> interface.
     */
    static class InitColumnSorter implements Comparator<Object[]> {
        int colIndex;

        InitColumnSorter(int colIndex) {
            this.colIndex = colIndex;
        }

        @Override
        public int compare(Object[] thisRow, Object[] otherRow) {
            return thisRow[colIndex].toString().compareTo(otherRow[colIndex].toString());
        }
    }

}
