package quackie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests recognition of the command keywords accepted by Quackie. */
class CommandTypeTest {
    /** Verifies that supported commands map to their corresponding types. */
    @Test
    void fromInputRecognisesSupportedCommands() {
        assertEquals(CommandType.BYE, CommandType.fromInput("bye"));
        assertEquals(CommandType.LIST, CommandType.fromInput("list"));
        assertEquals(CommandType.FIND, CommandType.fromInput("find book"));
        assertEquals(CommandType.DELETE, CommandType.fromInput("delete 1"));
        assertEquals(CommandType.MARK, CommandType.fromInput("mark 1"));
        assertEquals(CommandType.UNMARK, CommandType.fromInput("unmark 1"));
        assertEquals(CommandType.UPDATE, CommandType.fromInput("update 1 todo read novel"));
        assertEquals(CommandType.EVENT, CommandType.fromInput("event meeting /from 2pm /to 3pm"));
        assertEquals(CommandType.TODO, CommandType.fromInput("todo read book"));
        assertEquals(CommandType.DEADLINE, CommandType.fromInput("deadline report /by Friday"));
        assertEquals(CommandType.TODO, CommandType.fromInput("  todo   read book  "));
    }

    /** Verifies that unknown input is not mistaken for a supported command. */
    @Test
    void fromInputReturnsUnknownForUnsupportedCommands() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("archive everything"));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(""));
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(null));
    }
}
