package benbot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Captures standard output while a test executes an action. */
final class OutputCapture {
    private OutputCapture() {
    }

    /** Runs an action and returns the text that it writes to standard output. */
    static String capture(ThrowingRunnable action) throws Exception {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedBytes = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(capturedBytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return capturedBytes.toString(StandardCharsets.UTF_8);
    }

    /** Represents an action that can throw a checked exception. */
    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }
}
