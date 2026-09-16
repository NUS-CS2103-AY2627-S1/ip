package anaconda.testutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Temporarily replaces console streams for a test and restores them even if an assertion fails.
 * Tests using this fixture must hold the SYSTEM_STREAMS resource lock.
 */
public class ConsoleSession implements AutoCloseable {
    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);

    /**
     * Supplies the given input lines to the application and begins capturing output.
     *
     * @param input Complete input, including newline separators.
     */
    public ConsoleSession(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(capturedOutput);
    }

    /**
     * Returns captured output, normalizing platform line endings only.
     *
     * @return Captured console output with LF line endings.
     */
    public String output() {
        capturedOutput.flush();
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    /**
     * Restores the process streams without closing the original console streams.
     */
    @Override
    public void close() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        capturedOutput.close();
    }
}
