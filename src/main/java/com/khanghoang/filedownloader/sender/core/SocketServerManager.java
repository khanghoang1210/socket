package com.khanghoang.filedownloader.sender.core;

import java.net.Socket;

public interface SocketServerManager {
    void start();
    void close();
    void send(Socket client, byte[] data);
}
