package gary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import gary.task.Deadline;
import gary.task.Event;
import gary.task.Todo;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_helpWithAndWithoutArguments_returnsHelpOrUnknown() {
        assertEquals(CommandType.HELP, parser.parseCommandType("help"));
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("help extra"));
    }

    @Test
    void parseCommandType_variedWhitespaceAndCase_returnsCommandType() {
        assertEquals(CommandType.LIST, parser.parseCommandType("  LIST  "));
        assertEquals(CommandType.TODO, parser.parseCommandType("\ttodo\tread book"));
    }

    @Test
    void parseTask_supportedTypes_returnsMatchingTasks() {
        assertInstanceOf(Todo.class,
                parser.parseTask("todo read book", CommandType.TODO));
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask("deadline return book /by 2019-12-02", CommandType.DEADLINE));
        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event meeting /from 2019-12-02 /to 2019-12-03", CommandType.EVENT));

        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals(LocalDate.of(2019, 12, 2), event.getFrom());
        assertEquals(LocalDate.of(2019, 12, 3), event.getTo());
    }

    @Test
    void parseTask_invalidDetails_throwsException() {
        Executable parseTodoWithoutDescription = () -> parser.parseTask("todo", CommandType.TODO);
        Executable parseDeadlineWithInvalidDate = () -> parser.parseTask(
                "deadline report /by tomorrow", CommandType.DEADLINE);
        Executable parseEventWithInvalidDates = () -> parser.parseTask(
                "event meeting /from Monday /to Tuesday", CommandType.EVENT);
        Executable parseDeadlineWithDayFirstDate = () -> parser.parseTask(
                "deadline report /by 02-12-2019", CommandType.DEADLINE);
        Executable parseDeadlineWithImpossibleDate = () -> parser.parseTask(
                "deadline report /by 2019-02-31", CommandType.DEADLINE);
        Executable parseEventWithReversedDates = () -> parser.parseTask(
                "event trip /from 2019-12-03 /to 2019-12-02", CommandType.EVENT);
        Executable parseTodoWithStorageDelimiter = () -> parser.parseTask(
                "todo compare red | blue", CommandType.TODO);
        Executable parseDeadlineWithStorageDelimiter = () -> parser.parseTask(
                "deadline compare red | blue /by 2019-12-02", CommandType.DEADLINE);
        Executable parseEventWithStorageDelimiter = () -> parser.parseTask(
                "event compare red | blue /from 2019-12-02 /to 2019-12-03", CommandType.EVENT);

        assertThrows(IllegalArgumentException.class, parseTodoWithoutDescription);
        assertThrows(IllegalArgumentException.class, parseDeadlineWithInvalidDate);
        assertThrows(IllegalArgumentException.class, parseEventWithInvalidDates);
        assertThrows(IllegalArgumentException.class, parseDeadlineWithDayFirstDate);
        assertThrows(IllegalArgumentException.class, parseDeadlineWithImpossibleDate);
        assertThrows(IllegalArgumentException.class, parseEventWithReversedDates);
        assertThrows(IllegalArgumentException.class, parseTodoWithStorageDelimiter);
        assertThrows(IllegalArgumentException.class, parseDeadlineWithStorageDelimiter);
        assertThrows(IllegalArgumentException.class, parseEventWithStorageDelimiter);
    }

    @Test
    void parseTask_uppercaseSeparators_returnsMatchingTasks() {
        assertInstanceOf(Deadline.class,
                parser.parseTask("DEADLINE report /BY 2019-12-02", CommandType.DEADLINE));
        assertInstanceOf(Event.class,
                parser.parseTask("EVENT meeting /FROM 2019-12-02 /TO 2019-12-03", CommandType.EVENT));
    }

    @Test
    void parseTaskNumber_variedInputs_returnsValidatedNumber() {
        assertEquals(2, parser.parseTaskNumber("mark 2", 3));
        assertEquals(-1, parser.parseTaskNumber("mark 0", 3));
        assertEquals(-1, parser.parseTaskNumber("mark 4", 3));
        assertEquals(-1, parser.parseTaskNumber("mark two", 3));
    }

    @Test
    void parseKeyword_presentAndMissing_returnsOrThrows() {
        Executable parseMissingKeyword = () -> parser.parseKeyword("find");

        assertEquals("read book", parser.parseKeyword("find read book"));
        assertThrows(IllegalArgumentException.class, parseMissingKeyword);
    }
}
