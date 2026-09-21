package yqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DukeTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addThenList_responsesReturnedAndTaskPersisted() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Duke duke = new Duke(dataFile.toString());

        String addResponse = duke.getResponse("todo read book");
        String listResponse = duke.getResponse("list");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.", addResponse);
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book", listResponse);
        assertTrue(dataFile.toFile().isFile());
    }

    @Test
    void getResponse_invalidCommand_errorReturned() {
        Duke duke = new Duke(temporaryDirectory.resolve("tasks.txt").toString());

        String response = duke.getResponse("unknown");

        assertEquals("Unknown command 'unknown'. Available commands: "
                + "todo, deadline, event, list, mark, unmark, delete, find, undo, bye", response);
    }

    @Test
    void getCommandResult_validCommand_resultNotMarkedAsError() {
        Duke duke = new Duke(temporaryDirectory.resolve("tasks.txt").toString());

        CommandResult result = duke.getCommandResult("list");

        assertFalse(result.isError());
    }

    @Test
    void getCommandResult_invalidCommand_resultMarkedAsError() {
        Duke duke = new Duke(temporaryDirectory.resolve("tasks.txt").toString());

        CommandResult result = duke.getCommandResult("unknown");

        assertEquals("Unknown command 'unknown'. Available commands: "
                + "todo, deadline, event, list, mark, unmark, delete, find, undo, bye", result.text());
        assertTrue(result.isError());
    }

    @Test
    void getCommandResult_nullCommand_clearErrorReturned() {
        Duke duke = new Duke(temporaryDirectory.resolve("tasks.txt").toString());

        CommandResult result = duke.getCommandResult(null);

        assertEquals("Please enter a command", result.text());
        assertTrue(result.isError());
    }

    @Test
    void getCommandResult_duplicateTask_errorReturnedAndListUnchanged() {
        Duke duke = new Duke(temporaryDirectory.resolve("tasks.txt").toString());
        duke.getResponse("todo read book");

        CommandResult duplicateResult = duke.getCommandResult("todo read book");

        assertEquals("This task already exists in the list", duplicateResult.text());
        assertTrue(duplicateResult.isError());
        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book",
                duke.getResponse("list"));
    }

    @Test
    void getWelcomeMessage_corruptedStorage_warningReturnedAndEmptyListUsed() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "invalid data", StandardCharsets.UTF_8);

        Duke duke = new Duke(dataFile.toString());

        assertTrue(duke.hasLoadingError());
        assertTrue(duke.getWelcomeMessage().contains("Invalid saved task on line 1"));
        assertEquals("There are no tasks in your list.", duke.getResponse("list"));
    }

    @Test
    void missingStorageFile_firstTaskCreatesFileWithoutWarning() {
        Path dataFile = temporaryDirectory.resolve("new-directory").resolve("tasks.txt");
        Duke duke = new Duke(dataFile.toString());

        assertFalse(duke.hasLoadingError());
        assertFalse(Files.exists(dataFile));

        duke.getResponse("todo read book");

        assertTrue(Files.isRegularFile(dataFile));
    }

    @Test
    void getResponse_undoAfterList_previousChangeRestoredAndPersisted() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Duke duke = new Duke(dataFile.toString());
        duke.getResponse("todo first task");
        duke.getResponse("todo second task");
        duke.getResponse("list");

        String undoResponse = duke.getResponse("undo");
        String listResponse = duke.getResponse("list");
        Duke reloadedDuke = new Duke(dataFile.toString());

        assertEquals("Done. The most recent change has been undone.", undoResponse);
        assertEquals("Here are the tasks in your list:\n1. [T][ ] first task", listResponse);
        assertEquals(listResponse, reloadedDuke.getResponse("list"));
    }

    @Test
    void getResponse_undoWithoutPreviousChange_errorReturned() {
        Duke duke = new Duke(temporaryDirectory.resolve("tasks.txt").toString());

        String response = duke.getResponse("undo");

        assertEquals("There is no command to undo", response);
    }

    @Test
    void getResponse_byeThenAnotherCommand_sessionRemainsEnded() {
        Duke duke = new Duke(temporaryDirectory.resolve("tasks.txt").toString());

        String goodbyeResponse = duke.getResponse("bye");
        String afterExitResponse = duke.getResponse("list");

        assertEquals("Bye. Hope to see you again soon!", goodbyeResponse);
        assertTrue(duke.hasExited());
        assertEquals("This session has ended. Restart yqr to enter more commands.", afterExitResponse);
    }
}
