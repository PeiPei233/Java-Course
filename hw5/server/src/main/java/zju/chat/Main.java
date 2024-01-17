package zju.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Config.SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Failed to start server");
            System.exit(1);
        }

        System.out.println("Server started on port " + Config.SERVER_PORT);

        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                System.out.println("Accepted connection from " + socket.getInetAddress() + ":" + socket.getPort());
                Thread thread = new Thread(new Controller(socket));
                thread.start();
            } catch (IOException e) {
                System.err.println("Failed to accept connection");
                continue;
            }
        }
    }
}