package sace;

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

/**
 * Tests the console adapter using in-memory input and output streams.
 */
class UiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;

    @BeforeEach
    void rememberSystemStreams() {
        originalInput = System.in;
        originalOutput = System.out;
    }

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void consoleInteraction_readsTrimmedCommandsAndDisplaysMessages() {
        ByteArrayInputStream input = new ByteArrayInputStream(
                "  list  \nbye\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(input);
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        Ui ui = new Ui();

        ui.showWelcome("Welcome, Summoner.");
        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        ui.showDivider();
        ui.showResponse("No quests await you here.");
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
        ui.close();

        String displayedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedText.contains("____"));
        assertTrue(displayedText.contains("Welcome, Summoner."));
        assertTrue(displayedText.contains("No quests await you here."));
        assertTrue(displayedText.contains("____________________________"));
    }
}
