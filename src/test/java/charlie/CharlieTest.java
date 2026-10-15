package charlie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CharlieTest {
    private static final String GREETING =
            "Oh, hello! I'm Charlie.\nReady to tackle some tasks together?";

    @Test
    public void getGreeting_missingSaveFile_returnsGreeting(@TempDir Path temporaryDirectory) {
        Charlie charlie = new Charlie(temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(GREETING, charlie.getGreeting());
    }

    @Test
    public void getGreeting_invalidSaveFile_returnsGreetingWithLoadingError(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Files.writeString(saveFile, "D | Not done | missing date");
        Charlie charlie = new Charlie(saveFile.toString());

        assertEquals(
                GREETING + "\nError loading saved tasks: Invalid number of fields in saved task.",
                charlie.getGreeting());
        assertEquals("Your task nest is empty.", charlie.getResponse("list"));
    }

    @Test
    public void getResponse_sequenceOfCommands_updatesAndPersistsTasks(
            @TempDir Path temporaryDirectory) throws IOException {
        Path saveFile = temporaryDirectory.resolve("charlie.txt");
        Charlie charlie = new Charlie(saveFile.toString());

        assertEquals(
                "A happy little roar! I've tucked this task safely into the nest:"
                        + System.lineSeparator()
                        + "  [T][ ] borrow book" + System.lineSeparator()
                        + "There is now 1 task in our nest.",
                charlie.getResponse("todo borrow book"));
        assertEquals(
                "Tiny victory roar! This task is done:" + System.lineSeparator()
                        + "  [T][X] borrow book",
                charlie.getResponse("mark 1"));
        assertEquals("T | Done | borrow book" + System.lineSeparator(),
                Files.readString(saveFile));
    }

    @Test
    public void getResponse_invalidThenExit_updatesExitSignal(@TempDir Path temporaryDirectory) {
        Charlie charlie = new Charlie(temporaryDirectory.resolve("charlie.txt").toString());

        assertEquals(
                "Bye for now! I'll guard the task nest until you return.",
                charlie.getResponse("bye"));
        assertTrue(charlie.isExitRequested());

        assertEquals("Oh! I don't recognize that command yet.", charlie.getResponse("hello"));
        assertFalse(charlie.isExitRequested());
    }

    @Test
    public void run_listAndExit_processesConsoleCommands(@TempDir Path temporaryDirectory) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        String commands = "list" + System.lineSeparator() + "bye" + System.lineSeparator();
        ByteArrayInputStream testInput = new ByteArrayInputStream(
                commands.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();

        try {
            System.setIn(testInput);
            System.setOut(new PrintStream(testOutput, true, StandardCharsets.UTF_8));
            Charlie charlie = new Charlie(temporaryDirectory.resolve("charlie.txt").toString());

            charlie.run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Oh, hello! I'm Charlie."));
        assertTrue(output.contains("Your task nest is empty."));
        assertTrue(output.contains("Bye for now! I'll guard the task nest until you return."));
    }
}
