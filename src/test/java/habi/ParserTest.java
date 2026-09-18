package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests command parsing and validation. */
public class ParserTest {
    @Test
    public void parseEvent_validValuesWithWhitespace_returnsTrimmedEvent()
            throws HabiException {
        Event event = Parser.parseEvent(
                "event project meeting /from  Mon 2pm  /to  4pm ");

        assertEquals("project meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    public void parseDeadline_validIsoDate_returnsDeadline() throws HabiException {
        Deadline deadline = Parser.parseDeadline(
                "deadline return book /by 2026-09-15");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), deadline.getBy());
    }

    @Test
    public void parseDeadline_flexibleSeparatorSpacing_returnsDeadline() throws HabiException {
        Deadline deadline = Parser.parseDeadline("deadline return book/by2026-09-15");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), deadline.getBy());
    }

    @Test
    public void parseDeadline_duplicateBySeparator_throwsUsageError() {
        HabiException exception = assertThrows(HabiException.class, () ->
                Parser.parseDeadline("deadline return book /by 2026-09-15 /by 2026-09-16"));

        assertEquals("OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd", exception.getMessage());
    }

    @Test
    public void parseDeadline_invalidDate_throwsHabiException() {
        HabiException exception = assertThrows(HabiException.class, () ->
                Parser.parseDeadline("deadline return book /by 15-09-2026"));

        assertEquals("OOPS! Use: deadline DESCRIPTION /by yyyy-MM-dd",
                exception.getMessage());
    }

    @Test
    public void parseEvent_flexibleSeparatorSpacing_returnsEvent() throws HabiException {
        Event event = Parser.parseEvent("event project meeting/fromMon 2pm/to4pm");

        assertEquals("project meeting", event.getDescription());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    public void parseEvent_duplicateSeparator_throwsUsageError() {
        HabiException exception = assertThrows(HabiException.class, () ->
                Parser.parseEvent("event project /from Mon /from Tue /to Wed"));

        assertEquals("OOPS! Use: event DESCRIPTION /from START /to END", exception.getMessage());
    }

    @Test
    public void parseEvent_duplicateToSeparator_throwsUsageError() {
        HabiException exception = assertThrows(HabiException.class, () ->
                Parser.parseEvent("event project /from Mon /to Tue /to Wed"));

        assertEquals("OOPS! Use: event DESCRIPTION /from START /to END", exception.getMessage());
    }

    @Test
    public void parseTodo_tabInDescription_throwsHabiException() {
        HabiException exception = assertThrows(HabiException.class, () ->
                Parser.parseTodo("todo read\tbook"));

        assertEquals("OOPS! HABI cannot save text containing tab characters.", exception.getMessage());
    }

    @Test
    public void parseNote_tabInContent_throwsHabiException() {
        HabiException exception = assertThrows(HabiException.class, () ->
                Parser.parseNote("note call\tMum"));

        assertEquals("OOPS! HABI cannot save text containing tab characters.", exception.getMessage());
    }

    @Test
    public void parseDeadline_tabInPersistedField_throwsHabiException() {
        HabiException descriptionException = assertThrows(HabiException.class, () ->
                Parser.parseDeadline("deadline return\tbook /by 2026-09-15"));
        HabiException dateException = assertThrows(HabiException.class, () ->
                Parser.parseDeadline("deadline return book /by 2026-09-\t15"));

        assertEquals("OOPS! HABI cannot save text containing tab characters.",
                descriptionException.getMessage());
        assertEquals("OOPS! HABI cannot save text containing tab characters.",
                dateException.getMessage());
    }

    @Test
    public void parseEvent_tabInPersistedField_throwsHabiException() {
        HabiException descriptionException = assertThrows(HabiException.class, () ->
                Parser.parseEvent("event project\tmeeting /from Mon /to Tue"));
        HabiException fromException = assertThrows(HabiException.class, () ->
                Parser.parseEvent("event project meeting /from Mon\t2pm /to Tue"));
        HabiException toException = assertThrows(HabiException.class, () ->
                Parser.parseEvent("event project meeting /from Mon /to Tue\t4pm"));

        assertEquals("OOPS! HABI cannot save text containing tab characters.",
                descriptionException.getMessage());
        assertEquals("OOPS! HABI cannot save text containing tab characters.",
                fromException.getMessage());
        assertEquals("OOPS! HABI cannot save text containing tab characters.",
                toException.getMessage());
    }

    @Test
    public void parseTaskIndex_validAndInvalidNumbers_handlesOneBasedIndex()
            throws HabiException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", "mark", 3));
        assertThrows(HabiException.class, () ->
                Parser.parseTaskIndex("mark 4", "mark", 3));
    }

    @Test
    public void parseFindKeyword_presentAndMissingKeyword_handlesBothCases()
            throws HabiException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
        assertThrows(HabiException.class, () ->
                Parser.parseFindKeyword("find"));
    }
}
