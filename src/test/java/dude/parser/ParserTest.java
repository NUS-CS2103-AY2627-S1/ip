package dude.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dude.command.core.CommandType;
import dude.exception.UsageException;

/**
 * Tests command parsing.
 */
public class ParserTest {
    @Test
    public void parseUndo_returnsUndoRequest() throws Exception {
        assertEquals(CommandType.UNDO, Parser.parse("undo").type());
    }

    @Test
    public void parseListWithArgument_rejectsUnexpectedArgument() {
        UsageException exception = assertThrows(UsageException.class,
                () -> Parser.parse("list extra"));
        assertEquals(CommandType.LIST.getWord(), exception.getAction());
        assertEquals("argument", exception.getFieldName());
    }

    @Test
    public void parseByeWithArgument_rejectsUnexpectedArgument() {
        assertThrows(UsageException.class, () -> Parser.parse("bye later"));
    }

    @Test
    public void parseUndoWithArgument_rejectsUnexpectedArgument() {
        assertThrows(UsageException.class, () -> Parser.parse("undo one"));
    }

    @Test
    public void parseBlankInput_rejectsMissingCommand() {
        UsageException exception = assertThrows(UsageException.class, () -> Parser.parse("   "));
        assertEquals("bye, list, find, on, mark, unmark, delete, todo, deadline, event, undo",
                exception.getExpectedType());
    }

    @Test
    public void parseUnknownCommand_reportsCompleteSupportedCommandSet() {
        UsageException exception = assertThrows(UsageException.class,
                () -> Parser.parse("unknown"));
        assertEquals("bye, list, find, on, mark, unmark, delete, todo, deadline, event, undo",
                exception.getExpectedType());
    }
}
