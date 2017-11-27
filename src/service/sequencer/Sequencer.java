package service.sequencer;

/**
 * Singleton
 */
public class Sequencer {
    public static Sequencer ourInstance = new Sequencer();
    private static long nonce = 0;

    private Sequencer() {
    }
}
