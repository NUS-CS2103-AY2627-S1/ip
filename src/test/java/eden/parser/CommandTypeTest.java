package eden.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests recognition of user command words.
 */
public class CommandTypeTest {
    /**
     * Verifies that find is recognized regardless of surrounding whitespace or case.
     */
    @Test
    public void from_findCommandWithMixedCaseAndWhitespace_returnsFind() {
        assertEquals(CommandType.FIND, CommandType.from("find book"));
        assertEquals(CommandType.FIND, CommandType.from("  FiNd   BOOK  "));
        assertEquals(CommandType.FIND, CommandType.from("find"));
    }

    /**
     * Verifies recognition of every supported command word.
     */
    @Test
    public void from_allSupportedCommandWords_returnsMatchingTypes() {
        assertEquals(CommandType.BYE, CommandType.from("bye"));
        assertEquals(CommandType.LIST, CommandType.from("list"));
        assertEquals(CommandType.MARK, CommandType.from("mark 1"));
        assertEquals(CommandType.UNMARK, CommandType.from("unmark 1"));
        assertEquals(CommandType.TODO, CommandType.from("todo task"));
        assertEquals(CommandType.DEADLINE,
                CommandType.from("deadline task /by 2026-09-30"));
        assertEquals(CommandType.EVENT,
                CommandType.from("event task /from 2pm /to 3pm"));
        assertEquals(CommandType.DELETE, CommandType.from("delete 1"));
    }

    /**
     * Verifies that blank and unsupported inputs map to the safe unknown type.
     */
    @Test
    public void from_blankOrUnsupportedInput_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
        assertEquals(CommandType.UNKNOWN, CommandType.from("   "));
        assertEquals(CommandType.UNKNOWN, CommandType.from("archive 1"));
    }
}
