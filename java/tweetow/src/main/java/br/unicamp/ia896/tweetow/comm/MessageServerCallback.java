package br.unicamp.ia896.tweetow.comm;

/**
 * Back door callback.
 * 
 * @author dimascrocco
 */
public interface MessageServerCallback {

    public void messageReceived(IDataReceiver source, String message);

}
