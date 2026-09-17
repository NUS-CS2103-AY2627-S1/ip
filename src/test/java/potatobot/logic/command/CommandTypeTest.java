package potatobot.logic.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;

public class CommandTypeTest {
    @Test
    public void parse_supportedCommandWords_correspondingCommandTypesReturned()
            throws PotatoBotException {
        assertEquals(CommandType.ADD, CommandType.parse("add"));
        assertEquals(CommandType.LIST, CommandType.parse("list"));
        assertEquals(CommandType.FIND, CommandType.parse("find"));
        assertEquals(CommandType.UNDO, CommandType.parse("undo"));
        assertEquals(CommandType.MARK, CommandType.parse("mark"));
        assertEquals(CommandType.UNMARK, CommandType.parse("unmark"));
        assertEquals(CommandType.DELETE, CommandType.parse("delete"));
        assertEquals(CommandType.TODO, CommandType.parse("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.parse("deadline"));
        assertEquals(CommandType.EVENT, CommandType.parse("event"));
        assertEquals(CommandType.BYE, CommandType.parse("bye"));
    }

    @Test
    public void parse_unsupportedCommandWord_exceptionThrown() {
        PotatoBotException exception = assertThrows(
                PotatoBotException.class, () -> CommandType.parse("archive"));

        assertEquals("Me no gets?", exception.getMessage());
    }

    @Test
    public void parse_emptyCommandWord_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> CommandType.parse(""));
    }

    @Test
    public void parse_nullCommandWord_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> CommandType.parse(null));
    }

    @Test
    public void parse_differentlyCasedCommandWord_exceptionThrown() {
        assertThrows(PotatoBotException.class, () -> CommandType.parse("LIST"));
    }
}
