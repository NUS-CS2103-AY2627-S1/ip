package slotbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandTypeTest {

    @Test
    public void getCommandType_knownCommands_returnsMatchingTypes() {
        assertEquals(CommandType.BYE, CommandType.getCommandType("bye"));
        assertEquals(CommandType.LIST, CommandType.getCommandType("list"));
        assertEquals(CommandType.REMINDERS, CommandType.getCommandType("reminders"));
        assertEquals(CommandType.MARK, CommandType.getCommandType("mark"));
        assertEquals(CommandType.UNMARK, CommandType.getCommandType("unmark"));
        assertEquals(CommandType.DELETE, CommandType.getCommandType("delete"));
        assertEquals(CommandType.FIND, CommandType.getCommandType("find"));
        assertEquals(CommandType.TODO, CommandType.getCommandType("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.getCommandType("deadline"));
        assertEquals(CommandType.EVENT, CommandType.getCommandType("event"));
    }

    @Test
    public void getCommandType_unknownOrWrongCase_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.getCommandType(""));
        assertEquals(CommandType.UNKNOWN, CommandType.getCommandType("Todo"));
        assertEquals(CommandType.UNKNOWN, CommandType.getCommandType("schedule"));
    }
}
