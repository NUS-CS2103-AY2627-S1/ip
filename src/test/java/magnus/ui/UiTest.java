package magnus.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the text returned and printed by the command-line user interface.
 */
public class UiTest {
    private static final String DIVIDER =
            "____________________________________________________________";

    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    public void redirectStandardOutput() {
        this.originalOutput = System.out;
        this.capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.capturedOutput, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreStandardOutput() {
        System.setOut(this.originalOutput);
    }

    @Test
    public void getBanner_returnsBannerWithoutTrailingLineBreak() {
        String banner = Ui.getBanner();

        assertTrue(banner.startsWith("███╗   ███╗"));
        assertTrue(banner.endsWith("╚══════╝"));
        assertFalse(banner.endsWith("\n"));
    }

    @Test
    public void showWelcome_printsBannerGreetingAndDividers() {
        Ui ui = new Ui();

        ui.showWelcome();

        String lineSeparator = System.lineSeparator();
        String expected = DIVIDER + lineSeparator + lineSeparator
                + Ui.getBanner() + lineSeparator
                + Ui.getGreeting() + lineSeparator
                + DIVIDER + lineSeparator + lineSeparator;
        assertEquals(expected, capturedText());
    }

    @Test
    public void printDivider_printsIndentedDividerAndBlankLine() {
        new Ui().printDivider();

        String lineSeparator = System.lineSeparator();
        assertEquals("\t" + DIVIDER + lineSeparator + lineSeparator, capturedText());
    }

    @Test
    public void showMessage_printsMessageAndLineBreak() {
        new Ui().showMessage("Your move");

        assertEquals("Your move" + System.lineSeparator(), capturedText());
    }

    /**
     * Returns the UTF-8 text written to the redirected standard output stream.
     *
     * @return Captured console output.
     */
    private String capturedText() {
        return this.capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
