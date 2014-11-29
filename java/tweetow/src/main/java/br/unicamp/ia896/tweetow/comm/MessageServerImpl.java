package br.unicamp.ia896.tweetow.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import br.unicamp.ia896.tweetow.comm.DataReceiver;
import br.unicamp.ia896.tweetow.comm.MessageServer;
import org.apache.log4j.Logger;

/**
 * This is a basic back door implementation.
 *
 * @author dimascrocco
 */
public class MessageServerImpl implements MessageServer {

    private ServerSocket server;
    private ConnectionHandler connectionHandler;
    private boolean serverIsRunning;
    private int port = 45678; // default server port
    private List<DataReceiver> connectedClients;
    private MessageServerCallback callback;

    private static final Logger LOG = Logger.getLogger(MessageServer.class);

    public MessageServerImpl() {

    }

    public MessageServerImpl(int port) {
        this.port = port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }

    public void setCallback(MessageServerCallback callback) {
        this.callback = callback;
    }

    public void start() {
        if (serverIsRunning) {
            throw new RuntimeException("Server is already running at port " + port);
        }

        connectionHandler = new ConnectionHandler();
        connectedClients = new ArrayList();

        try {
            // TODO allow use only local interface when needed
            server = new ServerSocket(port);
            serverIsRunning = true;
            new Thread(connectionHandler, "ConnectionHandler").start();
            LOG.debug("Backdoor started at port " + port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        serverIsRunning = false;
        
        for (DataReceiver dr : connectedClients) {
            dr.close();
        }
        try {
            server.close();
        } catch (IOException ex) {
            LOG.error("An error occurred while stopping the backdoor.", ex);
        }
    }

    public boolean isRunning() {
        return serverIsRunning;
    }
    
    public synchronized void sendMessage(String message) {
        List<DataReceiver> bogousClients = new ArrayList();
        for (DataReceiver dr : connectedClients) {
            try {
                dr.sendMessage(message);
            } catch (IOException ioe) {
                bogousClients.add(dr);
                LOG.warn("An error occurred while sending message: " + ioe.getMessage());
            }
        }
        
        connectedClients.removeAll(bogousClients);
    }

    public MessageServerCallback getCallback() {
        return this.callback;
    }

    class ConnectionHandler implements Runnable {

        @Override
        public void run() {
            while (serverIsRunning) {
                try {
                    LOG.debug("Waiting for connection...");
                    Socket client = server.accept();
                    connectedClients.add(new DataReceiver(MessageServerImpl.this, client)
                            .processRequest());
                } catch (IOException ioe) {
                    if (serverIsRunning) {
                        LOG.error("An error occurred while processing a request.", ioe);
                    }
                }
            }
        }
    }
    
}
