package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidTaskNumberException;
import yuno.exception.YunoException;
import yuno.task.Task;

class DeleteCommandTest extends CommandTestSupport {
    @Test
    void execute_validTaskNumber_deletesAndSavesTask() throws YunoException, IOException {
        Task deletedTask = taskList.addTask("first task");
        taskList.addTask("second task");

        CommandResult commandResult = new DeleteCommand("1").execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(1, taskList.getCount());
        assertSame(deletedTask, ui.getDeletedTask());
        assertEquals("T |   | second task", Files.readString(tempDir.resolve("tasks.txt")).stripTrailing());
    }

    @Test
    void execute_nonNumericTaskNumber_throwsInvalidTaskNumberException() {
        assertThrows(
                InvalidTaskNumberException.class, () ->
                        new DeleteCommand("one").execute(taskList, ui, storage));
    }

    @Test
    void execute_outOfBoundsTaskNumber_throwsInvalidTaskNumberException() {
        assertThrows(
                InvalidTaskNumberException.class, () ->
                        new DeleteCommand("1").execute(taskList, ui, storage));
    }
}
