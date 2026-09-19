package nori.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

/**
 * Tests what the console interface reads from and writes to the terminal.
 *
 * Standard input and standard output are replaced for the length of each test
 * and put back afterwards, so a test reads the lines it supplied and sees only
 * the output it caused.
 */
public class UiTest {
    private static final String DIVIDER =
            "    ____________________________________________________________";

    private final ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
    private PrintStream previousOut;
    private InputStream previousIn;

    @BeforeEach
    public void captureConsole() {
        previousOut = System.out;
        previousIn = System.in;
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreConsole() {
        System.setOut(previousOut);
        System.setIn(previousIn);
    }

    @Test
    public void showResponse_oneLine_wrapsItInIndentedDividers() {
        new Ui().showResponse("Noot noot!");

        assertEquals(DIVIDER + "\n     Noot noot!\n" + DIVIDER + "\n\n", captured());
    }

    @Test
    public void showResponse_severalLines_indentsEachOfThem() {
        new Ui().showResponse("first", "second", "third");

        assertEquals(DIVIDER + "\n     first\n     second\n     third\n" + DIVIDER + "\n\n",
                captured());
    }

    @Test
    public void showError_anyLines_printsThemLikeAnyOtherResponse() {
        Ui ui = new Ui();

        ui.showError("something went wrong");

        assertEquals(DIVIDER + "\n     something went wrong\n" + DIVIDER + "\n\n", captured());
    }

    @Test
    public void showWelcome_always_printsTheWordmarkAndTheGreeting() {
        new Ui().showWelcome();

        String output = captured();
        assertTrue(output.contains("Noot noot! I'm Nori, your tiny task penguin."));
        assertTrue(output.contains("Waddle in a command and I'll get flapping."));
        assertTrue(output.startsWith("  _   _  ____  _____  _____"),
                "the wordmark should come first");
    }

    @Test
    public void showHelp_always_namesEveryCommand() {
        new Ui().showHelp();

        String output = captured();
        for (String keyword : new String[] {"todo", "deadline", "event", "on", "schedule",
            "list", "find", "mark", "unmark", "delete", "help", "bye"}) {
            assertTrue(output.contains(keyword), "help should mention " + keyword);
        }
    }

    @Test
    public void showLoadingNotice_anyMessage_printsIt() {
        new Ui().showLoadingNotice("I've restored the backup.");

        assertTrue(captured().contains("I've restored the backup."));
    }

    @Test
    public void showLoadingError_anyMessage_printsIt() {
        new Ui().showLoadingError("SAD NOOT! Your saved tasks are corrupted.");

        assertTrue(captured().contains("SAD NOOT! Your saved tasks are corrupted."));
    }

    @Test
    public void readCommand_oneLine_returnsIt() {
        Ui ui = withInput("todo read book\n");

        assertEquals("todo read book", ui.readCommand());
    }

    @Test
    public void readCommand_lineWithSurroundingSpaces_trimsThem() {
        Ui ui = withInput("   list   \n");

        assertEquals("list", ui.readCommand());
    }

    @Test
    public void readCommand_severalLines_returnsThemInOrder() {
        Ui ui = withInput("first\nsecond\n");

        assertEquals("first", ui.readCommand());
        assertEquals("second", ui.readCommand());
    }

    @Test
    public void readCommand_endOfInput_returnsNull() {
        Ui ui = withInput("only line\n");
        ui.readCommand();

        assertNull(ui.readCommand());
    }

    @Test
    public void readCommand_noInputAtAll_returnsNull() {
        assertNull(withInput("").readCommand());
    }

    @Test
    public void close_afterReading_leavesTheInterfaceUsableForOutput() {
        Ui ui = withInput("list\n");
        ui.readCommand();

        ui.close();
        ui.showResponse("still speaking");

        assertTrue(captured().contains("still speaking"));
    }

    /**
     * Creates a console interface reading the supplied text instead of the terminal.
     *
     * @param input the text standard input should supply.
     * @return the console interface.
     */
    private static Ui withInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Ui();
    }

    /**
     * Returns everything written to standard output so far, with line endings normalised.
     *
     * @return the captured output.
     */
    private String captured() {
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
