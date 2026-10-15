package ayre.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ayre.CommandResult;
import ayre.enums.AyreStatus;

// Refactored by GPT-5.6 Luna
/** Tests the command that terminates the application. */
public class ByeCommandTest {
    @Test
    public void doCommand_noArguments_terminates() throws Exception {
        CommandResult result = new ByeCommand(null).doCommand(new ArrayList<>());

        assertEquals(AyreStatus.TERMINATE, result.status());
        assertEquals("~ Terminating connection. See you again, Raven.\n", result.message());
    }

    @Test
    public void validate_noArguments_doesNotThrow() {
        assertDoesNotThrow(() -> new ByeCommand(null).validate(new ArrayList<>()));
    }
}
