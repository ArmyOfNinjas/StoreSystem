package com.forms;

import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;

public class MainFormTemp extends JFrame {
    private final int WINDOW_WIDTH = 310, // Window width
     WINDOW_HEIGHT = 100; // Window height private
     JPanel panel; // To reference a panel private
     JLabel messageLabel; // To reference a label
     private JTextField kiloTextField; // To reference a text field
     private JButton calcButton; // To reference a button
     private Container c;

    public MainFormTemp(){
        c = getContentPane();
        buildPanel();   // Build the panel and add it to the frame
        c.add(panel); //Set JFrame parms:
                    // Set the title bar text, JFrame's visibility, its size, & action to performwhen close is clicked
        setTitle("Kilometer Converter");
        setVisible(true);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void buildPanel() {
        // Create a label to display instructions.
        messageLabel = new JLabel("Enter a distance in kilometers");

        // Create a text field 10 characters wide.
        kiloTextField = new JTextField(10);

        // Create a button with the caption "Calculate".
        calcButton = new JButton("Calculate");

        // Create a JPanel object and let the panel field reference it.
        panel = new JPanel();

        // Add the label, text field, and button components to the panel. panel.add(messageLabel);
        panel.add(kiloTextField);
        panel.add(calcButton);

    }
}
