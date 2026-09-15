package seedu.goat.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class UiTest {
    @Test
    public void input_whitespaceAndEndOfFile_handled() {
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  list  \n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();
            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(original);
        }
    }

    @Test
    public void show_multilineAndMultipleReplies_indentsEveryLine() {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            Ui ui = new Ui();
            ui.show("one\ntwo", "three");
            String newline = System.lineSeparator();
            assertTrue(output.toString(StandardCharsets.UTF_8).contains(
                    " one" + newline + " two" + newline + " three" + newline));
        } finally {
            System.setOut(original);
        }
    }
}
