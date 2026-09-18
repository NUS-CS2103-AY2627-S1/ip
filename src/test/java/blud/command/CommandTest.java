package blud.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import blud.exception.TaskTypeException;

/** Tests conversion of user-entered command names. */
public class CommandTest {
    /** Verifies that command conversion ignores case and surrounding spaces. */
    @Test
    public void stringToCommand_mixedCaseAndSpaces_returnsCommand() {
        assertEquals(Command.SORT, Command.stringToCommand(" sort "));
    }

    /** Verifies that a null command input is preserved as null. */
    @Test
    public void stringToCommand_nullInput_returnsNull() {
        assertNull(Command.stringToCommand(null));
    }

    /** Verifies that unsupported command names produce a user-facing error. */
    @Test
    public void stringToCommand_unknownInput_throwsTaskTypeException() {
        assertThrows(TaskTypeException.class, () -> Command.stringToCommand("unknown"));
    }
}
