package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidTaskNumberException;
import yuno.exception.YunoException;
import yuno.task.Todo;

class UnmarkCommandTest extends CommandTestSupport {
    @Test
    void execute_validTaskNumber_unmarksAndSavesTask() throws YunoException, IOException {
        taskList.addTask(new Todo("read book", true));

        CommandResult commandResult = new UnmarkCommand("1").execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(' ', taskList.getTask(1).getStatus());
        assertSame(taskList.getTask(1), ui.getUnmarkedTask());
        assertEquals("T |   | read book", Files.readString(tempDir.resolve("tasks.txt")).stripTrailing());
    }

    @Test
    void execute_nonNumericTaskNumber_throwsInvalidTaskNumberException() {
        assertThrows(
                InvalidTaskNumberException.class, () ->
                        new UnmarkCommand("one").execute(taskList, ui, storage));
    }

    @Test
    void execute_outOfBoundsTaskNumber_throwsInvalidTaskNumberException() {
        assertThrows(
                InvalidTaskNumberException.class, () ->
                        new UnmarkCommand("1").execute(taskList, ui, storage));
    }
}
