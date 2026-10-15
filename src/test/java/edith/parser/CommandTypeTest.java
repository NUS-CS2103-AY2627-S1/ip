package edith.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Tests recognition of complete command inputs. */
public class CommandTypeTest {
    @Test
    public void fromInput_eachExactKeyword_returnsMatchingCommandType() {
        for (CommandType commandType : CommandType.values()) {
            assertEquals(commandType, CommandType.fromInput(commandType.getKeyword()));
        }
    }

    @Test
    public void fromInput_argumentCommandWithArguments_returnsMatchingCommandType() {
        assertEquals(CommandType.TODO, CommandType.fromInput("todo read book"));
        assertEquals(CommandType.DEADLINE, CommandType.fromInput("deadline return book /by 2026-09-06"));
        assertEquals(CommandType.EVENT,
                CommandType.fromInput("event meeting /from 2026-09-06 /to 2026-09-07"));
        assertEquals(CommandType.MARK, CommandType.fromInput("mark 1"));
        assertEquals(CommandType.UNMARK, CommandType.fromInput("unmark 1"));
        assertEquals(CommandType.DELETE, CommandType.fromInput("delete 1"));
        assertEquals(CommandType.TAG, CommandType.fromInput("tag 2 #fun"));
        assertEquals(CommandType.UNTAG, CommandType.fromInput("untag 2 #fun"));
        assertEquals(CommandType.FIND, CommandType.fromInput("find read book"));
    }

    @Test
    public void fromInput_noArgumentCommandWithArguments_returnsNull() {
        assertNull(CommandType.fromInput("list now"));
        assertNull(CommandType.fromInput("help now"));
        assertNull(CommandType.fromInput("bye now"));
    }

    @Test
    public void fromInput_unknownOrPartialKeyword_returnsNull() {
        assertNull(CommandType.fromInput(""));
        assertNull(CommandType.fromInput("todos"));
        assertNull(CommandType.fromInput("marking 1"));
        assertNull(CommandType.fromInput(" todo read book"));
    }
}
