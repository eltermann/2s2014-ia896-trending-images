package br.unicamp.ia896.tweetow.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Easy to use client socket.
 * 
 * @author dimas.oliveira
 */
public class ClientSocket {
   
    public static final int DEFAULT_TIMEOUT = 2000;
    protected Socket socket;
    protected BufferedWriter out;
    protected BufferedReader in;
    protected String address;
    protected int port;
    protected int timeout;
    protected boolean connected;

    /**
     * Creates a new client socket.
     * 
     * @param address
     * @param port
     * @param timeout socket timeout in milliseconds
     */
    public ClientSocket(String address, int port, int timeout) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Creates a new client socket with a default timeout (2 seconds).
     * 
     * @param address
     * @param port
     */
    public ClientSocket(String address, int port) {
        this(address, port, DEFAULT_TIMEOUT);
    }

    public void connect() throws IOException {
        socket = new Socket(address, port);
        socket.setKeepAlive(true);

        if (timeout > 0) {
            socket.setSoTimeout(timeout);
        }
        
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.connected = true;
    }

    public void disconnect() {
        try { out.close(); } catch (Exception ignored) {}
        try { in.close(); } catch (Exception ignored) {}
        try { socket.close(); } catch (Exception ignored) {}

        this.connected = false;
    }

    /**
     * Write informed data to the output stream.
     * 
     * @param data
     *        data to be written
     * @throws IOException
     */
    public void write(String data) throws IOException {
        out.write(data);
        out.newLine();
        out.flush();
    }

    /**
     * Read a line from the input stream.
     * 
     * @return data read or null after a socket timeout
     * @throws IOException
     */
    public String read() throws IOException {
        String response = null;
        try {
            response = in.readLine();
        } catch (SocketTimeoutException e) {
            System.err.println("* read timeout");
        }
        
        return response;
    }

    /**
     * Tells if the device is connected or not.
     * 
     * @return true if the device is connected, otherwise returns false
     */
    public boolean isConnected() {
        return this.connected;
    }
    
}
