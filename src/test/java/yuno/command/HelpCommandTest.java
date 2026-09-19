package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;

class HelpCommandTest extends CommandTestSupport {
    @Test
    void execute_noArguments_displaysHelp() throws YunoException {
        CommandResult commandResult = new HelpCommand("").execute(taskList, ui, storage);

        assertEquals(CommandResult.CONTINUE, commandResult);
        assertTrue(ui.isHelpPrinted());
    }

    @Test
    void execute_additionalArguments_throwsInvalidCommandFormatException() {
        assertThrows(
                InvalidCommandFormatException.class, () ->
                        new HelpCommand("commands").execute(taskList, ui, storage));
    }
}
