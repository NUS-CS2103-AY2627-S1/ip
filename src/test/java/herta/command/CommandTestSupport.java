package herta.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Provides output capture for command tests that exercise the console UI.
 */
final class CommandTestSupport {
    private CommandTestSupport() {
        // Utility class; do not instantiate.
    }

    /** Captures standard output while a command action runs. */
    static String captureOutput(OutputAction action) throws Exception {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOutput);
        }
    }

    /** Represents a command test action that may fail during execution. */
    @FunctionalInterface
    interface OutputAction {
        void run() throws Exception;
    }
}
