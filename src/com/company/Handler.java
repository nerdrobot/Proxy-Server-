package com.company;

/**
 * Hassan Rao
 * October 3, 2019
 */

import java.io.*;
import java.net.*;


public class Handler {

    public static final int REMOTE_ORIGIN_PORT = 80;
    public static final int BUFFER_SIZE = 1024;

    /**
     * this method is invoked by a separate thread
     */
    public void process(Socket client) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedReader fromClient = null;
        InputStream fromOriginServer = null;
        PrintWriter toOriginServer = null;
        OutputStream toClient = null;
        Socket originSeverSocket = null;
        String completeRequest = "";
        String hostName = null;
        String resourceRequest = null;

        try {
            // Get input and output streams to talk to the client from the socket
            fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
            toClient = client.getOutputStream();

            /**
             * Read the first line from the client
             */
            String line;
            while ((line = fromClient.readLine()) != null) {
                completeRequest = line;
                break;
            }

            /**
             *  Parse out the resource and the host name
             */
            String modifiedURL = completeRequest.substring(5);
            String removedHTTP = modifiedURL.substring(0, modifiedURL.indexOf('H') - 1);

            if (removedHTTP.contains("/")) {
                hostName = removedHTTP.substring(0, modifiedURL.indexOf('/'));
                resourceRequest = removedHTTP.substring(modifiedURL.indexOf('/', 0));
            } else {
                hostName = removedHTTP;
                resourceRequest = "/";
            }

            originSeverSocket = new Socket(hostName, REMOTE_ORIGIN_PORT);
            System.out.println(originSeverSocket.isConnected());

            /**
             * Generate a request to the origin server
             */
            toOriginServer = new PrintWriter(new OutputStreamWriter(originSeverSocket.getOutputStream()));
            toOriginServer.println("GET " + resourceRequest + " HTTP/1.1");
            toOriginServer.println("Host: " + hostName);
            toOriginServer.println("Connection: close" + "\r\n");
            toOriginServer.flush();


            /**
             * Writes the response back to the client
             */

            fromOriginServer = originSeverSocket.getInputStream();
            int length;
            while ((length = fromOriginServer.read(buffer)) > 0) {
                toClient.write(buffer, 0, length);
            }

            System.out.println("Socket has received all the response! ");
            System.out.println("Socket is closing now!");
            toClient.flush();
            toClient.close();


        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
            // close streams and socket
            if (fromClient != null)
                fromClient.close();
            if (toOriginServer != null)
                toOriginServer.close();
            if (fromOriginServer != null)
                fromOriginServer.close();
            if (toClient != null)
                toClient.close();
            if (originSeverSocket != null)
                originSeverSocket.close();

        }
    }
}
