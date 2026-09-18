package lloyd;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests the console input and output of the Lloyd application.
 */
public class LloydTest {

    /** Verifies that the application prints both its greeting and farewell. */
    @Test
    public void main_byeCommand_printsGreetingAndFarewell() {
        String output = runLloyd("bye\n");

        assertTrue(output.contains("Lloyd Frontera"));
        assertTrue(output.contains("Goodbye! Your tasks will be waiting."));
    }

    /** Verifies that an unknown command produces the guidance message. */
    @Test
    public void main_unknownCommand_printsErrorMessage() {
        String output = runLloyd("hello\nbye\n");

        assertTrue(output.contains(
                "Enter a valid command."));
    }

    /**
     * Runs Lloyd with simulated user input and returns everything it prints.
     *
     * @param input Commands to send to Lloyd.
     * @return Console output produced by Lloyd.
     */
    private String runLloyd(String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayInputStream testInput = new ByteArrayInputStream(
                input.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();

        try {
            System.setIn(testInput);
            System.setOut(new PrintStream(
                    capturedOutput, true, StandardCharsets.UTF_8));

            Lloyd.main(new String[0]);
            return capturedOutput.toString(StandardCharsets.UTF_8);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
