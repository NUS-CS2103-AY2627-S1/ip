package milo.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/** Tests Milo's console response formatting. */
public class UiTest {

    /** Verifies that a supplied response is printed inside Milo's standard layout. */
    @Test
    public void showResponse_message_expectedFormattedOutput() {
        Ui ui = new Ui();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            ui.showResponse("Here are the tasks I found:\n    1. [T] [ ] read book");
        } finally {
            System.setOut(originalOutput);
        }

        String response = output.toString(StandardCharsets.UTF_8);
        assertTrue(response.contains("Here are the tasks I found:"));
        assertTrue(response.contains("1. [T] [ ] read book"));
    }
}
