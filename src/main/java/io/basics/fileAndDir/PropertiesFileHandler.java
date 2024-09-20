package io.basics.fileAndDir;

import java.io.*;
import java.util.Properties;

public class PropertiesFileHandler {
    public static void main(String[] args) {
        Properties properties = new Properties();

        // Load properties file
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            properties.forEach((key, value) -> System.out.println(key + ": " + value));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Modify properties
        properties.setProperty("newKey", "newValue");

        // Save changes back to properties file
        try (FileOutputStream fos = new FileOutputStream("config.properties")) {
            properties.store(fos, "Updated Properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
