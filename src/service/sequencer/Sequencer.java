package service.sequencer;

import domain.Lock;
import lombok.Getter;
import service.Properties;

/**
 * Singleton
 */
public class Sequencer implements Runnable{
    public static Sequencer ourInstance = new Sequencer();
    @Getter private static long nonce = 0;
    @Getter private final Lock nonceLock = new Lock();

    /**
     * Constructor
     */
    private Sequencer() {
        Thread thread = new Thread(this);
        thread.start();
        System.out.println("Sequencer initialized");
    }

    /**
     * Sequencer listen to Front end request at port
     * @see Properties#SEQUENCER_LISTENING_PORT
     */
    @Override
    public void run(){
        System.out.println("Sequencer udp Listener is initiating at port " + Properties.SEQUENCER_LISTENING_PORT);

        new Thread(new SequencerUdpListener(this)).start();

        System.out.println("Sequencer udp Listener initiated and listen to Front end at port "+
                Properties.SEQUENCER_LISTENING_PORT);
    }

    /**
     * get the incremented nonce
     * @return incremented nonce
     */
    long getNonce(){
        synchronized (this.nonceLock) {
            return ++nonce;
        }
    }
}
