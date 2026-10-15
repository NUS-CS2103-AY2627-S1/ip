package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.task.ToDo;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Tests marking tasks incomplete through the unmark command. */
class UnmarkCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void execute_validTaskNumber_taskUnmarkedAndSaved() throws CloverException, java.io.IOException {
        ToDo task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(task);
        Storage storage = new Storage(tempDir.resolve("clover.txt"));

        new UnmarkCommand("1").execute(tasks, new TutoreeList(), new Ui(), storage);

        assertFalse(task.isDone());
        assertFalse(storage.load().getFirst().isDone());
    }

    @Test
    void execute_incompleteTask_exceptionThrownWithoutSavingAgain() {
        ToDo task = new ToDo("read book");
        TaskList tasks = new TaskList();
        tasks.add(task);

        CloverException exception = assertThrows(CloverException.class, () -> new UnmarkCommand("1").execute(
                tasks, new TutoreeList(), new Ui(), new Storage(tempDir.resolve("clover.txt"))));

        assertEquals("That study quest is already incomplete.", exception.getMessage());
        assertFalse(task.isDone());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0", "2", "one"})
    void execute_invalidTaskNumber_exceptionThrownWithoutChangingTask(String taskNumber) {
        ToDo task = new ToDo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(task);

        CloverException exception = assertThrows(CloverException.class, () -> new UnmarkCommand(taskNumber).execute(
                tasks, new TutoreeList(), new Ui(), new Storage(tempDir.resolve("clover.txt"))));

        assertEquals("Choose a valid quest number to tend again.", exception.getMessage());
        assertTrue(task.isDone());
    }
}
