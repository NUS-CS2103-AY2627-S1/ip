package nori.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import nori.NoriException;
import nori.task.Deadline;
import nori.task.Event;

class ParserTest {
    private static final String DEADLINE_FORMAT_ERROR =
            "Use yyyy-MM-dd HHmm or d/M/yyyy HHmm for deadline dates and times.";
    private static final String DEADLINE_USAGE_ERROR =
            "Use: deadline DESCRIPTION /by yyyy-MM-dd HHmm or d/M/yyyy HHmm";

    private final Parser parser = new Parser();

    @Test
    void parseTask_eventWithMissingFields_throwsNoriException() {
        String[] commands = {"event /from Monday /to Tuesday", "event meeting /from /to Tuesday",
            "event meeting /from Monday /to", "event meeting /to Tuesday /from Monday"};
        for (String command : commands) {
            assertThrows(NoriException.class, () -> parser.parseTask(command), command);
        }
    }

    @Test
    void parseTask_eventWithFreeText_preservesBothEndpoints() throws NoriException {
        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event team meeting /from Friday 2pm /to Friday 3pm"));

        assertEquals("team meeting", event.getDescription());
        assertEquals("Friday 2pm", event.getFrom());
        assertEquals("Friday 3pm", event.getTo());
    }

    @Test
    void parseTask_leapDay_acceptsOnlyLeapYears() throws NoriException {
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask("deadline leap day /by 2028-02-29 0000"));

        assertEquals(LocalDate.of(2028, 2, 29), deadline.getBy());
        assertEquals(LocalTime.MIDNIGHT, deadline.getTime());
        assertThrows(NoriException.class, () -> parser.parseTask("deadline invalid /by 2026-02-29 0000"));
    }

    @Test
    void parseTask_isoDeadline_returnsDeadline() throws NoriException {
        Deadline deadline = assertInstanceOf(
                Deadline.class, parser.parseTask("deadline submit report /by 2019-12-03 1800"));

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 3), deadline.getBy());
        assertEquals(LocalTime.of(18, 0), deadline.getTime());
    }

    @Test
    void parseTask_slashDeadline_returnsDeadline() throws NoriException {
        Deadline deadline = assertInstanceOf(
                Deadline.class, parser.parseTask("deadline return book /by 2/12/2019 1800"));

        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals(LocalTime.of(18, 0), deadline.getTime());
    }

    @Test
    void parseTask_invalidDeadline_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline return book /by 31/2/2019 1800"));

        assertEquals(DEADLINE_FORMAT_ERROR, exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithoutArguments_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline"));

        assertEquals(DEADLINE_USAGE_ERROR, exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithoutDescription_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline /by 2019-12-03 1800"));

        assertEquals("A deadline needs both a description and /by value.", exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithoutSlashBeforeBy_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline return book by 2019-12-03 1800"));

        assertEquals(DEADLINE_USAGE_ERROR, exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithoutTime_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline return book /by 2019-12-03"));

        assertEquals(DEADLINE_FORMAT_ERROR, exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithExtraArgument_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline return book /by 2019-12-03 1800 extra"));

        assertEquals(DEADLINE_FORMAT_ERROR, exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithInvalidHour_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline return book /by 2019-12-03 2500"));

        assertEquals(DEADLINE_FORMAT_ERROR, exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithInvalidMinute_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTask("deadline return book /by 2019-12-03 1860"));

        assertEquals(DEADLINE_FORMAT_ERROR, exception.getMessage());
    }

    @Test
    void parseTaskIndex_validNumber_returnsIndex() throws NoriException {
        assertEquals(1, parser.parseTaskIndex("mark 2", "mark", 3));
    }

    @Test
    void parseTaskIndex_outOfRangeNumber_throwsException() {
        assertThrows(NoriException.class, () ->
                parser.parseTaskIndex("delete 4", "delete", 3));
    }

    @Test
    void parseTaskIndex_multipleNumbers_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseTaskIndex("mark 1 2", "mark", 3));

        assertEquals("The task number must be a whole number.", exception.getMessage());
    }

    @Test
    void parseCommandType_findCommand_returnsFind() {
        assertEquals(CommandType.FIND, parser.parseCommandType("find book"));
    }

    @Test
    void parseCommandType_helpCommand_returnsHelp() {
        assertEquals(CommandType.HELP, parser.parseCommandType("help"));
    }

    @Test
    void parseCommandType_helpWithArguments_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.parseCommandType("help commands"));
    }

    @Test
    void parseFindKeyword_validCommand_returnsKeyword() throws NoriException {
        assertEquals("return book", parser.parseFindKeyword("find return book"));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsException() {
        NoriException exception = assertThrows(NoriException.class, () ->
                parser.parseFindKeyword("find"));

        assertEquals("Please provide a keyword after find.", exception.getMessage());
    }
}
