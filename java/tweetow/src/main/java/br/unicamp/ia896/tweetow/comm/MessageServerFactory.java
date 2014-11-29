package br.unicamp.ia896.tweetow.comm;

/**
 *
 * @author dimas.oliveira
 */
public class MessageServerFactory {
    
    private static MessageServer dummy;
        
    public static void dontUseMe(MessageServer door) {
        dummy = door;
    }
    
    public static MessageServer createBackdoor(int port) {
        if (dummy != null) {
            return dummy;
        }
        
        return new MessageServerImpl(port);
    }

    public static MessageServer createBackdoor() {
        if (dummy != null) {
            return dummy;
        }
        
        return new MessageServerImpl();
    }

}
