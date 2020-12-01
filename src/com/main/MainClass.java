package com.main;

import com.dbutility.TablesCreator;
import com.forms.MainForm;
import javax.swing.*;


/**
 * Main executable class. Calls table creator and opens the Main form.
 */
public class MainClass {
    public static void main(String[] args) {
        TablesCreator tablesCreator = new TablesCreator();
        try {
            tablesCreator.CreateTables();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainForm mainForm = new MainForm();
        JFrame frame = new JFrame("Application");
        frame.setLocationRelativeTo(null);
        frame.setContentPane(mainForm.panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        mainForm.populateTable();
    }
}
