package potatobot.ui.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Checks console formatting and input handling while restoring the real system
 * streams after each test.
 */
@ResourceLock("SYSTEM_STREAMS")
public class ConsoleUiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream output;
    private PrintStream capturedOutput;

    @BeforeEach
    public void setUp() {
        originalInput = System.in;
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8);
        System.setOut(capturedOutput);
        System.setIn(new ByteArrayInputStream("todo read book\n\nbye".getBytes(StandardCharsets.UTF_8)));
    }

    @AfterEach
    public void tearDown() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
        capturedOutput.close();
    }

    @Test
    public void readCommand_linesAndEndOfInput_preservesInputAndReturnsNull() {
        try (ConsoleUi ui = new ConsoleUi()) {
            assertEquals("todo read book", ui.readCommand());
            assertEquals("", ui.readCommand());
            assertEquals("bye", ui.readCommand());
            assertNull(ui.readCommand());
        }
        assertEquals("Me: Me: Me: Me: ", output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void showMessage_multilineReply_indentsEveryLine() {
        try (ConsoleUi ui = new ConsoleUi()) {
            ui.showMessage("first\nsecond");
            ui.showBlankLine();
        }
        assertEquals("PotatoBot:\n  first\n  second\n\n\n", normalizedOutput());
    }

    @Test
    public void showGreetingAndFarewell_displaysBannerInstructionsAndSeparators() {
        try (ConsoleUi ui = new ConsoleUi()) {
            ui.showGreeting();
            String greeting = normalizedOutput();
            assertTrue(greeting.startsWith("=".repeat(80) + "\n"));
            assertTrue(greeting.contains("|  __/ (_)"));
            assertTrue(greeting.contains("Hello! I'm PotatoBot, your trusty spud assistant.\n"));
            assertTrue(greeting.contains("(Say \"bye\" if you want me to leave you alone)\n"));
            assertTrue(greeting.endsWith("=".repeat(80) + "\n\n"));
            output.reset();
            ui.showFarewell();
            assertEquals("=".repeat(80) + "\n"
                    + "Bye. I'm rolling back to the potato patch. Hope to see you again soon!\n"
                    + "=".repeat(80) + "\n", normalizedOutput());
        }
    }

    @Test
    public void close_openScanner_preventsFurtherReads() {
        ConsoleUi ui = new ConsoleUi();
        ui.close();
        assertThrows(IllegalStateException.class, ui::readCommand);
    }

    /**
     * Normalizes platform line endings in captured console output.
     *
     * @return Captured text using newline characters only.
     */
    private String normalizedOutput() {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
