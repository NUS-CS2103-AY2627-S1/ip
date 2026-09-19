package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidTaskNumberException;
import yuno.exception.YunoException;

class MarkCommandTest extends CommandTestSupport {
    @Test
    void execute_validTaskNumber_marksAndSavesTask() throws YunoException, IOException {
        taskList.addTask("read book");

        CommandResult commandResult = new MarkCommand("1").execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals('X', taskList.getTask(1).getStatus());
        assertSame(taskList.getTask(1), ui.getMarkedTask());
        assertEquals("T | X | read book", Files.readString(tempDir.resolve("tasks.txt")).strip());
    }

    @Test
    void execute_nonNumericTaskNumber_throwsInvalidTaskNumberException() {
        assertThrows(
                InvalidTaskNumberException.class, () ->
                        new MarkCommand("one").execute(taskList, ui, storage));
    }

    @Test
    void execute_outOfBoundsTaskNumber_throwsInvalidTaskNumberException() {
        assertThrows(
                InvalidTaskNumberException.class, () ->
                        new MarkCommand("1").execute(taskList, ui, storage));
    }

    @Test
    void execute_negativeTaskNumber_throwsInvalidTaskNumberException() {
        taskList.addTask("read book");

        assertThrows(
                InvalidTaskNumberException.class, () ->
                        new MarkCommand("-1").execute(taskList, ui, storage));
    }
}
