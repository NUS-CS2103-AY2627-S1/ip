package alpha.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import alpha.AlphaException;
import alpha.task.Deadline;
import alpha.task.Event;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** Tests conversion of user input into typed Moss commands. */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies that day-first date/time text becomes a typed deadline. */
    @Test
    void parsesDeadlineWithDayFirstDateAndTime() throws AlphaException {
        Parser.Command command = this.parser.parse("deadline return book /by 2/12/2019 1800");

        assertEquals(Parser.CommandType.ADD, command.getType());
        Deadline deadline = assertInstanceOf(Deadline.class, command.getTask());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019 18:00)", deadline.toString());
    }

    /** Verifies that event boundaries are parsed into typed date/time values. */
    @Test
    void parsesEventWithStartAndEndDateTimes() throws AlphaException {
        Parser.Command command = this.parser.parse(
                "event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");

        Event event = assertInstanceOf(Event.class, command.getTask());
        assertEquals(LocalDateTime.of(2019, 10, 15, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 10, 15, 16, 0), event.getTo());
    }

    /** Verifies that a find command preserves its keyword for task-list searching. */
    @Test
    void parsesFindCommand() throws AlphaException {
        Parser.Command command = this.parser.parse("find Book");

        assertEquals(Parser.CommandType.FIND, command.getType());
        assertEquals("Book", command.getKeyword());
    }

    /** Verifies that the sort command is converted into a sort operation. */
    @Test
    void parsesSortCommand() throws AlphaException {
        Parser.Command command = this.parser.parse("sort");

        assertEquals(Parser.CommandType.SORT, command.getType());
    }

    /** Verifies that incomplete, invalid, and non-numeric commands are rejected. */
    @Test
    void rejectsMissingTaskDetailsAndInvalidDates() {
        assertThrows(AlphaException.class, () -> this.parser.parse("todo"));
        assertThrows(AlphaException.class,
                () -> this.parser.parse("deadline submit report /by 2019-02-30"));
        assertThrows(AlphaException.class,
                () -> this.parser.parse("mark not-a-number"));
        assertThrows(AlphaException.class, () -> this.parser.parse("find"));
    }

    /** Verifies that a missing command is reported as a user error instead of a null-pointer failure. */
    @Test
    void rejectsNullInputWithUserFriendlyError() {
        AlphaException exception = assertThrows(AlphaException.class, () -> this.parser.parse(null));

        assertEquals("Please enter a command.", exception.getMessage());
    }
}
