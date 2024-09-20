package io.basics.fileAndDir;

import java.io.*;
import java.util.Scanner;

public class DirectoryTraversalWithKeywordSearch {
    public static void main(String[] args) {
        File directory = new File("path/to/directory");
        String keyword = "search_keyword";
        listFilesWithKeyword(directory, ".txt", keyword);
    }

    public static void listFilesWithKeyword(File dir, String extension, String keyword) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    listFilesWithKeyword(file, extension, keyword);
                } else if (file.getName().endsWith(extension)) {
                    if (containsKeyword(file, keyword)) {
                        System.out.println("File: " + file.getName() + ", Path: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static boolean containsKeyword(File file, String keyword) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().contains(keyword)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
