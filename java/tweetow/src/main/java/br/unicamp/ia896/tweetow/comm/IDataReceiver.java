package br.unicamp.ia896.tweetow.comm;

import java.io.IOException;

/**
 *
 * @author dimas.oliveira
 */
public interface IDataReceiver {

    /**
     * Send a message to the attached socket.
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException;
    
    public void close();
    
}
