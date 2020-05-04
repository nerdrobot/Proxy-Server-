package com.company;

/**
 * Hassan Rao
 * October 3, 2019
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProxyServerMT {

    /* The port number for the server */
    public static final int DEFAULT_PORT = 8080;

    /* create a thread pool for concurrency */
    private static final Executor exec = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        ServerSocket sock = null;

        try {
            sock = new ServerSocket(DEFAULT_PORT);
            System.out.println("Server started at port " + sock.getLocalPort());
            for (;;) {
                Socket client = sock.accept();
                    if(client != null) {
                        System.out.println("Processing the client socket");
                        Runnable task = new Connection(client);
                        exec.execute(task);
                    }
            }

        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
            if (sock != null)
                sock.close();
        }

    }
}
