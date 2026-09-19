package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;

class TodoCommandTest extends CommandTestSupport {
    @Test
    void execute_validDescription_addsAndSavesTodo() throws YunoException, IOException {
        CommandResult commandResult = new TodoCommand("read | book").execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertEquals(1, taskList.getCount());
        assertSame(taskList.getTask(1), ui.getAddedTask());
        assertEquals("T |   | read | book", Files.readString(tempDir.resolve("tasks.txt")).strip());
    }

    @Test
    void execute_blankDescription_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new TodoCommand("   ").execute(taskList, ui, storage));
        assertEquals(0, taskList.getCount());
    }
}
