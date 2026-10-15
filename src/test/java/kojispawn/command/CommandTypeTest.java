package kojispawn.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests recognition of command words before detailed parsing begins.
 */
public class CommandTypeTest {
    @Test
    public void getCommandTypeFromString_allSupportedCommandWords_returnsMatchingTypes() {
        assertEquals(CommandType.TODO, CommandType.getCommandTypeFromString("todo read book"));
        assertEquals(CommandType.DEADLINE,
                CommandType.getCommandTypeFromString("deadline return book /by 2019-12-02"));
        assertEquals(CommandType.EVENT,
                CommandType.getCommandTypeFromString("event meeting /from 2pm /to 4pm"));
        assertEquals(CommandType.LIST, CommandType.getCommandTypeFromString("list"));
        assertEquals(CommandType.MARK, CommandType.getCommandTypeFromString("mark 1"));
        assertEquals(CommandType.UNMARK, CommandType.getCommandTypeFromString("unmark 1"));
        assertEquals(CommandType.DELETE, CommandType.getCommandTypeFromString("delete 1"));
        assertEquals(CommandType.UNDO, CommandType.getCommandTypeFromString("undo"));
        assertEquals(CommandType.ON, CommandType.getCommandTypeFromString("on 2019-12-02"));
        assertEquals(CommandType.FIND, CommandType.getCommandTypeFromString("find book"));
        assertEquals(CommandType.BYE, CommandType.getCommandTypeFromString("bye"));
    }

    @Test
    public void getCommandTypeFromString_unrecognizedOrPartialWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.getCommandTypeFromString("todoodle"));
        assertEquals(CommandType.UNKNOWN, CommandType.getCommandTypeFromString(""));
    }
}
