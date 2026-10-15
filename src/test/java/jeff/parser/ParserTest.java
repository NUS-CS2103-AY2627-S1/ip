package jeff.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.UpdateField;

public class ParserTest {

    @Test
    public void parseDeadline_validInput_returnsDeadline() {
        Deadline deadline = Parser.parseDeadline(
                "deadline return book /by 2026-09-15");

        assertEquals("return book", deadline.getDescription());
        assertEquals(
                LocalDate.of(2026, 9, 15),
                deadline.getDueDate());
    }

    @Test
    public void parseDeadline_missingSeparator_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseDeadline(
                        "deadline return book 2026-09-15"));

        assertEquals(
                "A deadline must use: "
                        + "deadline <description> /by <date>",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseDeadline(
                        "deadline /by 2026-09-15"));

        assertEquals(
                "A deadline needs a description.",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_missingDate_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseDeadline(
                        "deadline return book /by "));

        assertEquals(
                "A deadline needs a date.",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_invalidDate_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseDeadline(
                        "deadline return book /by 2026-20-50"));

        assertEquals(
                "Please enter the date in YYYY-MM-DD format.",
                exception.getMessage());
    }

    @Test
    public void parseFindKeyword_validInput_returnsKeyword() {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseFindKeyword("find"));

        assertEquals(
                "A find command needs a keyword.",
                exception.getMessage());
    }

    @Test
    public void parseUpdate_validInput_returnsUpdateRequest() {
        UpdateRequest request = Parser.parseUpdate(
                "update 3 /to 1800");

        assertEquals(3, request.getTaskNumber());
        assertEquals(UpdateField.END_TIME, request.getField());
        assertEquals("1800", request.getValue());
    }

    @Test
    public void parseUpdate_missingTaskNumber_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseUpdate("update"));

        assertEquals(
                "Please enter a task number.",
                exception.getMessage());
    }

    @Test
    public void parseUpdate_invalidTaskNumber_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseUpdate(
                        "update abc /description read novel"));

        assertEquals(
                "Please enter a valid task number.",
                exception.getMessage());
    }

    @Test
    public void parseUpdate_missingField_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseUpdate("update 1"));

        assertEquals(
                "An update command needs a field.",
                exception.getMessage());
    }

    @Test
    public void parseUpdate_unknownField_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseUpdate("update 1 /unknown value"));

        assertEquals(
                "Valid update fields are: "
                        + "/description, /by, /from, /to.",
                exception.getMessage());
    }

    @Test
    public void parseUpdate_missingValue_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseUpdate(
                        "update 1 /description"));

        assertEquals(
                "An update command needs a new value.",
                exception.getMessage());
    }

    @Test
    public void parseUpdate_multipleFields_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, (
                ) -> Parser.parseUpdate(
                        "update 1 /from 1400 /to 1800"));

        assertEquals(
                "Please update one field at a time.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_flexibleWhitespace_returnsEvent() {
        Event event = Parser.parseEvent(
                "  event   team  meeting\t/from\t1400   /to   1600  ");

        assertEquals("team  meeting", event.getDescription());
        assertEquals("1400", event.getStartTime());
        assertEquals("1600", event.getEndTime());
    }

    @Test
    public void parseEvent_repeatedStartField_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "event meeting /from 1400 /from 1500 /to 1600"));

        assertEquals(
                "Please specify /from only once.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_repeatedEndField_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "event meeting /from 1400 /to 1600 /to 1800"));

        assertEquals(
                "Please specify /to only once.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_missingDescription_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "event /from 1400 /to 1600"));

        assertEquals(
                "An event needs a description.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_missingStartTime_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "event meeting /from /to 1600"));

        assertEquals(
                "An event needs a start time.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_missingEndTime_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "event meeting /from 1400 /to"));

        assertEquals(
                "An event needs an end time.",
                exception.getMessage());
    }

    @Test
    public void parseEvent_reversedFields_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "event meeting /to 1600 /from 1400"));

        assertEquals(
                "An event must use: "
                        + "event <description> /from <time> /to <time>",
                exception.getMessage());
    }

    @Test
    public void parseEvent_missingEndField_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "event meeting /from 1400"));

        assertEquals(
                "An event must use: "
                        + "event <description> /from <time> /to <time>",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_flexibleWhitespace_returnsDeadline() {
        Deadline deadline = Parser.parseDeadline(
                "  deadline   return  book\t/by\t2026-09-30  ");

        assertEquals("return  book", deadline.getDescription());
        assertEquals(
                LocalDate.of(2026, 9, 30),
                deadline.getDueDate());
    }

    @Test
    public void parseDeadline_repeatedDateField_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseDeadline(
                        "deadline report /by 2026-09-30 /by 2026-10-01"));

        assertEquals(
                "Please specify /by only once.",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_joinedDateField_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseDeadline(
                        "deadline report /by2026-09-30"));

        assertEquals(
                "A deadline must use: deadline <description> /by <date>",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_separatorInsideWord_preservesDescription() {
        Deadline deadline = Parser.parseDeadline(
                "deadline review /bylaws /by 2026-09-30");

        assertEquals("review /bylaws", deadline.getDescription());
        assertEquals(
                LocalDate.of(2026, 9, 30),
                deadline.getDueDate());
    }

    @Test
    public void parseDeadline_nonexistentDate_exceptionThrown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Parser.parseDeadline(
                        "deadline report /by 2026-02-30"));

        assertEquals(
                "Please enter the date in YYYY-MM-DD format.",
                exception.getMessage());
    }

    @Test
    public void parseDeadline_validLeapDay_returnsDeadline() {
        Deadline deadline = Parser.parseDeadline(
                "deadline report /by 2028-02-29");

        assertEquals(
                LocalDate.of(2028, 2, 29),
                deadline.getDueDate());
    }
}
