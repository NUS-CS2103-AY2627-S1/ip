package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;

class ListCommandTest extends CommandTestSupport {
    @Test
    void execute_noArguments_displaysTaskList() throws YunoException {
        taskList.addTask("read book");

        CommandResult commandResult = new ListCommand("").execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertSame(taskList, ui.getDisplayedTaskList());
    }

    @Test
    void execute_additionalArguments_throwsInvalidCommandFormatException() {
        InvalidCommandFormatException exception = assertThrows(
                InvalidCommandFormatException.class, () ->
                        new ListCommand("all").execute(taskList, ui, storage));
        assertEquals(
                "I only need 'list' to show your tasks. What am I supposed to do with the rest?",
                exception.getMessage());
    }
}
