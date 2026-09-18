package herta.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.TaskList;
import herta.task.Todo;
import herta.ui.Ui;

/**
 * Tests task creation, status mutation, deletion, and failed-save rollback.
 */
class TaskMutationCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void addCommand_executePersistsAndAddsTask() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        String output = CommandTestSupport.captureOutput(() ->
                new TodoCommand(todo).execute(tasks, new Ui(), new Storage(dataFile.toString())));

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
        assertEquals(List.of("T | 0 | read book"), Files.readAllLines(dataFile));
        assertTrue(output.contains("There. I've added it:"));
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("That makes 1 active task."));
    }

    @Test
    void addCommand_subclassesPersistDeadlineAndEvent() throws Exception {
        Path dataFile = temporaryDirectory.resolve("typed-tasks.txt");
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 10, 15, 18, 0));
        Event event = new Event("project meeting", LocalDateTime.of(2019, 10, 16, 10, 0),
                LocalDateTime.of(2019, 10, 16, 11, 0));
        Storage storage = new Storage(dataFile.toString());

        CommandTestSupport.captureOutput(() -> new DeadlineCommand(deadline)
                .execute(tasks, new Ui(), storage));
        CommandTestSupport.captureOutput(() -> new EventCommand(event)
                .execute(tasks, new Ui(), storage));

        assertEquals(2, tasks.size());
        assertEquals(List.of(deadline.toStorageString(), event.toStorageString()),
                Files.readAllLines(dataFile));
    }

    @Test
    void markUnmarkAndDelete_executeUpdatesPersistenceAndTaskList() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));
        Storage storage = new Storage(dataFile.toString());

        String markOutput = CommandTestSupport.captureOutput(() ->
                new MarkCommand(0).execute(tasks, new Ui(), storage));
        assertTrue(todo.isCompleted());
        assertTrue(markOutput.contains("Done. It's marked complete:"));

        String unmarkOutput = CommandTestSupport.captureOutput(() ->
                new UnmarkCommand(0).execute(tasks, new Ui(), storage));
        assertFalse(todo.isCompleted());
        assertTrue(unmarkOutput.contains("Fine. It's incomplete again:"));

        String deleteOutput = CommandTestSupport.captureOutput(() ->
                new DeleteCommand(0).execute(tasks, new Ui(), storage));
        assertEquals(0, tasks.size());
        assertTrue(deleteOutput.contains("Gone. I've removed it:"));
        assertEquals(List.of(), Files.readAllLines(dataFile));
    }

    @Test
    void taskChangingCommands_saveFailureRestoresOrPreservesMemory() throws Exception {
        Path dataDirectory = temporaryDirectory.resolve("not-a-file");
        Files.createDirectory(dataDirectory);
        Storage failingStorage = new Storage(dataDirectory.toString());
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        assertMarkFailureRestoresStatus(failingStorage, tasks, todo);
        assertUnmarkFailureRestoresStatus(failingStorage, tasks, todo);
        assertDeleteFailurePreservesTask(failingStorage, tasks, todo);
        assertAddFailurePreservesTaskList(failingStorage);
        assertInvalidIndexReportsHelpfulMessage(failingStorage, tasks);
    }

    @Test
    void taskStatusCommands_repeatedExecution_reportsNoOpAndPreservesStorage() throws Exception {
        Path dataFile = temporaryDirectory.resolve("status.txt");
        Todo todo = new Todo("repeat status");
        TaskList tasks = new TaskList(List.of(todo));
        Storage storage = new Storage(dataFile.toString());
        storage.save(tasks);

        CommandTestSupport.captureOutput(() ->
                new MarkCommand(0).execute(tasks, new Ui(), storage));
        assertStatusNoOpPreservesStorage(new MarkCommand(0), tasks, storage, dataFile,
                "Already complete. There is nothing more to do.");
        assertTrue(todo.isCompleted());

        CommandTestSupport.captureOutput(() ->
                new UnmarkCommand(0).execute(tasks, new Ui(), storage));
        assertStatusNoOpPreservesStorage(new UnmarkCommand(0), tasks, storage, dataFile,
                "Already incomplete. There is nothing to undo.");

        assertFalse(todo.isCompleted());
        assertEquals(List.of("T | 0 | repeat status"), Files.readAllLines(dataFile));
    }

    @Test
    void taskStatusCommands_noOpDoesNotRequireStorageWrite() throws Exception {
        Path dataDirectory = temporaryDirectory.resolve("not-a-file");
        Files.createDirectory(dataDirectory);
        Storage failingStorage = new Storage(dataDirectory.toString());

        Todo completedTask = new Todo("completed task");
        completedTask.markAsDone();
        TaskList completedTasks = new TaskList(List.of(completedTask));
        String markOutput = CommandTestSupport.captureOutput(() ->
                new MarkCommand(0).execute(completedTasks, new Ui(), failingStorage));
        assertEquals("     Already complete. There is nothing more to do."
                + System.lineSeparator(), markOutput);
        assertTrue(completedTask.isCompleted());

        Todo incompleteTask = new Todo("incomplete task");
        TaskList incompleteTasks = new TaskList(List.of(incompleteTask));
        String unmarkOutput = CommandTestSupport.captureOutput(() ->
                new UnmarkCommand(0).execute(incompleteTasks, new Ui(), failingStorage));
        assertEquals("     Already incomplete. There is nothing to undo."
                + System.lineSeparator(), unmarkOutput);
        assertFalse(incompleteTask.isCompleted());
    }

    private void assertStatusNoOpPreservesStorage(Command command, TaskList tasks,
                                                  Storage storage, Path dataFile,
                                                  String expectedMessage) throws Exception {
        byte[] originalFileBytes = Files.readAllBytes(dataFile);
        String output = CommandTestSupport.captureOutput(() ->
                command.execute(tasks, new Ui(), storage));

        assertEquals("     " + expectedMessage + System.lineSeparator(), output);
        assertArrayEquals(originalFileBytes, Files.readAllBytes(dataFile));
    }

    @Test
    void taskIndexCommands_invalidIndex_rejectEverySelectedTaskCommand() throws Exception {
        Path dataFile = temporaryDirectory.resolve("invalid-index.txt");
        TaskList tasks = new TaskList(List.of(new Todo("one task")));
        Storage storage = new Storage(dataFile.toString());
        storage.save(tasks);

        for (Command command : List.of(new MarkCommand(1), new UnmarkCommand(1),
                new DeleteCommand(1))) {
            assertThrows(HertaException.class, () -> command.execute(tasks, new Ui(), storage));
        }
        assertEquals(1, tasks.size());
    }

    @Test
    void constructors_invalidTaskArguments_rejectCommands() {
        assertThrows(NullPointerException.class, () -> new TodoCommand(null));
        assertThrows(NullPointerException.class, () -> new DeadlineCommand(null));
        assertThrows(NullPointerException.class, () -> new EventCommand(null));
        assertThrows(IllegalArgumentException.class, () -> new MarkCommand(-1));
        assertThrows(IllegalArgumentException.class, () -> new UnmarkCommand(-1));
        assertThrows(IllegalArgumentException.class, () -> new DeleteCommand(-1));
    }

    private void assertMarkFailureRestoresStatus(Storage failingStorage, TaskList tasks, Todo todo) {
        assertThrows(HertaException.class, () ->
                new MarkCommand(0).execute(tasks, new Ui(), failingStorage));
        assertFalse(todo.isCompleted());
    }

    private void assertUnmarkFailureRestoresStatus(Storage failingStorage, TaskList tasks, Todo todo) {
        todo.markAsDone();
        assertThrows(HertaException.class, () ->
                new UnmarkCommand(0).execute(tasks, new Ui(), failingStorage));
        assertTrue(todo.isCompleted());
    }

    private void assertDeleteFailurePreservesTask(Storage failingStorage, TaskList tasks, Todo todo) {
        assertThrows(HertaException.class, () ->
                new DeleteCommand(0).execute(tasks, new Ui(), failingStorage));
        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
    }

    private void assertAddFailurePreservesTaskList(Storage failingStorage) {
        TaskList invalidAddTasks = new TaskList();
        assertThrows(HertaException.class, () -> new TodoCommand(new Todo("valid task"))
                .execute(invalidAddTasks, new Ui(), failingStorage));
        assertEquals(0, invalidAddTasks.size());
    }

    private void assertInvalidIndexReportsHelpfulMessage(Storage failingStorage, TaskList tasks) {
        HertaException invalidIndexException = assertThrows(HertaException.class, () ->
                new MarkCommand(1).execute(tasks, new Ui(), failingStorage));
        assertEquals("No active task has that number. Check the list and try again.",
                invalidIndexException.getMessage());
    }
}
