package shan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ShanConsoleTest {
    @TempDir
    Path tempDir;

    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void run_multipleCommands_processesCommandsUntilExit() throws Exception {
        Path dataFile = tempDir.resolve("data.txt");
        ByteArrayOutputStream capturedOutput = redirectSystemStreams(
                "todo read book\nlist\nbye\n");

        new Shan(dataFile).run();

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hey! I'm Shan."));
        assertTrue(output.contains("I Gotchu. I've added this:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Bye! See you soon."));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(dataFile));
    }

    @Test
    void run_corruptDataAndNoCommands_displaysStartupWarning() throws Exception {
        Path dataFile = tempDir.resolve("data.txt");
        Files.writeString(dataFile, "invalid entry");
        ByteArrayOutputStream capturedOutput = redirectSystemStreams("");

        new Shan(dataFile).run();

        String output = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Warning: I skipped 1 invalid task entry in "));
    }

    private ByteArrayOutputStream redirectSystemStreams(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
        return capturedOutput;
    }
}
