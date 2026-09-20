package goop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command responses, recovery, and exit handling through the chatbot facade.
 */
class GoopTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addThenList_returnsGuiFriendlyResponsesAndRetainsTask() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        CommandResult addResult = goop.getResponse("todo read book");
        CommandResult listResult = goop.getResponse("list");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 tasks in the list.",
                addResult.getResponse());
        assertFalse(addResult.isExit());
        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book",
                listResult.getResponse());
        assertFalse(listResult.isExit());
    }

    @Test
    void getResponse_invalidCommand_returnsErrorWithoutConsoleDecoration() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        CommandResult result = goop.getResponse("unknown");

        assertEquals("ERROR: I don't recognise that command. Use todo, deadline, "
                + "event, list, find, mark, unmark, delete, priority, or bye.",
                result.getResponse());
        assertFalse(result.isExit());
    }

    @Test
    void getResponse_bye_returnsGoodbyeWithExitStatus() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        CommandResult result = goop.getResponse("bye");

        assertEquals("Bye. Hope to see you again soon!", result.getResponse());
        assertTrue(result.isExit());
    }

    @Test
    void getResponse_saveFails_rollsBackAndAllowsRecovery() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Goop goop = new Goop(dataFile);
        Files.createDirectory(dataFile);

        CommandResult result = goop.getResponse("todo unsaved task");

        assertEquals("ERROR: I couldn't save tasks to " + dataFile + ". No changes were made.",
                result.getResponse());
        assertFalse(result.isExit());
        assertEquals("Here are the tasks in your list:", goop.getResponse("list").getResponse());

        Files.delete(dataFile);
        goop.getResponse("todo saved task");
        Goop reloadedGoop = new Goop(dataFile);
        assertEquals("Here are the tasks in your list:\n1.[T][ ] saved task",
                reloadedGoop.getResponse("list").getResponse());
    }

    @Test
    void getResponse_findWithNoMatches_returnsHeadingOnly() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));
        goop.getResponse("todo read book");

        CommandResult result = goop.getResponse("find missing");

        assertEquals("Here are the matching tasks in your list:", result.getResponse());
        assertFalse(result.isExit());
    }
}
