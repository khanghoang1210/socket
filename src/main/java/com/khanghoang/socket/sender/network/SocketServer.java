package com.khanghoang.socket.sender.network;

import com.khanghoang.socket.sender.service.FileDistributorService;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private final int port;
    private final List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private static final int CHUNK_SIZE = 1024;
    private int clientCounter = 0;

    public SocketServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, clientCounter++);
                clientHandlers.add(handler);
                executor.execute(handler);
                System.out.println("Client connected: #" + (clientCounter - 1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void distributeFile(File file) {
        try {
            FileDistributorService.distributeFile(file, clientHandlers, CHUNK_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void waitForClients(int expectedClients) {
        while (clientCounter < expectedClients) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Waiting for clients... Currently connected: " + clientCounter);
        }
    }


    public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
}
