package ollie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseCommand_supportedAndUnknownCommands_returnsCorrectTypes() {
        assertEquals(CommandType.HELP, parser.parseCommand("help"));
        assertEquals(CommandType.LIST, parser.parseCommand("list"));
        assertEquals(CommandType.FIND, parser.parseCommand("find book"));
        assertEquals(CommandType.TODO, parser.parseCommand("todo read book"));
        assertEquals(CommandType.DEADLINE, parser.parseCommand("deadline return book /by 2019-12-02"));
        assertEquals(CommandType.EVENT, parser.parseCommand("event meeting /from 2019-12-03 /to 2019-12-05"));
        assertEquals(CommandType.MARK, parser.parseCommand("mark 1"));
        assertEquals(CommandType.UNMARK, parser.parseCommand("unmark 1"));
        assertEquals(CommandType.DELETE, parser.parseCommand("delete 1"));
        assertEquals(CommandType.BYE, parser.parseCommand("bye"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommand("unknown"));
    }

    @Test
    public void parseCommand_irregularWhitespace_returnsCorrectType() {
        assertEquals(CommandType.DEADLINE,
                parser.parseCommand("  deadline   return book   /by   2019-12-02  "));
        assertEquals(CommandType.EVENT,
                parser.parseCommand("\tevent\tmeeting\t/from\t2019-12-03\t/to\t2019-12-05\t"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommand("   "));
        assertEquals(CommandType.UNKNOWN, parser.parseCommand(null));
    }

    @Test
    public void parseFindKeyword_validAndMissingKeyword_returnsKeywordOrThrowsException()
            throws OllieException {
        assertEquals("book", parser.parseFindKeyword("find book"));
        assertThrows(OllieException.class, () -> parser.parseFindKeyword("find"));
    }

    @Test
    public void parseTask_validDeadline_returnsParsedDeadline() throws OllieException {
        Task task = parser.parseTask("  deadline   return book   /by   2019-12-02  ");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getDueDate());
    }

    @Test
    public void parseTask_validEvent_returnsParsedEvent() throws OllieException {
        Task task = parser.parseTask("event meeting /from 2019-12-03 /to 2019-12-05");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("meeting", event.getDescription());
        assertEquals(LocalDate.of(2019, 12, 3), event.getStartDate());
        assertEquals(LocalDate.of(2019, 12, 5), event.getEndDate());
    }

    @Test
    public void parseTask_invalidOrIncompleteCommands_throwsOllieException() {
        assertThrows(OllieException.class, () -> parser.parseTask(""));
        assertThrows(OllieException.class, () -> parser.parseTask("unknown"));
        assertThrows(OllieException.class, () -> parser.parseTask("todo"));
        assertThrows(OllieException.class, () -> parser.parseTask("deadline return book"));
        assertThrows(OllieException.class, () -> parser.parseTask("deadline return book /by 2019-02-30"));
        assertThrows(OllieException.class, () -> parser.parseTask("event meeting /from 2019-12-03"));
        assertThrows(OllieException.class, () -> parser.parseTask(
                "deadline return /by 2019-12-02 /by 2019-12-03"));
        assertThrows(OllieException.class, () -> parser.parseTask("deadline return /from 2019-12-02"));
        assertThrows(OllieException.class, () -> parser.parseTask(
                "event meeting /from 2019-12-03 /from 2019-12-04 /to 2019-12-05"));
        assertThrows(OllieException.class, () -> parser.parseTask(
                "event meeting /to 2019-12-05 /from 2019-12-03"));
        assertThrows(OllieException.class, () -> parser.parseTask("todo read | book"));
    }

    @Test
    public void parseTask_eventWithInvalidDateRange_throwsOllieException() {
        OllieException sameDateException = assertThrows(OllieException.class, () -> parser.parseTask(
                "event meeting /from 2019-12-03 /to 2019-12-03"));
        OllieException reversedDateException = assertThrows(OllieException.class, () -> parser.parseTask(
                "event meeting /from 2019-12-05 /to 2019-12-03"));

        assertTrue(sameDateException.getMessage().contains("before"));
        assertTrue(reversedDateException.getMessage().contains("before"));
    }

    @Test
    public void validateNoArguments_extraArguments_throwsOllieException() {
        assertThrows(OllieException.class, () -> parser.validateNoArguments("list now", "list"));
        assertThrows(OllieException.class, () -> parser.validateNoArguments("bye please", "bye"));
    }

    @Test
    public void parseTaskIndex_validTaskNumber_returnsZeroBasedIndex() throws OllieException {
        assertEquals(1, parser.parseTaskIndex("mark 2", "mark", 3));
        assertEquals(1, parser.parseTaskIndex("  mark    2  ", "mark", 3));
    }

    @Test
    public void parseTaskIndex_invalidTaskNumbers_throwOllieException() {
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("mark", "mark", 3));
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("mark two", "mark", 3));
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("mark 1 2", "mark", 3));
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("mark +1", "mark", 3));
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("delete 1", "mark", 3));
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("mark 0", "mark", 3));
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("mark 4", "mark", 3));
        assertThrows(OllieException.class, () -> parser.parseTaskIndex("mark 1", "mark", 0));
    }
}
