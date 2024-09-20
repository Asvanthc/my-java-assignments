package io.basics.fileAndDir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopyWithoutBuffer {
    public static void main(String[] args) {
        String source = "C:\\Users\\asvanth\\IdeaProjects\\my-java-assignment\\src\\largefile.txt";
        String dest = "copy.txt";

        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {

            long startTime = System.nanoTime();

            int byteData;
            while ((byteData = fis.read()) != -1) {
                fos.write(byteData);
            }

            long endTime = System.nanoTime();
            System.out.println("Time taken without buffer: " + (endTime - startTime) / 1_000_000 + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
