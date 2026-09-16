package tasque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void parseEvent_endBeforeStart_throwsSpecificError() {
        Parser parser = new Parser();

        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseEvent(
                "event meeting /from 2026-08-17 /to 2026-08-16"));

        assertEquals("Unless you've invented time travel, the end date can't be before the start date.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_sameDay_returnsEvent() throws TasqueException {
        Parser parser = new Parser();

        assertEquals("[E][ ] meeting (from: Aug 16 2026 to: Aug 16 2026)",
                parser.parseEvent("event meeting /from 2026-08-16 /to 2026-08-16").toString());
    }

    @Test
    public void parseTaskNumber_validTaskNumber_returnsTaskNumber() throws TasqueException {
        Parser parser = new Parser();
        int actual = parser.parseTaskNumber("delete 2", "delete", 5);
        assertEquals(2, actual);
    }

    @Test
    public void parseTaskNumber_nonNumericTaskNumber_throwsTasqueException() {
        Parser parser = new Parser();
        assertThrows(TasqueException.class, () -> parser.parseTaskNumber("delete abc", "delete", 5));
    }

    @Test
    public void parseTaskNumber_invalidIntegerText_throwsSpecificError() {
        Parser parser = new Parser();

        for (String taskNumber : new String[] {"2147483648", "1.5", "1 2"}) {
            TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseTaskNumber(
                    "delete " + taskNumber, "delete", 5), taskNumber);

            assertEquals("The task number must be a positive whole number.", exception.getMessage(), taskNumber);
        }
    }

    @Test
    public void parseTaskNumber_negativeTaskNumber_throwsTasqueException() {
        Parser parser = new Parser();
        assertThrows(TasqueException.class, () -> parser.parseTaskNumber("delete -1", "delete", 5));
    }

    @Test
    public void parseTaskNumber_missingTaskNumber_throwsTasqueException() {
        Parser parser = new Parser();
        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseTaskNumber(
                "delete", "delete", 5));
        assertEquals("The delete command needs a task number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_zeroTaskNumber_throwsTasqueException() {
        Parser parser = new Parser();
        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseTaskNumber(
                "delete 0", "delete", 5));
        assertEquals("The task number must be a positive whole number.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_taskNumberPastEnd_throwsTasqueException() {
        Parser parser = new Parser();
        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseTaskNumber(
                "delete 6", "delete", 5));
        assertEquals("Task 6 doesn't exist.", exception.getMessage());
    }

    @Test
    public void parseTaskNumber_lastTaskNumber_returnsTaskNumber() throws TasqueException {
        Parser parser = new Parser();
        int actual = parser.parseTaskNumber("delete 5", "delete", 5);
        assertEquals(5, actual);
    }

    @Test
    public void parseFindKeyword_keywordProvided_returnsKeyword() throws TasqueException {
        Parser parser = new Parser();
        String actual = parser.parseFindKeyword("find book");
        assertEquals("book", actual);
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsTasqueException() {
        Parser parser = new Parser();
        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseFindKeyword(
                "find   "));
        assertEquals("The find command needs a keyword.", exception.getMessage());
    }

    @Test
    public void parseDeadline_validLeapDay_preservesIsoDate() throws TasqueException {
        Parser parser = new Parser();

        assertEquals("D2 | 0 | cmVwb3J0 | 2028-02-29",
                parser.parseDeadline("deadline report /by 2028-02-29").toStorageString());
    }

    @Test
    public void parseDeadline_nonLeapYearFebruary29_throwsSpecificError() {
        Parser parser = new Parser();

        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseDeadline(
                "deadline report /by 2026-02-29"));

        assertEquals("Enter a valid date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseEvent_reversedDelimiters_throwsFormatError() {
        Parser parser = new Parser();

        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseEvent(
                "event meeting /to 2026-08-29 /from 2026-08-28"));

        assertEquals("Use the format: event DESCRIPTION /from START /to END.", exception.getMessage());
    }

    @Test
    public void parseEvent_validStartInvalidEnd_throwsSpecificError() {
        Parser parser = new Parser();

        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseEvent(
                "event meeting /from 2026-02-28 /to 2026-02-29"));

        assertEquals("Enter valid dates in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseEvent_validIsoDates_returnsEventWithFormattedDates() throws TasqueException {
        Parser parser = new Parser();

        String actual = parser.parseEvent(
                "event project meeting /from 2026-08-28 /to 2026-08-29").toString();

        assertEquals("[E][ ] project meeting (from: Aug 28 2026 to: Aug 29 2026)", actual);
    }

    @Test
    public void parseEvent_invalidDate_throwsTasqueException() {
        Parser parser = new Parser();

        TasqueException exception = assertThrows(TasqueException.class, () -> parser.parseEvent(
                "event project meeting /from tomorrow /to 2026-08-29"));

        assertEquals("Enter valid dates in yyyy-MM-dd format.", exception.getMessage());
    }
}
