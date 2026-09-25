package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import datahandler.DataHandler;
import exception.FriedbergInternalException;
import exception.FriedbergUserInputException;
import task.TaskStatus;
import task.ToDo;

/**
 * Tests context invariants and persistence without touching application storage.
 */
class CommandContextTest {
    @TempDir
    Path tempDir;

    private DataHandler dataHandler;
    private CommandContext context;

    @BeforeEach
    void setUp() throws Exception {
        dataHandler = new DataHandler(tempDir, tempDir.resolve("tasks.txt"));
        context = new CommandContext(dataHandler);
    }

    @Test
    void constructor_emptyStorage_loadsEmptyList() {
        assertEquals(0, context.getTasksSize());
        assertEquals("Here are the tasks in your list:", context.renderTasks());
    }

    @Test
    void constructor_incompleteStoredTask_throwsInternalException() throws Exception {
        dataHandler.write("name,P");

        FriedbergInternalException error = assertThrows(
                FriedbergInternalException.class, () -> new CommandContext(dataHandler));

        assertEquals(new FriedbergInternalException(
                "Expected serialized task to contain name, status, and type").getMessage(), error.getMessage());
        assertEquals("name,P", dataHandler.read());
    }

    @Test
    void addTask_realTask_persistsAndReloads() throws Exception {
        context.addTask(new ToDo("read book"));

        CommandContext reloaded = new CommandContext(dataHandler);
        assertEquals(1, reloaded.getTasksSize());
        assertEquals("[T][ ] read book", reloaded.renderTask(0));
        assertEquals("read book,P,T", dataHandler.read());
    }

    @Test
    void addTask_nullTask_rejectsBeforeMutationOrSaving() throws Exception {
        context.addTask(new ToDo("existing task"));
        String originalData = dataHandler.read();

        AssertionError error = assertThrows(AssertionError.class, () -> context.addTask(null));

        assertEquals("A task must not be null when added", error.getMessage());
        assertEquals(1, context.getTasksSize());
        assertEquals("[T][ ] existing task", context.renderTask(0));
        assertEquals(originalData, dataHandler.read());
    }

    @Test
    void markAndUnmark_validTask_persistExpectedStatus() throws Exception {
        context.addTask(new ToDo("read book"));

        context.markTask(0);
        assertEquals("[T][X] read book", new CommandContext(dataHandler).renderTask(0));

        context.unmarkTask(0);
        assertEquals("[T][ ] read book", new CommandContext(dataHandler).renderTask(0));
    }

    @Test
    void archiveAndUnarchive_validTask_persistExpectedLists() throws Exception {
        context.addTask(new ToDo("read book"));
        context.addTask(new ToDo("write code"));

        context.archiveTask(0);
        CommandContext archivedContext = new CommandContext(dataHandler);
        assertEquals("Here are the tasks in your list:\n1. [T][ ] write code", archivedContext.renderTasks());
        assertEquals("Here are the tasks in your archive:\n1. [T][ ] read book", archivedContext.renderArchivedTasks());

        archivedContext.unarchiveTask(0);
        CommandContext unarchivedContext = new CommandContext(dataHandler);
        assertEquals("Here are the tasks in your list:\n1. [T][ ] write code\n2. [T][ ] read book",
                unarchivedContext.renderTasks());
        assertEquals("Here are the tasks in your archive:", unarchivedContext.renderArchivedTasks());
    }

    @Test
    void taskOperations_invalidIndices_throwUserInputException() {
        assertThrows(FriedbergUserInputException.class, () -> context.markTask(-1));
        assertThrows(FriedbergUserInputException.class, () -> context.unmarkTask(0));
        assertThrows(FriedbergUserInputException.class, () -> context.removeTask(0));
        assertThrows(FriedbergUserInputException.class, () -> context.archiveTask(0));
        assertThrows(FriedbergUserInputException.class, () -> context.unarchiveTask(0));
    }

    @Test
    void markTask_brokenTask_throwsAssertionBeforeSaving() throws Exception {
        context.addTask(new NonMarkingTask());
        String originalData = dataHandler.read();

        AssertionError error = assertThrows(AssertionError.class, () -> context.markTask(0));

        assertEquals("Marking a task must leave it done", error.getMessage());
        assertEquals(originalData, dataHandler.read());
    }

    @Test
    void unmarkTask_brokenTask_throwsAssertionBeforeSaving() throws Exception {
        context.addTask(new NonUnmarkingTask());
        String originalData = dataHandler.read();

        AssertionError error = assertThrows(AssertionError.class, () -> context.unmarkTask(0));

        assertEquals("Unmarking a task must leave it in progress", error.getMessage());
        assertEquals(originalData, dataHandler.read());
    }

    /**
     * Intentionally violates the mark contract to exercise the context's postcondition.
     */
    private static class NonMarkingTask extends ToDo {
        NonMarkingTask() {
            super("broken mark");
        }

        @Override
        public void mark() {
            // Intentionally leave the task in progress.
        }
    }

    /**
     * Intentionally violates the unmark contract to exercise the context's postcondition.
     */
    private static class NonUnmarkingTask extends ToDo {
        NonUnmarkingTask() {
            super("broken unmark", TaskStatus.DONE);
        }

        @Override
        public void unmark() {
            // Intentionally leave the task done.
        }
    }
}
