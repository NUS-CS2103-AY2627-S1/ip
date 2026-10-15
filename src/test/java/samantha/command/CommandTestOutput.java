package samantha.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Captures console output emitted by a command while it is under test. */
final class CommandTestOutput {
    private CommandTestOutput() {
    }

    static String capture(ThrowingAction action) throws Exception {
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

    @FunctionalInterface
    interface ThrowingAction {
        void run() throws Exception;
    }
}
