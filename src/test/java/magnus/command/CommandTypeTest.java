package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests command-keyword parsing and task-list mutation metadata.
 */
public class CommandTypeTest {
    @Test
    public void parseKeyword_allSupportedKeywords_returnsMatchingTypes() {
        assertEquals(CommandType.BYE, CommandType.parseKeyword("bye"));
        assertEquals(CommandType.LIST, CommandType.parseKeyword("list"));
        assertEquals(CommandType.LIST_DEADLINE, CommandType.parseKeyword("list_deadline"));
        assertEquals(CommandType.LIST_EVENT, CommandType.parseKeyword("list_event"));
        assertEquals(CommandType.FIND, CommandType.parseKeyword("find"));
        assertEquals(CommandType.MARK, CommandType.parseKeyword("mark"));
        assertEquals(CommandType.UNMARK, CommandType.parseKeyword("unmark"));
        assertEquals(CommandType.TODO, CommandType.parseKeyword("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.parseKeyword("deadline"));
        assertEquals(CommandType.EVENT, CommandType.parseKeyword("event"));
        assertEquals(CommandType.UPDATE, CommandType.parseKeyword("update"));
        assertEquals(CommandType.DELETE, CommandType.parseKeyword("delete"));
    }

    @Test
    public void parseKeyword_mixedCaseKeyword_ignoresCase() {
        assertEquals(CommandType.LIST_DEADLINE, CommandType.parseKeyword("LiSt_DeAdLiNe"));
    }

    @Test
    public void parseKeyword_unknownOrNullKeyword_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> CommandType.parseKeyword("archive"));
        assertThrows(IllegalArgumentException.class, () -> CommandType.parseKeyword(null));
    }

    @Test
    public void canChangeTaskList_eachCommand_returnsExpectedMetadata() {
        Set<CommandType> mutatingCommands = EnumSet.of(
                CommandType.MARK,
                CommandType.UNMARK,
                CommandType.TODO,
                CommandType.DEADLINE,
                CommandType.EVENT,
                CommandType.DELETE);

        for (CommandType commandType : CommandType.values()) {
            if (mutatingCommands.contains(commandType)) {
                assertTrue(commandType.canChangeTaskList());
            } else {
                assertFalse(commandType.canChangeTaskList());
            }
        }
    }
}
