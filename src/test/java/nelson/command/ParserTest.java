package nelson.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nelson.exception.NelsonException;

/** Tests command recognition and argument extraction in {@link Parser}. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_listCommand_returnsListCommand() throws NelsonException {
        Parser.Command command = parser.parse("list");

        assertEquals(Parser.Type.LIST, command.getType());
    }

    @Test
    public void parseTodoCommand_returnsDescription() throws NelsonException {
        Parser.Command command = parser.parse("todo read book");

        assertEquals(Parser.Type.TODO, command.getType());
        assertEquals("read book", command.getArgument(0));
    }

    @Test
    public void parseDeadlineCommand_returnsDescriptionAndDate() throws NelsonException {
        Parser.Command command = parser.parse("deadline submit report /by 2026-08-25");

        assertEquals(Parser.Type.DEADLINE, command.getType());
        assertEquals("submit report", command.getArgument(0));
        assertEquals("2026-08-25", command.getArgument(1));
    }

    @Test
    public void parseEventCommand_returnsDescriptionAndDates() throws NelsonException {
        Parser.Command command = parser.parse("event team meeting /from 2026-08-25 /to 2026-08-26");

        assertEquals(Parser.Type.EVENT, command.getType());
        assertEquals("team meeting", command.getArgument(0));
        assertEquals("2026-08-25", command.getArgument(1));
        assertEquals("2026-08-26", command.getArgument(2));
    }

    @Test
    public void parseMarkCommand_returnsTaskNumber() throws NelsonException {
        Parser.Command command = parser.parse("mark 3");

        assertEquals(Parser.Type.MARK, command.getType());
        assertEquals("3", command.getArgument(0));
    }

    @Test
    public void parseUnmarkCommand_returnsTaskNumber() throws NelsonException {
        Parser.Command command = parser.parse("unmark 3");

        assertEquals(Parser.Type.UNMARK, command.getType());
        assertEquals("3", command.getArgument(0));
    }

    @Test
    public void parseDeleteCommand_returnsTaskNumber() throws NelsonException {
        Parser.Command command = parser.parse("delete 3");

        assertEquals(Parser.Type.DELETE, command.getType());
        assertEquals("3", command.getArgument(0));
    }

    @Test
    public void parseFindCommand_returnsKeyword() throws NelsonException {
        Parser.Command command = parser.parse("find book");

        assertEquals(Parser.Type.FIND, command.getType());
        assertEquals("book", ((FindCommand) command).getKeyword());
    }

    @Test
    public void parseFindWithoutKeyword_throwsNelsonException() {
        assertThrows(
            NelsonException.class, () -> parser.parse("find"));
    }

    @Test
    public void parseEmptyTodo_throwsNelsonException() {
        NelsonException exception = assertThrows(NelsonException.class, () -> parser.parse("todo"));

        assertEquals(
            "Molo! An empty move? You must provide a description, you amateur.", exception.getMessage());
    }

    @Test
    public void parseDeadlineWithoutDate_throwsNelsonException() {
        assertThrows(
            NelsonException.class, () -> parser.parse("deadline submit report"));
    }

    @Test
    public void parseEventWithoutTimeParameters_throwsNelsonException() {
        assertThrows(
            NelsonException.class, () -> parser.parse("event team meeting"));
    }

    @Test
    public void parseUnknownCommand_throwsNelsonException() {
        assertThrows(
            NelsonException.class, () -> parser.parse("schedule meeting"));
    }
}
