package duchess;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests the command-line entry point using isolated standard streams. */
public class DuchessMainTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /** Replaces process streams with isolated in-memory streams. */
    @BeforeEach
    public void setUpStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    /** Restores process streams so this test class cannot affect other tests. */
    @AfterEach
    public void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that the CLI welcomes the user, handles bye, and stops reading. */
    @Test
    public void main_byeThenExtraInput_printsGoodbyeAndStops() {
        System.setIn(new ByteArrayInputStream("bye\nnot a command\n".getBytes(StandardCharsets.UTF_8)));

        Duchess.main(new String[0]);

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello! I'm Duchess."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertFalse(output.contains("I don't know what that means"));
    }

    /** Verifies that end-of-input after the welcome message exits cleanly. */
    @Test
    public void main_emptyInput_printsWelcomeAndReturns() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        Duchess.main(new String[0]);

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hello! I'm Duchess."));
        assertFalse(output.contains("Bye. Hope to see you again soon!"));
    }
}
