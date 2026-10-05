package gatsby.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Tests Gatsby's console input and output wrapper. */
class UiTest {
    private final java.io.InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @AfterEach
    void restoreConsoleStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that input availability and line reading preserve user input. */
    @Test
    void hasNextLineAndReadLine_inputAvailable_returnsNextLine() {
        System.setIn(new ByteArrayInputStream("todo read book\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertTrue(ui.hasNextLine());
        assertEquals("todo read book", ui.readLine());
        assertFalse(ui.hasNextLine());
        ui.close();
    }

    /** Verifies the welcome banner and the small output helpers. */
    @Test
    void outputMethods_printExpectedMessages() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        Ui ui = new Ui();

        ui.showWelcome();
        ui.showSeparator();
        ui.printLine("line");
        ui.print("partial");

        String printed = output.toString(StandardCharsets.UTF_8);
        assertTrue(printed.contains("*              Gatsby              *"));
        assertTrue(printed.contains("Wassup! I'm Gatsby."));
        assertTrue(printed.contains(Ui.getStartingJoke()));
        assertTrue(printed.contains("____________________________________________________________"));
        assertTrue(printed.contains("line" + System.lineSeparator()));
        assertTrue(printed.endsWith("partial"));
        ui.close();
    }
}
