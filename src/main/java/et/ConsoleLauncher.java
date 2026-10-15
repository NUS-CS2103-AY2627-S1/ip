package et;

/**
 * Starts ET's legacy console interface for automated end-to-end testing.
 */
public final class ConsoleLauncher {
    /** Prevents instantiation of the console launcher. */
    private ConsoleLauncher() {
    }

    /**
     * Starts ET and reads commands from standard input.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        new ET().run();
    }
}
