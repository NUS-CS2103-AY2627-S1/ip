package eve;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test-only helper that captures whatever a block of code prints to
 * {@code System.out}, so {@link Ui}/{@link eve.command.Command} output can
 * be asserted on directly instead of re-implementing this capture logic
 * (see {@link Eve#getResponse}) in every test class that needs it.
 */
public final class TestUtil {
    private TestUtil() {
        // Not meant to be instantiated: every method is static.
    }

    /**
     * Runs {@code action}, returning everything it printed to
     * {@code System.out} while it ran.
     *
     * @param action the code to run, e.g. a call to a {@code Ui.showXxx} method.
     * @throws Exception whatever {@code action} itself throws, e.g. {@link EveException}.
     */
    public static String captureStdOut(ThrowingRunnable action) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return captured.toString();
    }

    /** Like {@link Runnable}, but allowed to throw a checked exception, e.g. {@link EveException}. */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
