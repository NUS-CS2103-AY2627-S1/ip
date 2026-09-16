package bobby;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Tests the console entry loop without changing persisted task data. */
public class BobbyMainTest {
    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    /** Restores process-wide console streams after each test. */
    @AfterEach
    public void restoreConsoleStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies the explicit exit command ends the console session cleanly. */
    @Test
    public void main_byeCommand_welcomeAndGoodbyeShown() {
        String output = runMain("bye\n");

        assertTrue(output.contains(Ui.WELCOME_MESSAGE));
        assertTrue(output.contains("Toodles! Bobby is off to recharge the silly batteries."));
    }

    /** Verifies an invalid command is handled and end of input triggers a clean exit. */
    @Test
    public void main_invalidCommandThenEndOfInput_errorAndGoodbyeShown() {
        String output = runMain("blah\n");

        assertTrue(output.contains("No such task type available. Try again!"));
        assertTrue(output.contains("Toodles! Bobby is off to recharge the silly batteries."));
    }

    /** Runs the console entry point with isolated input and captured output. */
    private String runMain(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        Bobby.main(new String[0]);

        return output.toString(StandardCharsets.UTF_8);
    }
}
