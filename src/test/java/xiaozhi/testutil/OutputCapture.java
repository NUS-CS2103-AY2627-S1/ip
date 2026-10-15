package xiaozhi.testutil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Test-only helper that captures whatever a block of test code prints to
 * {@link System#out} while it runs, instead of letting it reach the real
 * console.
 * <p>
 * Mirrors the redirect {@code xiaozhi.XiaoZhi#getResponse} itself does for
 * the GUI, so tests can assert on exactly what a {@link xiaozhi.ui.Ui} call
 * would have shown a user.
 */
public final class OutputCapture {

    private OutputCapture() {
    }

    /**
     * A block of test code that exercises production code able to throw a
     * checked exception, e.g. {@code Command#execute}.
     *
     * @param <E> Type of checked exception the block may throw.
     */
    @FunctionalInterface
    public interface ThrowingRunnable<E extends Exception> {
        /**
         * Runs the block.
         *
         * @throws E If the production code under test throws.
         */
        void run() throws E;
    }

    /**
     * Runs {@code action}, redirecting {@link System#out} to a buffer for its
     * duration, and returns everything it printed.
     *
     * @param action The block of code to run with output capture.
     * @param <E> Type of checked exception {@code action} may throw.
     * @return Everything {@code action} printed to {@link System#out}.
     * @throws E If {@code action} throws; the real {@link System#out} is
     *         still restored first.
     */
    public static <E extends Exception> String capture(ThrowingRunnable<E> action) throws E {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString();
    }
}
