package sace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the complete console command loop independently of JavaFX.
 */
class SaceConsoleTest {
    @TempDir
    Path tempDir;

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
    void run_commandThenEndOfInput_displaysResponsesAndPersistsTask()
            throws SaceException {
        ByteArrayInputStream input = new ByteArrayInputStream(
                "todo console quest\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(input);
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        Path dataFile = tempDir.resolve("sace.txt");

        new Sace(dataFile).run();

        String displayedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedText.contains("Welcome, Summoner"));
        assertTrue(displayedText.contains("[T][ ] console quest"));
        assertTrue(displayedText.contains("The battle rests for now"));
        List<Task> savedTasks = new Storage(dataFile).load();
        assertEquals(1, savedTasks.size());
        assertEquals("console quest", savedTasks.get(0).getDescription());
    }
}
