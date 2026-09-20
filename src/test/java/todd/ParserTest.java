package todd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the command-specific parsing behavior provided by {@link Parser}. */
public class ParserTest {
    @Test
    public void parse_supportedCommands_returnsMatchingCommand() {
        assertEquals(Command.BYE, Parser.parse("bye"));
        assertEquals(Command.LIST, Parser.parse("list"));
        assertEquals(Command.MARK, Parser.parse("mark 1"));
        assertEquals(Command.UNMARK, Parser.parse("unmark 1"));
        assertEquals(Command.TODO, Parser.parse("todo read book"));
        assertEquals(Command.DEADLINE, Parser.parse("deadline submit report /by 2026-09-04"));
        assertEquals(Command.EVENT, Parser.parse("event meeting /from 2026-09-04 /to 2026-09-05"));
        assertEquals(Command.DELETE, Parser.parse("delete 1"));
        assertEquals(Command.ON, Parser.parse("on 2026-09-04"));
        assertEquals(Command.FIND, Parser.parse("find book"));
        assertEquals(Command.HELP, Parser.parse("help"));
        assertEquals(Command.REMINDERS, Parser.parse("reminders"));
    }

    @Test
    public void parse_unsupportedCommand_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Parser.parse("dance"));
    }

    @Test
    public void parseIndex_validTaskNumber_returnsZeroBasedIndex() throws TodException {
        assertEquals(0, Parser.parseIndex("mark 1", "mark", 3));
        assertEquals(2, Parser.parseIndex("mark 3", "mark", 3));
    }

    @Test
    public void parseIndex_nonNumericTaskNumber_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseIndex("mark one", "mark", 3));
    }

    @Test
    public void parseIndex_taskNumberOutsideList_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseIndex("mark 0", "mark", 3));
        assertThrows(TodException.class, () ->
                Parser.parseIndex("mark 4", "mark", 3));
    }

    @Test
    public void parseTodo_validInput_returnsTodo() throws TodException {
        Todo result = Parser.parseTodo("todo read book");

        assertEquals("read book", result.getDescription());
    }

    @Test
    public void parseTodo_missingDescription_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseTodo("todo"));
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws TodException {
        Deadline result = Parser.parseDeadline("deadline submit report /by 2026-09-04 2359");

        assertEquals("submit report", result.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 4, 23, 59), result.getBy());
    }

    @Test
    public void parseDeadline_missingBy_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseDeadline("deadline submit report"));
    }

    @Test
    public void parseDeadline_invalidDate_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseDeadline("deadline submit report /by 2026-02-30"));
    }

    @Test
    public void parseEvent_validInput_returnsEvent() throws TodException {
        Event result = Parser.parseEvent(
                "event project meeting /from 2026-09-04 1400 /to 2026-09-04 1600");

        assertEquals("project meeting", result.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 4, 14, 0), result.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 4, 16, 0), result.getTo());
    }

    @Test
    public void parseEvent_missingFrom_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseEvent("event project meeting"));
    }

    @Test
    public void parseEvent_missingTo_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseEvent("event project meeting /from 2026-09-04 1400"));
    }

    @Test
    public void parseEvent_endBeforeStart_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseEvent("event project meeting /from 2026-09-04 1600 /to 2026-09-04 1400"));
    }

    @Test
    public void parseDate_validInput_returnsLocalDate() throws TodException {
        LocalDate result = Parser.parseDate("on 2026-09-04");

        assertEquals(LocalDate.of(2026, 9, 4), result);
    }

    @Test
    public void parseDate_invalidInput_throwsTodException() {
        assertThrows(TodException.class, () ->
                Parser.parseDate("on someday"));
    }

    @Test
    public void parseKeyword_validInput_returnsTrimmedKeyword() throws TodException {
        assertEquals("project report", Parser.parseKeyword("find   project report  "));
    }

    @Test
    public void parseKeyword_missingKeyword_throwsTodException() {
        assertThrows(TodException.class, () -> Parser.parseKeyword("find"));
    }
    @Test
    public void parse_extraWhitespace_acceptsCommandAndArguments() throws TodException {
        assertEquals(Command.TODO, Parser.parse("  todo\tread book  "));
        assertEquals("read  book", Parser.parseTodo("  todo\tread  book  ").getDescription());
        assertEquals(0, Parser.parseIndex("  mark\t1  ", "mark", 1));
        assertEquals(LocalDateTime.of(2026, 9, 4, 23, 59),
                Parser.parseDeadline("  deadline report\t/by\t2026-09-04   2359  ").getBy());
        assertEquals("meeting", Parser.parseEvent(
                "  event meeting   /from\t2026-09-04  /to   2026-09-05  ").getDescription());
    }

    @Test
    public void parseDeadline_missingOrRepeatedFields_throwsHelpfulError() {
        String[] invalidInputs = {
            "deadline /by 2026-09-04", "deadline report /by",
            "deadline report /by 2026-09-04 /by 2026-09-05"
        };
        for (String input : invalidInputs) {
            assertThrows(TodException.class, () -> Parser.parseDeadline(input), input);
        }
    }

    @Test
    public void parseEvent_missingRepeatedOrReversedFields_throwsError() {
        String[] invalidInputs = {
            "event /from 2026-09-04 /to 2026-09-05",
            "event meeting /from /to 2026-09-05",
            "event meeting /from 2026-09-04 /to",
            "event meeting /to 2026-09-05 /from 2026-09-04",
            "event meeting /from 2026-09-04 /from 2026-09-04 /to 2026-09-05",
            "event meeting /from 2026-09-04 /to 2026-09-05 /to 2026-09-06",
            "event meeting /from 2026-09-04 1400 /to 2026-09-04 1400"
        };
        for (String input : invalidInputs) {
            assertThrows(TodException.class, () -> Parser.parseEvent(input), input);
        }
    }

    @Test
    public void parseTodo_saveFileSeparators_throwsError() {
        assertThrows(TodException.class, () -> Parser.parseTodo("todo read | write"));
        assertThrows(TodException.class, () -> Parser.parseTodo("todo read\nwrite"));
        assertThrows(TodException.class, () -> Parser.parseTodo("todo read\rwrite"));
        assertThrows(TodException.class, () -> Parser.parseDeadline("deadline a | b /by today"));
        assertThrows(TodException.class, () -> Parser.parseEvent("event a | b /from today /to tomorrow"));
    }

    @Test
    public void parseIndex_overflowNegativeAndExtraArguments_throwsError() {
        for (String number : new String[]{"999999999999999999999", "-1", "1 2", "1.5", ""}) {
            assertThrows(TodException.class, () -> Parser.parseIndex("mark " + number, "mark", 2));
        }
    }
}
