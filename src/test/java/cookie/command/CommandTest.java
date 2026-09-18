package cookie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests conversion between command words and {@link Command} values. */
public class CommandTest {
    @Test
    public void fromString_validCommand_returnsMatchingCommand() throws CookieException {
        assertEquals(Command.TODO, Command.fromString("todo"));
        assertEquals(Command.DEADLINE, Command.fromString("DeAdLiNe"));
        assertEquals(Command.EVENT, Command.fromString("EVENT"));
        assertEquals(Command.FIND, Command.fromString("find"));
        assertEquals(Command.SORT, Command.fromString("SoRt"));
    }

    @Test
    public void fromString_unknownCommand_throwsCookieException() {
        CookieException exception = assertThrows(CookieException.class, () -> Command.fromString("remember"));
        assertEquals("What is that command!?", exception.getMessage());
    }
}
