package tblade.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import tblade.exception.TBladeException;

/**
 * Tests Parser's command-argument extraction and validation for every supported command,
 * including the error message produced for each invalid input.
 */
public class ParserTest {
    // getTodoDescription

    @Test
    public void getTodoDescription_validCommand_returnsTrimmedDescription() throws TBladeException {
        assertEquals("read book", Parser.getTodoDescription("todo   read book  "));
    }

    @Test
    public void getTodoDescription_noDescription_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () -> Parser.getTodoDescription("todo"));

        assertEquals("The description of a todo cannot be empty. Use: todo DESCRIPTION", exception.getMessage());
    }

    @Test
    public void getTodoDescription_onlyWhitespaceDescription_throwsTBladeException() {
        assertThrows(TBladeException.class, () -> Parser.getTodoDescription("todo    "));
    }

    // getFindKeyword

    @Test
    public void getFindKeyword_validCommand_returnsTrimmedKeyword() throws TBladeException {
        assertEquals("book", Parser.getFindKeyword("find   book  "));
    }

    @Test
    public void getFindKeyword_noKeyword_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () -> Parser.getFindKeyword("find"));

        assertEquals("The search keyword cannot be empty. Use: find KEYWORD", exception.getMessage());
    }

    // parseDeadlineArgs

    @Test
    public void parseDeadlineArgs_validCommand_returnsDescriptionAndDate() throws TBladeException {
        Parser.DeadlineArgs args = Parser.parseDeadlineArgs("deadline return book /by 2026-03-15");

        assertEquals("return book", args.description());
        assertEquals(LocalDate.of(2026, 3, 15), args.by());
    }

    @Test
    public void parseDeadlineArgs_missingDescription_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseDeadlineArgs("deadline /by 2026-03-15"));

        assertEquals("The description of a deadline cannot be empty. Use: deadline DESCRIPTION /by DATE",
                exception.getMessage());
    }

    @Test
    public void parseDeadlineArgs_missingByKeyword_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseDeadlineArgs("deadline return book"));

        assertEquals("A deadline needs a /by date. Use: deadline DESCRIPTION /by DATE", exception.getMessage());
    }

    @Test
    public void parseDeadlineArgs_emptyDate_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseDeadlineArgs("deadline return book /by   "));

        assertEquals("The /by date cannot be empty. Use: deadline DESCRIPTION /by DATE", exception.getMessage());
    }

    @Test
    public void parseDeadlineArgs_dateNotIsoFormat_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseDeadlineArgs("deadline return book /by Sunday"));

        assertEquals("The /by date must use yyyy-MM-dd. Use: deadline DESCRIPTION /by yyyy-MM-dd",
                exception.getMessage());
    }

    // parseEventArgs

    @Test
    public void parseEventArgs_validCommand_returnsDescriptionFromAndTo() throws TBladeException {
        Parser.EventArgs args = Parser.parseEventArgs("event project meeting /from Mon 2pm /to 4pm");

        assertEquals("project meeting", args.description());
        assertEquals("Mon 2pm", args.from());
        assertEquals("4pm", args.to());
    }

    @Test
    public void parseEventArgs_missingDescription_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseEventArgs("event /from Mon /to Tue"));

        assertEquals("The description of an event cannot be empty. Use: event DESCRIPTION /from START /to END",
                exception.getMessage());
    }

    @Test
    public void parseEventArgs_missingFromKeyword_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseEventArgs("event workshop /to Tue"));

        assertEquals("An event needs a /from start time. Use: event DESCRIPTION /from START /to END",
                exception.getMessage());
    }

    @Test
    public void parseEventArgs_missingToKeyword_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseEventArgs("event workshop /from Mon"));

        assertEquals("An event needs a /to end time. Use: event DESCRIPTION /from START /to END",
                exception.getMessage());
    }

    @Test
    public void parseEventArgs_emptyToTime_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseEventArgs("event workshop /from Mon /to   "));

        assertEquals("The /to end time cannot be empty. Use: event DESCRIPTION /from START /to END",
                exception.getMessage());
    }

    // parseTaskIndex

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws TBladeException {
        assertEquals(1, Parser.parseTaskIndex("mark 2", "mark", 3));
    }

    @Test
    public void parseTaskIndex_noNumberProvided_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseTaskIndex("mark", "mark", 3));

        assertEquals("Please provide a task number. Use: mark TASK_NUMBER", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_emptyList_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseTaskIndex("mark 1", "mark", 0));

        assertEquals("There are no tasks to mark. Add a task first.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_numberOutOfRange_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseTaskIndex("mark 5", "mark", 3));

        assertEquals("Task number must be between 1 and 3. Use: mark TASK_NUMBER", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_notANumber_throwsTBladeException() {
        TBladeException exception = assertThrows(TBladeException.class, () ->
                Parser.parseTaskIndex("mark abc", "mark", 3));

        assertEquals("Task number must be a whole number. Use: mark TASK_NUMBER", exception.getMessage());
    }
}
