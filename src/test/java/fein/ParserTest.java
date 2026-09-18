package fein;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fein.task.Task;

/** Tests the conversion of user commands into tasks. */
class ParserTest {
    /** Parser used by the test cases. */
    private final Parser parser = new Parser();

    /** Verifies that a deadline command keeps its description and date/time. */
    @Test
    void parseDeadlineCreatesCorrectTask() throws FeinException {
        Task task = parser.parseTask("deadline return book /by 2/12/2019 1800");

        assertEquals("return book", task.getDescription());
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", task.toString());
    }

    /** Verifies that a two-digit numeric year is interpreted as a year from 2000 to 2099. */
    @Test
    void parseDeadlineAcceptsTwoDigitYear() throws FeinException {
        Task task = parser.parseTask("deadline submit report /by 23/6/26 1800");

        assertEquals("[D][ ] submit report (by: Jun 23 2026, 6:00 PM)", task.toString());
    }

    /** Verifies that both supported numeric year lengths work when a deadline has no time. */
    @Test
    void parseDeadlineAcceptsTwoAndFourDigitDateOnlyYears() throws FeinException {
        Task shortYearTask = parser.parseTask("deadline submit report /by 23/6/26");
        Task longYearTask = parser.parseTask("deadline submit report /by 23/6/2026");

        assertEquals("[D][ ] submit report (by: 23/6/26)", shortYearTask.toString());
        assertEquals("[D][ ] submit report (by: 23/6/2026)", longYearTask.toString());
    }

    /** Verifies that an unrecognised command is rejected. */
    @Test
    void parseInvalidCommandThrowsException() {
        assertThrows(FeinException.class, () -> parser.parseTask("unknown command"));
    }

    /** Verifies that a find command returns its trimmed keyword. */
    @Test
    void parseFindCommandReturnsKeyword() throws FeinException {
        assertEquals("book", parser.parseFindKeyword("find book"));
    }

    /** Verifies that invalid date-looking text is not accepted as a natural-language date. */
    @Test
    void parseTaskRejectsInvalidNumericDateTime() {
        assertThrows(FeinException.class,
                () -> parser.parseTask("deadline submit report /by 31/2/2026 1800"));
    }

    /** Verifies that a date-only numeric deadline is rejected rather than treated as ordinary text. */
    @Test
    void parseTaskRejectsInvalidNumericDateWithoutTime() {
        assertThrows(FeinException.class,
                () -> parser.parseTask("deadline submit report /by 31/6/2026"));
    }

    /** Verifies that numeric event endpoints must form a forward-moving time range. */
    @Test
    void parseTaskRejectsEventEndingBeforeItStarts() {
        assertThrows(FeinException.class,
                () -> parser.parseTask("event workshop /from 2/12/2026 1800 /to 2/12/2026 1700"));
    }

    /** Verifies that task fields cannot contain Fein's save-file separator. */
    @Test
    void parseTaskRejectsSaveFileSeparator() {
        assertThrows(FeinException.class, () -> parser.parseTask("todo buy | milk"));
    }

    /** Verifies that a deadline does not accept its due-date marker more than once. */
    @Test
    void parseTaskRejectsDuplicateDeadlineMarker() {
        assertThrows(FeinException.class,
                () -> parser.parseTask("deadline submit report /by Friday /by Saturday"));
    }

    /** Verifies that event markers must appear once and in their required order. */
    @Test
    void parseTaskRejectsMisorderedEventMarkers() {
        assertThrows(FeinException.class,
                () -> parser.parseTask("event workshop /to 4pm /from 2pm"));
    }
}
