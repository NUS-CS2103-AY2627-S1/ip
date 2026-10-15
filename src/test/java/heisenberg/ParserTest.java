package heisenberg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void getCommand_validCommands_returnsCorrectCommandType() {
        assertEquals(CommandType.TODO, new Parser("todo read book").getCommand());
        assertEquals(CommandType.DEADLINE,
                new Parser("deadline submit report /by 2026-09-01 1800").getCommand());
        assertEquals(CommandType.EVENT,
                new Parser("event meeting /from 2026-09-01 1400 /to 2026-09-01 1600").getCommand());
        assertEquals(CommandType.LIST, new Parser("list").getCommand());
        assertEquals(CommandType.MARK, new Parser("mark 1").getCommand());
        assertEquals(CommandType.DELETE, new Parser("delete 1").getCommand());
        assertEquals(CommandType.SORT, new Parser("sort").getCommand());
        assertEquals(CommandType.BYE, new Parser("bye").getCommand());
    }

    @Test
    public void constructor_invalidCommand_throwsInvalidCommandException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class,
                () -> new Parser("unknown command"));

        assertEquals("Unknown command was given.", exception.getMessage());
    }

    @Test
    public void constructor_emptyInput_throwsInvalidCommandException() {
        assertThrows(InvalidCommandException.class, () -> new Parser("   "));
    }
}
