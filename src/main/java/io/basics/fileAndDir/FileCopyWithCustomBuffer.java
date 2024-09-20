package io.basics.fileAndDir;

import java.io.*;

public class FileCopyWithCustomBuffer {
    public static void main(String[] args) {
        String source = "C:\\Users\\asvanth\\IdeaProjects\\my-java-assignment\\src\\largefile.txt";
        String dest = "copy.txt";
        int bufferSize = 8192; // Experiment with different sizes

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source), bufferSize);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest), bufferSize)) {

            long startTime = System.nanoTime();

            int byteData;
            while ((byteData = bis.read()) != -1) {
                bos.write(byteData);
            }

            long endTime = System.nanoTime();
            System.out.println("Time taken with buffer size " + bufferSize + ": " + (endTime - startTime) / 1_000_000 + " ms");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
