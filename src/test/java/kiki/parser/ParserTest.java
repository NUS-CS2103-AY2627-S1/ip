package kiki.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

import kiki.exception.KikiException;
import kiki.task.Deadlines;
import kiki.task.Events;
import kiki.task.Priority;

public class ParserTest {

    @Test
    public void parseTodoDescription_validDescription_returnsTrimmedDescription() throws KikiException {
        String description = Parser.parseTodoDescription("todo   read book  ");
        assertEquals("read book", description);
    }

    @Test
    public void parseTodoDescription_emptyDescription_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseTodoDescription("todo"));
        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsTrimmedKeyword() throws KikiException {
        String keyword = Parser.parseFindKeyword("find   book  ");
        assertEquals("book", keyword);
    }

    @Test
    public void parseFindKeyword_emptyKeyword_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseFindKeyword("find"));
        assertEquals("Please tell me what keyword to search for.", exception.getMessage());
    }

    @Test
    public void parseAiQuestion_validQuestion_returnsTrimmedQuestion() throws KikiException {
        assertEquals("how do I add a deadline?", Parser.parseAiQuestion("@ai   how do I add a deadline?  "));
    }

    @Test
    public void parseAiQuestion_emptyQuestion_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class, () -> Parser.parseAiQuestion("@ai"));
        assertEquals("Please tell me what you would like to ask Kiki's AI.", exception.getMessage());
    }

    @Test
    public void parseDeadline_validInput_returnsDeadlineWithParsedDateTime() throws KikiException {
        Deadlines deadline = Parser.parseDeadline("deadline return book /by 2019-12-01 1800");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, Month.DECEMBER, 1, 18, 0), deadline.getBy());
    }

    @Test
    public void parseDeadline_missingByKeyword_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseDeadline("deadline return book"));
        assertEquals("Please use: deadline DESCRIPTION /by TIME", exception.getMessage());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseDeadline("deadline /by 2019-12-01 1800"));
        assertEquals("The description of a deadline cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseDeadline_emptyByTime_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseDeadline("deadline return book /by"));
        assertEquals("The by time of a deadline cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseDeadline_wrongDateFormat_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseDeadline("deadline return book /by Sunday"));
        assertEquals("Please use: deadline DESCRIPTION /by yyyy-MM-dd HHmm", exception.getMessage());
    }

    @Test
    public void parseDeadline_nonexistentDate_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseDeadline("deadline return book /by 2035-02-30 1800"));
        assertEquals("Please use: deadline DESCRIPTION /by yyyy-MM-dd HHmm", exception.getMessage());
    }

    @Test
    public void parseDeadline_duplicateByMarker_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseDeadline(
                        "deadline return book /by 2035-02-28 1800 /by 2035-03-01 1800"));
        assertEquals("Please use only one /by marker in a deadline command.", exception.getMessage());
    }

    @Test
    public void parseEvent_validInput_returnsEventWithParsedDateTimes() throws KikiException {
        Events event = Parser.parseEvent(
                "event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, Month.DECEMBER, 2, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, Month.DECEMBER, 2, 16, 0), event.getTo());
    }

    @Test
    public void parseEvent_missingFromKeyword_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseEvent("event meeting /to 2019-12-02 1600"));
        assertEquals("Please use: event DESCRIPTION /from START /to END", exception.getMessage());
    }

    @Test
    public void parseEvent_missingToKeyword_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseEvent("event meeting /from 2019-12-02 1400"));
        assertEquals("Please use: event DESCRIPTION /from START /to END", exception.getMessage());
    }

    @Test
    public void parseEvent_wrongDateFormat_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseEvent("event meeting /from Sunday /to 2019-12-02 1600"));
        assertEquals("Please use: event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm",
                exception.getMessage());
    }

    @Test
    public void parseEvent_startNotBeforeEnd_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseEvent(
                        "event meeting /from 2035-03-01 1000 /to 2035-03-01 1000"));
        assertEquals("The event start time must be earlier than its end time.", exception.getMessage());
    }

    @Test
    public void parseEvent_duplicateToMarker_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseEvent(
                        "event meeting /from 2035-03-01 1000 /to 2035-03-01 1200 /to 2035-03-01 1400"));
        assertEquals("Please use only one /to marker in an event command.", exception.getMessage());
    }

    @Test
    public void parseEvent_nonexistentDate_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseEvent("event meeting /from 2035-02-30 1000 /to 2035-03-01 1200"));
        assertEquals("Please use: event DESCRIPTION /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm",
                exception.getMessage());
    }

    @Test
    public void parseCheckDate_explicitYear_returnsParsedDate() throws KikiException {
        LocalDate date = Parser.parseCheckDate("21 August 2035");
        assertEquals(LocalDate.of(2035, Month.AUGUST, 21), date);
    }

    @Test
    public void parseCheckDate_noYear_defaultsToCurrentYear() throws KikiException {
        LocalDate date = Parser.parseCheckDate("21 August");
        assertEquals(LocalDate.of(LocalDate.now().getYear(), Month.AUGUST, 21), date);
    }

    @Test
    public void parseCheckDate_emptyText_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseCheckDate(""));
        assertEquals("Please provide a date, e.g. check day 21 August", exception.getMessage());
    }

    @Test
    public void parseCheckDate_unparsableText_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseCheckDate("banana"));
        assertEquals("Please use a date like: 21 August", exception.getMessage());
    }

    @Test
    public void parseCheckDate_nonexistentDate_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseCheckDate("29 February 2035"));
        assertEquals("Please use a date like: 21 August", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validMiddleIndex_returnsZeroBasedIndex() throws KikiException {
        int result = Parser.parseTaskIndex("mark 2", "mark", 3);
        assertEquals(1, result);
    }

    @Test
    public void parsePriority_validName_returnsPriority() throws KikiException {
        assertEquals(Priority.HIGH, Parser.parsePriority("priority high"));
    }

    @Test
    public void parsePriority_invalidName_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parsePriority("priority urgent"));
        assertEquals("Priority must be low, normal, or high.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validFirstIndex_returnsZeroBasedIndex() throws KikiException {
        int result = Parser.parseTaskIndex("mark 1", "mark", 3);
        assertEquals(0, result);
    }

    @Test
    public void parseTaskIndex_validLastIndex_returnsZeroBasedIndex() throws KikiException {
        int result = Parser.parseTaskIndex("mark 3", "mark", 3);
        assertEquals(2, result);
    }

    @Test
    public void parseTaskIndex_surroundingWhitespace_returnsZeroBasedIndex() throws KikiException {
        int result = Parser.parseTaskIndex("mark   2  ", "mark", 3);
        assertEquals(1, result);
    }

    @Test
    public void parseTaskIndex_missingNumber_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseTaskIndex("mark", "mark", 3));
        assertEquals("Please tell me which task number to mark.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericInput_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseTaskIndex("mark abc", "mark", 3));
        assertEquals("Task number must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_indexTooLow_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseTaskIndex("mark 0", "mark", 3));
        assertEquals("That task number is not in your list.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_negativeIndex_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseTaskIndex("mark -1", "mark", 3));
        assertEquals("That task number is not in your list.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_indexTooHigh_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseTaskIndex("mark 4", "mark", 3));
        assertEquals("That task number is not in your list.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_emptyList_exceptionThrown() {
        KikiException exception = assertThrows(KikiException.class,
                () -> Parser.parseTaskIndex("mark 1", "mark", 0));
        assertEquals("That task number is not in your list.", exception.getMessage());
    }
}
