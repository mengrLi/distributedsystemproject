package service.sequencer;

import domain.Lock;
import lombok.Getter;
import service.Properties;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Singleton
 */
public class Sequencer implements Runnable{
    public static Sequencer ourInstance = new Sequencer();
    private static long nonce = 0;
    @Getter private final Lock nonceLock = new Lock();
    public final Logger log;
    private FileHandler fileHandler;

    /**
     * Constructor
     */
    private Sequencer() {
        Thread thread = new Thread(this);
        thread.start();
        log = Logger.getLogger(Sequencer.class.toString());
        initLogger();
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
        log.info("Sequencer udp Listener initiated and listen to Front end at port "+
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

    private void initLogger(){
        try {
            String dir = "src/log/sequencer_log/";
            log.setUseParentHandlers(false);
            fileHandler = new FileHandler(dir + "Sequencer.LOG", Properties.appendLog);
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            log.info("\nSequencer log loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fileHandler.close();
    }
}
