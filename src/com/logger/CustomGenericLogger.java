package com.logger;

import com.entity.Product;
import com.entity.Store;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class represents a generic custom logger that allows adding more functionality to it if any custom behavior will be required
 * for other types of classes.
 * If object of type Product is used as a type, then this product's change history will be saved in the TXT file.
 *
 * @param <T>
 */
public class CustomGenericLogger<T> {

    public void log(String message, Store store, T obj) {
        String fileName = "";
        String root = "D:\\StoreSystem\\";
        File file = new File(root);
        file.mkdir();

        if (obj instanceof Product) {
            Product product = (Product) obj;
            fileName = "\\" + product.getProductName() + ".txt";

            try {
                root += store.getName() + " - " + store.getAddress();
                file = new File(root);
                file.mkdir();
                FileWriter filewriter = new FileWriter(root + fileName, true);
                filewriter.write(message);
                filewriter.write("\n");
                filewriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }
}

