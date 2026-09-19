package yuno.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import yuno.exception.InvalidCommandFormatException;

class ByeCommandTest extends CommandTestSupport {
    @Test
    void execute_noArguments_printsByeAndStops() throws InvalidCommandFormatException {
        CommandResult commandResult = new ByeCommand("").execute(taskList, ui, storage);

        assertEquals(CommandResult.EXIT, commandResult);
        assertTrue(ui.isByePrinted());
    }

    @Test
    void execute_additionalArguments_throwsInvalidCommandFormatException() {
        InvalidCommandFormatException exception = assertThrows(
                InvalidCommandFormatException.class, () ->
                        new ByeCommand("now").execute(taskList, ui, storage));
        assertEquals(
                "If you're leaving, just say 'bye'. I don't need the unnecessary details.",
                exception.getMessage());
        assertFalse(ui.isByePrinted());
    }
}
