package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the terminal session from end to end.
 *
 * run() was the least covered method in the program, which is awkward given it
 * is the whole of the terminal app: the loop, when it stops, and the order
 * things are printed in. It looks untestable because it reads standard input,
 * but standard input can be handed to it, which is exactly what text-ui-test
 * does from the outside. Doing it here as well means a break is reported
 * against one named case rather than as a diff of the whole session.
 */
public class GoatRunTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /** Runs a whole session over the given typed lines and returns what was printed. */
    private static String runWith(Path folder, String... lines) {
        String typed = String.join(System.lineSeparator(), lines) + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        new Goat(folder.resolve("goat.txt").toString()).run();
        System.out.flush();
        return captured.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void run_bye_greetsThenSaysGoodbye(@TempDir Path folder) {
        String output = runWith(folder, "bye");

        assertTrue(output.contains("Goat here."), output);
        assertTrue(output.contains("Off up the hill."), output);
        // The greeting has to come first, or a complaint about the save file
        // would be the first thing a user ever sees.
        assertTrue(output.indexOf("Goat here.") < output.indexOf("Off up the hill."), output);
    }

    @Test
    public void run_inputRunsOutWithoutBye_stillSaysGoodbye(@TempDir Path folder) {
        // Commands piped from a file often end without one.
        String output = runWith(folder, "list");

        assertTrue(output.contains("Off up the hill."), output);
    }

    @Test
    public void run_commandsAfterBye_ignored(@TempDir Path folder) {
        String output = runWith(folder, "bye", "todo should not appear");

        assertFalse(output.contains("should not appear"), output);
    }

    @Test
    public void run_severalCommands_carriedOutInOrder(@TempDir Path folder) {
        String output = runWith(folder, "todo first", "todo second", "list", "bye");

        int first = output.indexOf("1.[T][ ] first");
        int second = output.indexOf("2.[T][ ] second");
        assertTrue(first > 0 && second > first, output);
    }

    @Test
    public void run_badCommand_doesNotEndTheSession(@TempDir Path folder) {
        // The whole point of catching inside the loop.
        String output = runWith(folder, "blah", "todo still working", "bye");

        assertTrue(output.contains("Hm. I do not know that one"), output);
        assertTrue(output.contains("still working"), output);
    }

    @Test
    public void run_blankLine_reportedAndSessionContinues(@TempDir Path folder) {
        String output = runWith(folder, "", "todo after a blank", "bye");

        assertTrue(output.contains("after a blank"), output);
    }

    @Test
    public void run_changes_reachTheSaveFile(@TempDir Path folder) throws Exception {
        runWith(folder, "todo read book", "bye");

        assertEquals("T | 0 | read book",
                Files.readString(folder.resolve("goat.txt")).strip());
    }

    @Test
    public void run_secondSession_seesWhatTheFirstSaved(@TempDir Path folder) {
        runWith(folder, "todo read book", "bye");

        String output = runWith(folder, "list", "bye");

        assertTrue(output.contains("1.[T][ ] read book"), output);
    }

    @Test
    public void run_damagedSaveFile_warnsAndStartsEmpty(@TempDir Path folder) throws Exception {
        Files.writeString(folder.resolve("goat.txt"), "D | 0 | x | 2 Dec 2019" + System.lineSeparator());

        String output = runWith(folder, "list", "bye");

        assertTrue(output.contains("damaged on line 1"), output);
        // Warned, but still usable rather than refusing to start.
        assertTrue(output.contains("Nothing on the list"), output);
    }

    @Test
    public void run_damagedSaveFile_nextChangeOverwritesIt(@TempDir Path folder) throws Exception {
        Path file = folder.resolve("goat.txt");
        Files.writeString(file, "D | 0 | x | 2 Dec 2019" + System.lineSeparator());

        runWith(folder, "todo fresh start", "bye");

        // This is what the warning is warning about, so it is worth pinning.
        assertEquals("T | 0 | fresh start", Files.readString(file).strip());
    }

    @Test
    public void run_everyReplyIsWrappedInDividers(@TempDir Path folder) {
        String output = runWith(folder, "list", "bye");

        String divider = "_".repeat(60);
        // Greeting, the list, and the farewell: three replies, two rules each.
        assertEquals(6, output.split(divider, -1).length - 1, output);
    }
}
