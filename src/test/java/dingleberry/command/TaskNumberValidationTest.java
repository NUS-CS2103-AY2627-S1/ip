package dingleberry.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dingleberry.exception.DingleberryException;
import dingleberry.model.TaskList;
import dingleberry.model.Todo;
import dingleberry.persistence.Storage;
import dingleberry.ui.Ui;

/** Tests task-number boundaries and failure side effects across commands. */
class TaskNumberValidationTest {
    /** Defines the first task number beyond the two-task fixture. */
    private static final int OUT_OF_RANGE_TASK_NUMBER = 3;
    /** Provides isolated storage for successful commands. */
    @TempDir
    private Path tempDir;

    @Test
    void execute_invalidTaskNumbers_preservesStateWithoutOutputOrSave() {
        int[] invalidTaskNumbers = {Integer.MIN_VALUE, -1, 0,
                OUT_OF_RANGE_TASK_NUMBER, Integer.MAX_VALUE};
        for (int taskNumber : invalidTaskNumbers) {
            for (Command command : commandsFor(taskNumber)) {
                Todo firstTask = new Todo("first");
                Todo lastTask = new Todo("last");
                lastTask.markAsDone();
                TaskList tasks = new TaskList(firstTask, lastTask);

                assertRejectedWithoutSideEffects(command, tasks);

                assertEquals(2, tasks.size());
                assertSame(firstTask, tasks.get(0));
                assertSame(lastTask, tasks.get(1));
                assertFalse(firstTask.isDone());
                assertTrue(lastTask.isDone());
            }
        }
    }

    @Test
    void execute_emptyList_rejectsTaskNumberWithoutOutputOrSave() {
        for (Command command : commandsFor(1)) {
            TaskList tasks = new TaskList();
            assertRejectedWithoutSideEffects(command, tasks);
            assertEquals(0, tasks.size());
        }
    }

    @Test
    void execute_firstAndLastTask_marksAndPersistsSelectedTask()
            throws Exception {
        for (int taskNumber : new int[]{1, 2}) {
            TaskList tasks = new TaskList(new Todo("first"), new Todo("last"));
            Storage storage = createStorage();

            new MarkCommand(taskNumber).execute(tasks, new Ui(), storage);

            TaskList loadedTasks = new TaskList(storage.load());
            assertEquals(2, loadedTasks.size());
            assertTrue(loadedTasks.get(taskNumber - 1).isDone());
            assertFalse(loadedTasks.get(2 - taskNumber).isDone());
        }
    }

    @Test
    void execute_firstAndLastTask_unmarksAndPersistsSelectedTask()
            throws Exception {
        for (int taskNumber : new int[]{1, 2}) {
            Todo firstTask = new Todo("first");
            Todo lastTask = new Todo("last");
            firstTask.markAsDone();
            lastTask.markAsDone();
            TaskList tasks = new TaskList(firstTask, lastTask);
            Storage storage = createStorage();

            new UnmarkCommand(taskNumber).execute(tasks, new Ui(), storage);

            TaskList loadedTasks = new TaskList(storage.load());
            assertEquals(2, loadedTasks.size());
            assertFalse(loadedTasks.get(taskNumber - 1).isDone());
            assertTrue(loadedTasks.get(2 - taskNumber).isDone());
        }
    }

    @Test
    void execute_firstAndLastTask_deletesAndPersistsSelectedTask()
            throws Exception {
        for (int taskNumber : new int[]{1, 2}) {
            TaskList tasks = new TaskList(new Todo("first"), new Todo("last"));
            Storage storage = createStorage();

            new DeleteCommand(taskNumber).execute(tasks, new Ui(), storage);

            TaskList loadedTasks = new TaskList(storage.load());
            assertEquals(1, loadedTasks.size());
            assertEquals(taskNumber == 1 ? "last" : "first",
                    loadedTasks.get(0).getDescription());
        }
    }

    private List<Command> commandsFor(final int taskNumber) {
        return List.of(new DeleteCommand(taskNumber),
                new MarkCommand(taskNumber),
                new UnmarkCommand(taskNumber));
    }

    private Storage createStorage() {
        return new Storage(tempDir.resolve("tasks.txt").toString());
    }

    /**
     * Verifies that validation fails before displaying success or saving.
     *
     * @param command the command with an invalid task number.
     * @param tasks the current task list.
     */
    private void assertRejectedWithoutSideEffects(final Command command,
            final TaskList tasks) {
        Storage storage = new Storage(
                tempDir.resolve("unused.txt").toString()) {
            @Override
            public void save(final TaskList taskList) {
                throw new AssertionError(
                        "Invalid commands must not save tasks.");
            }
        };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try (PrintStream capturedOutput = new PrintStream(output, true,
                StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            DingleberryException exception = assertThrows(
                    DingleberryException.class,
                    () -> command.execute(tasks, new Ui(), storage));
            assertEquals("That task number is not in the list.",
                    exception.getMessage());
            assertFalse(exception.isWrongCommand());
            assertEquals("", output.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(originalOutput);
        }
    }
}
