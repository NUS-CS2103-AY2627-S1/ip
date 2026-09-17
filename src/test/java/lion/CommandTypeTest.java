package lion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests command-word recognition by {@link CommandType}.
 */
public class CommandTypeTest {

    @Test
    void from_allKnownCommands_returnsMatchingCommandType() {
        assertEquals(CommandType.LIST, CommandType.from("list"));
        assertEquals(CommandType.TODO, CommandType.from("todo read book"));
        assertEquals(CommandType.DEADLINE,
                CommandType.from("deadline return book /by 2/12/2019 1800"));
        assertEquals(CommandType.EVENT,
                CommandType.from("event party /from 2/12/2019 1800 /to 2/12/2019 2000"));
        assertEquals(CommandType.MARK, CommandType.from("mark 1"));
        assertEquals(CommandType.UNMARK, CommandType.from("unmark 1"));
        assertEquals(CommandType.DELETE, CommandType.from("delete 1"));
        assertEquals(CommandType.FIND, CommandType.from("find book"));
        assertEquals(CommandType.HELP, CommandType.from("help"));
        assertEquals(CommandType.BYE, CommandType.from("bye"));
    }

    @Test
    void from_mixedCaseAndExtraWhitespace_returnsMatchingCommandType() {
        assertEquals(CommandType.TODO, CommandType.from("  ToDo   read book  "));
    }

    @Test
    void from_emptyOrInvalidInput_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
        assertEquals(CommandType.UNKNOWN, CommandType.from("   "));
        assertEquals(CommandType.UNKNOWN, CommandType.from("nonsense command"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("listing"));
    }

    @Test
    void from_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> CommandType.from(null));
    }
}
