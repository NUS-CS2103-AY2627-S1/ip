package goop.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.Priority;
import goop.task.TaskList;
import goop.task.Todo;
import goop.ui.ResponseUi;
import goop.ui.Ui;

/**
 * Tests command validation, persistence, and rollback after saving fails.
 */
class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    private final Ui ui = new Ui();
    private final Storage failingStorage = new FailingStorage();

    @Test
    void taskCommand_nonPositiveTaskNumber_assertionFails() {
        for (int taskNumber : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(AssertionError.class, () -> new MarkCommand(taskNumber));
            assertThrows(AssertionError.class, () -> new UnmarkCommand(taskNumber));
            assertThrows(AssertionError.class, () -> new DeleteCommand(taskNumber));
        }
    }

    @Test
    void addCommand_saveFails_removesNewTask() {
        Todo existingTask = new Todo("existing");
        TaskList tasks = new TaskList(List.of(existingTask));

        IOException error = assertThrows(IOException.class, () ->
                new AddCommand(new Todo("new")).execute(tasks, ui, failingStorage));

        assertEquals("simulated save failure", error.getMessage());
        assertEquals(1, tasks.size());
        assertSame(existingTask, tasks.get(0));
    }

    @Test
    void deleteCommand_saveFails_restoresDeletedTaskAtOriginalPosition() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));

        assertThrows(IOException.class, () ->
                new DeleteCommand(2).execute(tasks, ui, failingStorage));

        assertEquals(3, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertSame(third, tasks.get(2));
    }

    @Test
    void markCommand_saveFails_restoresPreviousCompletionState() {
        Todo incompleteTask = new Todo("incomplete");
        Todo completedTask = new Todo("completed");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(incompleteTask, completedTask));

        assertThrows(IOException.class, () ->
                new MarkCommand(1).execute(tasks, ui, failingStorage));
        assertFalse(incompleteTask.isDone());

        assertThrows(IOException.class, () ->
                new UnmarkCommand(2).execute(tasks, ui, failingStorage));
        assertTrue(completedTask.isDone());
    }

    @Test
    void completionCommand_saveFails_preservesEitherInitialState() {
        for (boolean wasDone : new boolean[] {false, true}) {
            for (Command command : new Command[] {new MarkCommand(1), new UnmarkCommand(1)}) {
                TaskList tasks = new TaskList(List.of(new Todo("task")));
                tasks.setDone(0, wasDone);
                ResponseUi responseUi = new ResponseUi();

                IOException error = assertThrows(IOException.class, () ->
                        command.execute(tasks, responseUi, failingStorage));

                assertEquals("simulated save failure", error.getMessage());
                assertEquals(wasDone, tasks.get(0).isDone());
                assertNull(responseUi.getResponse());
            }
        }
    }

    @Test
    void completionCommand_success_persistsAndReportsBothStates() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList(List.of(new Todo("task")));
        ResponseUi responseUi = new ResponseUi();

        new MarkCommand(1).execute(tasks, responseUi, storage);
        assertTrue(storage.loadTasks().get(0).isDone());
        assertEquals("Nice! I've marked this task as done:\n  [T][X] task", responseUi.getResponse());

        new UnmarkCommand(1).execute(tasks, responseUi, storage);
        assertFalse(storage.loadTasks().get(0).isDone());
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] task", responseUi.getResponse());
    }

    @Test
    void completionCommand_outOfRange_doesNotChangeOrSaveTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("task")));
        for (Command command : new Command[] {new MarkCommand(2), new UnmarkCommand(2)}) {
            GoopException error = assertThrows(GoopException.class, () ->
                    command.execute(tasks, ui, failingStorage));

            assertEquals("Task 2 is outside the list. Run list and choose a number from 1 to 1.",
                    error.getMessage());
            assertFalse(tasks.get(0).isDone());
        }
    }

    @Test
    void priorityCommand_saveFails_restoresPreviousPriorityWithoutSuccessResponse() {
        Todo task = new Todo("task");
        task.setPriority(Priority.HIGH);
        TaskList tasks = new TaskList(List.of(task));
        ResponseUi responseUi = new ResponseUi();

        for (Priority priority : new Priority[] {Priority.LOW, Priority.NONE}) {
            IOException error = assertThrows(IOException.class, () ->
                    new PriorityCommand(1, priority).execute(tasks, responseUi, failingStorage));

            assertEquals("simulated save failure", error.getMessage());
            assertEquals(Priority.HIGH, task.getPriority());
            assertNull(responseUi.getResponse());
        }
    }

    /** Storage double that consistently simulates a disk-write failure. */
    private static class FailingStorage extends Storage {
        FailingStorage() {
            super(Path.of("unused-test-file.txt"));
        }

        @Override
        public void saveTasks(TaskList tasks) throws IOException {
            throw new IOException("simulated save failure");
        }
    }
}
