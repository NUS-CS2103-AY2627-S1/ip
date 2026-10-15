package luckynoslacky.luckyui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Tests command-line input and output handled by LuckyNoCli.
 */
class LuckyNoCliTest {
    private static final String DIVIDER =
            "  ____________________________________________________________\n";

    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /** Redirects standard output before each CLI test. */
    @BeforeEach
    void setUp() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();

        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    /** Restores standard input and output after each CLI test. */
    @AfterEach
    void tearDown() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that commands are read until standard input is exhausted. */
    @Test
    void readCommand_inputLinesUntilExhausted_returnsEachCommand() {
        LuckyNoCli cli = createCliWithInput("todo read book\nbye\n");

        assertTrue(cli.hasNextLine());
        assertEquals("todo read book", cli.readCommand());
        assertTrue(cli.hasNextLine());
        assertEquals("bye", cli.readCommand());
        assertFalse(cli.hasNextLine());
    }

    /** Verifies standard divider and indentation formatting for replies. */
    @Test
    void showReply_multipleMessageParts_joinsWithNewlines() {
        LuckyNoCli cli = createCliWithInput("");

        cli.showReply("first line", "second line");

        assertEquals(
                expectedReply("first line\nsecond line"),
                capturedOutput.toString(StandardCharsets.UTF_8));
    }

    /** Verifies that emoji task text is written as UTF-8 despite a legacy console. */
    @Test
    void showReply_emojiTaskTextWithLegacyConsole_writesUtf8Bytes() {
        System.setOut(new PrintStream(
                capturedOutput, true, StandardCharsets.ISO_8859_1));
        LuckyNoCli cli = createCliWithInput("");
        String taskText = "[📌][❗] read book";

        cli.showReply(taskText);

        assertArrayEquals(
                expectedReply(taskText).getBytes(StandardCharsets.UTF_8),
                capturedOutput.toByteArray());
    }

    /** Verifies that the banner and greeting are displayed together. */
    @Test
    void showGreeting_noInput_displaysBannerAndGreeting() {
        LuckyNoCli cli = createCliWithInput("");

        cli.showGreeting();

        assertEquals(
                DIVIDER + LuckyNoQuips.banner() + "\n"
                        + expectedReply(LuckyNoQuips.greeting()),
                capturedOutput.toString(StandardCharsets.UTF_8));
    }

    /** Verifies goodbye, loading, and saving messages use reply formatting. */
    @Test
    void showGoodbyeAndStorageErrors_configuredMessages_displaysReplies() {
        LuckyNoCli cli = createCliWithInput("");

        cli.showGoodbye();
        cli.showLoadingError();
        cli.showSavingError();

        assertEquals(
                expectedReply(LuckyNoQuips.goodbye())
                        + expectedReply(LuckyNoQuips.loadErrorMessage())
                        + expectedReply(LuckyNoQuips.saveErrorMessage()),
                capturedOutput.toString(StandardCharsets.UTF_8));
    }

    /** Verifies configuration errors use standard reply formatting. */
    @Test
    void showConfigurationError_configuredMessage_displaysReply() {
        LuckyNoCli cli = createCliWithInput("");

        cli.showConfigurationError();

        assertEquals(
                expectedReply(LuckyNoQuips.configurationErrorMessage()),
                capturedOutput.toString(StandardCharsets.UTF_8));
    }

    /** Verifies that echoed input uses standard reply formatting. */
    @Test
    void echo_input_usesStandardReplyFormatting() {
        LuckyNoCli cli = createCliWithInput("");

        cli.echo("hello LuckyNoSlacky");

        assertEquals(
                expectedReply("hello LuckyNoSlacky"),
                capturedOutput.toString(StandardCharsets.UTF_8));
    }

    /** Creates a CLI connected to the supplied in-memory input. */
    private LuckyNoCli createCliWithInput(String userInput) {
        System.setIn(new ByteArrayInputStream(
                userInput.getBytes(StandardCharsets.UTF_8)));
        return new LuckyNoCli();
    }

    /** Builds the exact formatted output expected from the CLI. */
    private String expectedReply(String output) {
        String indentedOutput = output.replace("\n", "\n  ");
        return DIVIDER + "  " + indentedOutput + "\n" + DIVIDER;
    }
}
