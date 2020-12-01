package com.logger;

import java.io.FileWriter;
import java.io.IOException;


/**
 * Class represents a custom logger. Contains single method log saved in the project root folder.
 */
public class CustomLogger {

    public static void log(String message) {
        try {
            FileWriter filewriter = new FileWriter("Log.txt", true);
            filewriter.write(message);
            filewriter.write("\n");
            filewriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

