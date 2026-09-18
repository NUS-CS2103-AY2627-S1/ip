package blud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests basic Blud behavior. */
public class BludTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that the test harness is configured correctly. */
    @Test
    public void dummyTest() {
        assertEquals(2, 2);
    }

    /** Verifies successful sort commands and their user-facing responses. */
    @Test
    public void processCommand_sortDeadline_returnsSortedResponse() {
        Blud blud = new Blud(temporaryDirectory.resolve("tasks.txt").toString());
        blud.processCommand("todo read book");
        blud.processCommand("deadline submit report /by 10/6/2026 0900");

        assertEquals("Tasks sorted by deadline in ascending order.",
                blud.processCommand("sort deadline"));
        assertEquals("Here are the tasks in your list:\n"
                + "1. [T][ ] read book\n"
                + "2. [D][ ] submit report (by: Jun 10 2026, 9:00 am)",
                blud.processCommand("list"));
    }

    /** Verifies that invalid sort commands return errors without throwing. */
    @Test
    public void processCommand_invalidSort_returnsValidationMessage() {
        Blud blud = new Blud(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Please specify a sort criterion: deadline, event, or status.",
                blud.processCommand("sort"));
        assertEquals("Invalid sort direction. Please use asc or desc.",
                blud.processCommand("sort deadline backwards"));
    }

    /** Verifies that sorting an empty task list returns the dedicated message. */
    @Test
    public void processCommand_sortEmptyList_returnsNoTasksMessage() {
        Blud blud = new Blud(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("There are no tasks to sort.", blud.processCommand("sort status"));
    }

    /** Verifies add, mark, unmark, find, delete, and goodbye commands. */
    @Test
    public void processCommand_supportedCommands_returnsExpectedResponses() {
        Blud blud = new Blud(temporaryDirectory.resolve("tasks.txt").toString());

        assertFalse(blud.processCommandWithStatus("todo read book").isError());
        assertEquals("Nice! I've marked this task as done:\n[T][X] read book",
                blud.processCommand("mark 1"));
        assertEquals("OK, I've marked this task as not done yet:\n[T][ ] read book",
                blud.processCommand("unmark 1"));
        assertTrue(blud.processCommand("find BOOK").contains("read book"));
        assertEquals("Task removed successfully:\n[T][ ] read book", blud.processCommand("delete 1"));
        assertEquals("Thanks for the conversation, see you soon!", blud.processCommand("bye"));
        assertEquals("Thanks for the conversation, see you soon!", blud.processCommand("BYE"));
    }

    /** Verifies that command errors are represented in the status result. */
    @Test
    public void processCommandWithStatus_invalidCommand_returnsErrorResult() {
        Blud.CommandResult result = new Blud(temporaryDirectory.resolve("tasks.txt").toString()).
                processCommandWithStatus("not-a-command");

        assertTrue(result.isError());
        assertFalse(result.response().isBlank());
    }
}
