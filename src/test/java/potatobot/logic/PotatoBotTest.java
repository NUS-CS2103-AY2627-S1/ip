package potatobot.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import potatobot.exception.PotatoBotException;
import potatobot.logic.parser.Parser;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

public class PotatoBotTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void constructor_missingSaveFile_startsEmptyWithoutError() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("missing.txt"));
        assertNull(potatoBot.getStartupErrorMessage());
        assertEquals("Nothing to see here...", potatoBot.respondTo("list").message());
    }

    @Test
    public void constructor_corruptSaveFile_reportsFailureWithoutPartialLoad() throws IOException {
        Path saveFile = temporaryDirectory.resolve("corrupt.txt");
        Files.writeString(saveFile, "[ ] valid\n[?] invalid");
        PotatoBot potatoBot = createPotatoBot(saveFile);
        assertEquals("I couldn't load your saved tasks: Invalid completion status in line: [?] invalid",
                potatoBot.getStartupErrorMessage());
        assertEquals("Nothing to see here...", potatoBot.respondTo("list").message());
        assertFalse(potatoBot.respondTo("todo recovery").isError());
    }

    @Test
    public void constructor_unreadableSavePath_reportsStartupError() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory);
        assertTrue(potatoBot.getStartupErrorMessage().startsWith("I couldn't load your saved tasks: "));
        assertEquals("Nothing to see here...", potatoBot.respondTo("list").message());
    }

    @Test
    public void respondTo_failedAddAtCapacity_preservesUndoHistory() throws PotatoBotException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < TaskList.MAX_SIZE - 1; i++) {
            tasks.add(new Task("existing " + i));
        }
        PotatoBot potatoBot = new PotatoBot(new Parser(), tasks,
                new Storage(temporaryDirectory.resolve("missing.txt").toString()));
        assertFalse(potatoBot.respondTo("add last").isError());
        assertTrue(potatoBot.respondTo("add overflow").isError());
        assertFalse(potatoBot.respondTo("find existing").isError());
        assertEquals("PototaBot has travelled back in time: add last undone", potatoBot.respondTo("undo").message());
        assertEquals(TaskList.MAX_SIZE - 1, tasks.size());
    }

    @Test
    public void respondTo_failedUndo_keepsHistoryForRetry() throws PotatoBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Task("original"));
        PotatoBot potatoBot = new PotatoBot(new Parser(), tasks,
                new Storage(temporaryDirectory.resolve("missing.txt").toString()));
        potatoBot.respondTo("delete 1");
        // Fill through the collaborator so the pending delete remains the newest
        // history entry.
        for (int i = 0; i < TaskList.MAX_SIZE; i++) {
            tasks.add(new Task("external " + i));
        }
        assertTrue(potatoBot.respondTo("undo").isError());
        tasks.delete(tasks.size() - 1);
        assertFalse(potatoBot.respondTo("undo").isError());
        assertEquals("original", tasks.get(0).toString());
        assertEquals(TaskList.MAX_SIZE, tasks.size());
        assertTrue(potatoBot.respondTo("undo").isError());
    }

    @Test
    public void respondTo_restartedSession_doesNotRestoreUndoHistory() throws IOException {
        Path saveFile = temporaryDirectory.resolve("restart.txt");
        Files.writeString(saveFile, "[X] saved task (Todo)");
        PotatoBot potatoBot = createPotatoBot(saveFile);
        assertTrue(potatoBot.respondTo("undo").isError());
        assertEquals("Here are the tasks in your potato sack:\n  1.[X] saved task (Todo)",
                potatoBot.respondTo("list").message());
    }

    @Test
    public void respondTo_addThenList_stateRetainedAcrossCommands() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));

        CommandResult addResult = potatoBot.respondTo("todo read book");
        CommandResult listResult = potatoBot.respondTo("list");

        assertEquals("Added: read book (Todo)", addResult.message());
        assertFalse(addResult.isExit());
        assertFalse(addResult.isError());
        assertTrue(listResult.message().contains("1.[ ] read book (Todo)"));
        assertFalse(listResult.isExit());
        assertFalse(listResult.isError());
    }

    @Test
    public void respondTo_invalidCommand_errorResultReturned() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));

        CommandResult result = potatoBot.respondTo("dance");

        assertEquals("Me no gets?", result.message());
        assertFalse(result.isExit());
        assertTrue(result.isError());
    }

    @Test
    public void respondTo_invalidArgumentsAndTaskNumber_errorResultsReturned() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));

        assertTrue(potatoBot.respondTo("todo").isError());
        assertTrue(potatoBot.respondTo("deadline homework /by not-a-date").isError());
        assertTrue(potatoBot.respondTo("delete 1").isError());
        assertFalse(potatoBot.respondTo("list").isError());
    }

    @Test
    public void respondTo_taskContainsErrorText_successResultReturned() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));

        assertFalse(potatoBot.respondTo("todo Me no gets?").isError());
        assertFalse(potatoBot.respondTo("list").isError());
    }

    @Test
    public void respondTo_undoWithNoHistory_emptyHistoryMessageReturned() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));

        CommandResult result = potatoBot.respondTo("undo");

        assertEquals("PotatoBot can't travel THAT far back in time sorry :(", result.message());
        assertFalse(result.isExit());
        assertTrue(result.isError());
    }

    @Test
    public void respondTo_undoAdd_additionReversed() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));
        potatoBot.respondTo("todo read book");

        CommandResult result = potatoBot.respondTo("undo");

        assertEquals(
                "PototaBot has travelled back in time: todo read book undone",
                result.message());
        assertEquals("Nothing to see here...", potatoBot.respondTo("list").message());
    }

    @Test
    public void respondTo_undoMarkAndUnmark_statusChangesReversedInOrder() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));
        potatoBot.respondTo("todo read book");
        potatoBot.respondTo("mark 1");
        potatoBot.respondTo("unmark 1");

        assertEquals(
                "PototaBot has travelled back in time: unmark 1 undone",
                potatoBot.respondTo("undo").message());
        assertTrue(potatoBot.respondTo("list").message().contains("1.[X] read book (Todo)"));

        assertEquals(
                "PototaBot has travelled back in time: mark 1 undone",
                potatoBot.respondTo("undo").message());
        assertTrue(potatoBot.respondTo("list").message().contains("1.[ ] read book (Todo)"));
    }

    @Test
    public void respondTo_undoDelete_deletedTaskRestoredAtOriginalPosition() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));
        potatoBot.respondTo("add first");
        potatoBot.respondTo("add second");
        potatoBot.respondTo("delete 1");

        CommandResult result = potatoBot.respondTo("undo");

        assertEquals(
                "PototaBot has travelled back in time: delete 1 undone",
                result.message());
        assertEquals(
                "Here are the tasks in your potato sack:\n"
                        + "  1.[ ] first\n"
                        + "  2.[ ] second",
                potatoBot.respondTo("list").message());
    }

    @Test
    public void respondTo_sixAdditionsOnlyFiveMostRecentCommandsCanBeUndone() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));
        for (int taskNumber = 1; taskNumber <= 6; taskNumber++) {
            potatoBot.respondTo("add task " + taskNumber);
        }

        for (int undoCount = 0; undoCount < 5; undoCount++) {
            potatoBot.respondTo("undo");
        }

        assertEquals(
                "PotatoBot can't travel THAT far back in time sorry :(",
                potatoBot.respondTo("undo").message());
        assertEquals(
                "Here are the tasks in your potato sack:\n  1.[ ] task 1",
                potatoBot.respondTo("list").message());
    }

    @Test
    public void respondTo_nonMutatingAndFailedCommands_lastMutationRemainsUndoable() {
        PotatoBot potatoBot = createPotatoBot(temporaryDirectory.resolve("tasks.txt"));
        potatoBot.respondTo("add read book");
        potatoBot.respondTo("list");
        potatoBot.respondTo("delete 2");

        CommandResult result = potatoBot.respondTo("undo");

        assertEquals(
                "PototaBot has travelled back in time: add read book undone",
                result.message());
        assertEquals("Nothing to see here...", potatoBot.respondTo("list").message());
    }

    @Test
    public void constructor_existingSaveFile_tasksAvailableImmediately() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "[X] read book (Todo)");

        PotatoBot potatoBot = createPotatoBot(saveFile);

        assertTrue(potatoBot.respondTo("list").message().contains("1.[X] read book (Todo)"));
    }

    @Test
    public void respondTo_bye_tasksSavedAndExitResultReturned() throws IOException {
        Path saveFile = temporaryDirectory.resolve("nested/tasks.txt");
        PotatoBot potatoBot = createPotatoBot(saveFile);
        potatoBot.respondTo("todo read book");

        CommandResult result = potatoBot.respondTo("bye");

        assertEquals("Bye. I'm rolling back to the potato patch.", result.message());
        assertTrue(result.isExit());
        assertFalse(result.isError());
        assertEquals("[ ] read book (Todo)", Files.readString(saveFile));
    }

    @Test
    public void shutdown_withoutBye_tasksSaved() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        PotatoBot potatoBot = createPotatoBot(saveFile);
        potatoBot.respondTo("todo read book");

        potatoBot.shutdown();

        assertEquals("[ ] read book (Todo)", Files.readString(saveFile));
    }

    @Test
    public void shutdown_afterBye_tasksSavedOnlyOnce() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        PotatoBot potatoBot = createPotatoBot(saveFile);
        potatoBot.respondTo("todo read book");
        potatoBot.respondTo("bye");
        Files.writeString(saveFile, "external change");

        potatoBot.shutdown();

        assertEquals("external change", Files.readString(saveFile));
    }

    @Test
    public void shutdown_firstSaveFails_secondCallRetries() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(saveFile);
        PotatoBot potatoBot = createPotatoBot(saveFile);
        potatoBot.respondTo("todo read book");

        potatoBot.shutdown();
        Files.delete(saveFile);
        potatoBot.shutdown();

        assertEquals("[ ] read book (Todo)", Files.readString(saveFile));
    }

    /**
     * Creates a PotatoBot whose persistence is isolated to the current test.
     *
     * @param saveFile Save-file location for the test.
     * @return PotatoBot backed by the specified temporary file.
     */
    private static PotatoBot createPotatoBot(Path saveFile) {
        return new PotatoBot(
                new Parser(),
                new TaskList(),
                new Storage(saveFile.toString()));
    }
}
