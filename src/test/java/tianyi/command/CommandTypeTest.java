package tianyi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tianyi.TianyiException;

/**
 * Tests conversion from command keywords to command types.
 */
public class CommandTypeTest {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "I'm sorry, but I don't know what that means.";

    @Test
    public void from_allSupportedKeywords_returnsMatchingCommandType()
            throws TianyiException {
        assertSame(CommandType.BYE, CommandType.from("bye"));
        assertSame(CommandType.LIST, CommandType.from("list"));
        assertSame(CommandType.TODO, CommandType.from("todo"));
        assertSame(CommandType.DEADLINE, CommandType.from("deadline"));
        assertSame(CommandType.EVENT, CommandType.from("event"));
        assertSame(CommandType.MARK, CommandType.from("mark"));
        assertSame(CommandType.UNMARK, CommandType.from("unmark"));
        assertSame(CommandType.DELETE, CommandType.from("delete"));
        assertSame(CommandType.FIND, CommandType.from("find"));
        assertSame(CommandType.HELP, CommandType.from("help"));
    }

    @Test
    public void from_mixedCaseKeyword_returnsMatchingCommandType()
            throws TianyiException {
        assertSame(CommandType.TODO, CommandType.from("ToDo"));
    }

    @Test
    public void toString_byeType_returnsDisplayForm() {
        assertEquals("[bye]", CommandType.BYE.toString());
    }

    @Test
    public void from_emptyKeyword_exceptionThrown() {
        TianyiException exception = assertThrows(
                TianyiException.class, () -> CommandType.from(""));

        assertEquals(UNKNOWN_COMMAND_MESSAGE, exception.getMessage());
    }

    @Test
    public void from_unknownKeyword_exceptionThrown() {
        TianyiException exception = assertThrows(
                TianyiException.class, () -> CommandType.from("abracadabra"));

        assertEquals(UNKNOWN_COMMAND_MESSAGE, exception.getMessage());
    }

    @Test
    public void getCommands_allCommandTypes_returnsDescriptionsAndExamples() {
        String commands = CommandType.getCommands();

        for (CommandType type : CommandType.values()) {
            assertTrue(commands.contains(type.toString()));
            assertTrue(commands.contains("Example: " + type.getExample()));
        }
    }
}
