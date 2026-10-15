package cooper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises complete command flows through Cooper's text interface. */
public class CooperSmokeTest {
    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @TempDir
    private Path temporaryDirectory;

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void getResponse_errorStatus_doesNotAffectMessagesOrExit() {
        Cooper cooper = new Cooper(temporaryDirectory.resolve("cooper.txt").toString());
        CommandResult error = cooper.getResponse("mark");
        assertTrue(error.isError());
        assertFalse(error.shouldExit());
        assertEquals("Let's check those coordinates. Use: mark <task-number>",
                error.message());
        assertFalse(cooper.getResponse("list").isError());
        CommandResult goodbye = cooper.getResponse("bye");
        assertTrue(goodbye.shouldExit());
        assertFalse(goodbye.isError());
    }

    @Test
    public void run_invalidCommand_continuesAndPrintsPlainText() {
        String output = runCooper("mark\ntodo read book\nlist\nbye\ntodo should not run\n");
        assertTrue(output.contains("Let's check those coordinates. Use: mark <task-number>"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertFalse(output.contains("Error\n"));
        assertFalse(output.contains("should not run"));
    }

    @Test
    public void run_findMatchingTasks_displaysAllMatches() {
        String output = runCooper("todo read book\n"
                + "deadline return book /by 2026-08-30\n"
                + "todo buy groceries\n"
                + "find book\n"
                + "bye\n");

        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book"));
    }

    @Test
    public void run_findWithoutMatches_displaysNoMatchesMessage() {
        String output = runCooper("todo read book\nfind movie\nbye\n");

        assertTrue(output.contains("No matching tasks found!"));
    }

    @Test
    public void run_malformedEventAndByeWithArguments_continuesUntilValidBye() {
        String output = runCooper("event meeting /from 2026-10-01\nbye extra\ntodo recovered\nlist\nbye\n");
        assertTrue(output.contains("Use: event <description> /from <date> /to <date>"));
        assertTrue(output.contains("I don't need extra arguments. Use: bye"));
        assertTrue(output.contains("1.[T][ ] recovered"));
        assertTrue(output.contains("I'm signing off. Until our next mission!"));
    }

    @Test
    public void run_endOfInput_returnsCleanlyAndPersistsCommands() {
        String emptyOutput = assertTimeoutPreemptively(Duration.ofSeconds(5), () -> runCooper(""));
        assertEquals("Cooper here, your task co-pilot. What's our next mission?" + System.lineSeparator(), emptyOutput);

        String output = assertTimeoutPreemptively(Duration.ofSeconds(5), () -> runCooper("todo saved at EOF\nlist"));
        assertTrue(output.contains("On the flight plan. I've added this task:"));
        assertTrue(output.contains("Here's your flight plan:\n1.[T][ ] saved at EOF"));
        assertFalse(output.contains("I'm signing off."));

        Path file = temporaryDirectory.resolve("data").resolve("cooper.txt");
        CommandResult listed = new Cooper(file.toString()).getResponse("list");
        assertFalse(listed.isError());
        assertFalse(listed.shouldExit());
        assertEquals("Here's your flight plan:\n1.[T][ ] saved at EOF", listed.message());
    }

    private String runCooper(String commands) {
        ByteArrayInputStream input = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(input);
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        Path dataFile = temporaryDirectory.resolve("data").resolve("cooper.txt");
        new Cooper(dataFile.toString()).run();
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void run_notesLifecycleAndRestart_displaysPersistedNotes() {
        String output = runCooper("note add first\nnote add second\nnote list\nnote find SECOND\n"
                + "note edit 2 replacement\nnote delete 1\nbye\n");
        assertTrue(output.contains("Here are your notes:\n1.[N] first\n2.[N] second"));
        assertTrue(output.contains("Here are the matching notes:\n2.[N] second"));
        assertTrue(output.contains("Got it. I've updated this note:\n2.[N] replacement"));
        assertTrue(output.contains("Noted. I've removed this note:\n1.[N] first"));
        assertTrue(runCooper("note list\nbye\n").contains("Here are your notes:\n1.[N] replacement"));
    }
}
