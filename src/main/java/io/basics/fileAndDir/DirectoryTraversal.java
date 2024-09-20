package io.basics.fileAndDir;

import java.io.File;

public class DirectoryTraversal {
    public static void main(String[] args) {
        File directory = new File("path/to/directory");
        listFilesWithExtension(directory, ".txt");
    }

    public static void listFilesWithExtension(File dir, String extension) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    listFilesWithExtension(file, extension);
                } else if (file.getName().endsWith(extension)) {
                    System.out.println("File: " + file.getName() + ", Path: " + file.getAbsolutePath());
                }
            }
        }
    }
}
