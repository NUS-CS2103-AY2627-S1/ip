package james.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
 * Unit tests for {@link Ui}.
 */
@ResourceLock("standardStreams")
public class UiTest {

    private final PrintStream standardOut = System.out;
    private final InputStream standardIn = System.in;
    private ByteArrayOutputStream outputStreamCaptor;

    /**
     * Sets up output stream redirection to capture console output before each test.
     */
    @BeforeEach
    public void setUp() {
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    /**
     * Restores the standard System.out and System.in streams after each test.
     */
    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
        System.setIn(standardIn);
    }

    // ==========================================
    // Formatting & Helper Tests
    // ==========================================

    /**
     * Tests that encaseMessage encloses the given message string between horizontal dividing lines.
     */
    @Test
    public void encaseMessage_validString_returnsMessageEnclosedInDividers() {
        Ui ui = new Ui();
        String result = ui.encaseMessage("Test message\n");
        String expected = "____________________________________________________________\n" +
                "Test message\n" +
                "____________________________________________________________";
        assertEquals(expected, result);
    }

    // ==========================================
    // Console Output Tests
    // ==========================================

    /**
     * Tests that greet prints the welcome banner and introductory message.
     */
    @Test
    public void greet_invoked_printsGreetingBanner() {
        Ui ui = new Ui();
        ui.greet();
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("James the ぱんどろぼう"));
        assertTrue(output.contains("Shh! I'm James, your bread thief."));
    }

    // ==========================================
    // Input Reading Tests
    // ==========================================

    /**
     * Tests that readCommand correctly reads input from the configured input stream.
     */
    @Test
    public void readCommand_withInput_returnsEnteredString() {
        String inputData = "todo buy apples\n";
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));

        Ui ui = new Ui();
        assertTrue(ui.hasNextCommand());
        assertEquals("todo buy apples", ui.readCommand());
        ui.close();
    }

    /**
     * Tests that hasNextCommand returns false when the input stream is empty.
     */
    @Test
    public void hasNextCommand_emptyInput_returnsFalse() {
        System.setIn(new ByteArrayInputStream(new byte[0]));

        Ui ui = new Ui();
        assertFalse(ui.hasNextCommand());
        ui.close();
    }

    /**
     * Tests that an unterminated response puts the closing divider on its own line.
     */
    @Test
    public void showResponse_missingTrailingNewline_addsNewlineBeforeDivider() {
        Ui ui = new Ui();

        ui.showResponse("Task added");

        assertEquals("____________________________________________________________\n" +
                "Task added\n" +
                "____________________________________________________________" + System.lineSeparator(),
                outputStreamCaptor.toString());
    }

    /**
     * Tests that a terminated response does not gain a blank line.
     */
    @Test
    public void showResponse_existingTrailingNewline_preservesSingleNewline() {
        Ui ui = new Ui();

        ui.showResponse("Task added\n");

        assertEquals("____________________________________________________________\n" +
                "Task added\n" +
                "____________________________________________________________" + System.lineSeparator(),
                outputStreamCaptor.toString());
    }
    @Test
    public void readCommand_emptyInput_returnsNull() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        Ui ui = new Ui();
        assertNull(ui.readCommand());
        ui.close();
    }

    @Test
    public void readCommand_blankThenUnterminatedLine_distinguishesBlankFromEof() {
        System.setIn(new ByteArrayInputStream("\nlist".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();
        assertEquals("", ui.readCommand());
        assertEquals("list", ui.readCommand());
        assertFalse(ui.hasNextCommand());
        assertNull(ui.readCommand());
        ui.close();
    }
}
