package net.sock;

import java.io.IOException;
import java.util.Scanner;

public class Net {
    public static void main(String[] args) {
        System.out.println("Select the Echo Server implementation to run:");
        System.out.println("1: Simple Echo Server");
        System.out.println("2: Threaded Echo Server");
        System.out.println("3: ThreadPool Echo Server");
        System.out.println("4: NIO Echo Server");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                new SimpleEchoServer().start();
                break;
            case 2:
                new ThreadedEchoServer().start();
                break;
            case 3:
                new ThreadPoolEchoServer().start();
                break;
            case 4:
                try {
                    new NIOEchoServer().start();
                } catch (IOException e) {
                    System.out.println("Error starting NIO server: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Invalid choice");
        }

        scanner.close();
    }
}
