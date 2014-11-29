package br.unicamp.ia896.tweetow.comm;

/**
 * This interface describes a basic server socket component.
 *
 * @author dimascrocco
 */
public interface MessageServer {

    public void setCallback(MessageServerCallback callback);
    
    public void start();

    public void stop();

    public boolean isRunning();
    
    public void setPort(int port);
    
    public void sendMessage(String message);

    public MessageServerCallback getCallback();
    
}
