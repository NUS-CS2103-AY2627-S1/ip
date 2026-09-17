package panda.lifecycle;

/**
 * Provides operations used while Panda exits normally.
 */
public final class TerminationManager {

    private TerminationManager() {
        // Utility class: prevent accidental instantiation.
    }

    /**
     * Terminates Panda normally.
     */
    public static void terminate() {
        System.exit(0);
    }

}
