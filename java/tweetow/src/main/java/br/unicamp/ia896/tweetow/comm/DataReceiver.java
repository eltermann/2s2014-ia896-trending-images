package br.unicamp.ia896.tweetow.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * Process the client connection. Incoming data will be redirected to the associated
 * message server callback. Data can be sent back to client using the sendMessage method.
 *
 * @author dimascrocco
 */
public class DataReceiver implements Runnable, IDataReceiver {

        private final Socket client;
        private BufferedWriter out;
        private BufferedReader input;
        boolean closing = false;
        private MessageServer server;

        private static final Logger LOG = Logger.getLogger(DataReceiver.class);

        /**
         * Creates a new DataReceiver instance.
         *
         * @param server
         * @param socket
         * @throws IOException
         */
        public DataReceiver(MessageServer server, Socket socket) throws IOException {
            client = socket;
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.server = server;
        }

        /**
         * Starts the process thread.
         *
         * @return
         */
        public DataReceiver processRequest() {
            new Thread(this, "DataReceiver").start();
            return this;
        }

        /**
         * Send a message to the attached socket.
         *
         * @param message
         * @throws IOException
         */
    @Override
        public void sendMessage(String message) throws IOException {
            out.write(message);
            out.newLine();
            out.flush();
        }

        /**
         * Close all the communication channels.
         */
        public void close() {
            closing = true;
            try { out.close(); } catch (IOException ignored) { }
            try { input.close(); } catch (IOException ignored) { }
            try { client.close(); } catch (IOException ignored) { }
            LOG.debug("Client communication channel closed.");
        }

        /**
         * Notify callback when a message is received.
         */
        @Override
        public void run() {
            String data = null;
            try {
                while ((data = input.readLine()) != null) {
                    MessageServerCallback callback = server.getCallback();
                    if (callback != null) {
                        callback.messageReceived(this, data);
                    }
                }
            } catch (IOException ioe) {
                if (!closing) {
                    LOG.error("An error occurred while reading data:" + ioe.getMessage());
                }
            }
        }
    }
