package lloyd.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion of command words into {@link CommandType} values.
 */
public class CommandTypeTest {

    /** Verifies the mapping for every recognized command word. */
    @Test
    public void from_recognizedCommandWord_returnsMatchingCommandType() {
        assertEquals(CommandType.BYE, CommandType.from("bye"));
        assertEquals(CommandType.LIST, CommandType.from("list"));
        assertEquals(CommandType.FIND, CommandType.from("find"));
        assertEquals(CommandType.CHECK, CommandType.from("check"));
        assertEquals(CommandType.REMINDER, CommandType.from("reminder"));
        assertEquals(CommandType.HELP, CommandType.from("help"));
        assertEquals(CommandType.MARK, CommandType.from("mark"));
        assertEquals(CommandType.UNMARK, CommandType.from("unmark"));
        assertEquals(CommandType.DELETE, CommandType.from("delete"));
        assertEquals(CommandType.TODO, CommandType.from("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
        assertEquals(CommandType.EVENT, CommandType.from("event"));
    }

    /** Verifies that an unrecognized word maps to {@link CommandType#UNKNOWN}. */
    @Test
    public void from_unrecognizedCommandWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("hello"));
    }

    /** Verifies that an empty word maps to {@link CommandType#UNKNOWN}. */
    @Test
    public void from_emptyCommandWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
    }

    /** Verifies that a null word maps to {@link CommandType#UNKNOWN}. */
    @Test
    public void from_nullCommandWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(null));
    }

    /** Verifies that command-word matching remains case-sensitive. */
    @Test
    public void from_differentlyCasedCommandWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("TODO"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("Reminder"));
    }
}
