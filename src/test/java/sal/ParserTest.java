package sal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Parser} command parsing, including valid inputs and common format errors.
 */
public class ParserTest {
    @Test
    public void getCommandWord_singleWord_returnsWord() throws SalException {
        assertEquals("bye", Parser.getCommandWord("bye"));
        assertEquals("list", Parser.getCommandWord("list"));
    }

    @Test
    public void getCommandWord_commandWithArguments_returnsFirstWord() throws SalException {
        assertEquals("todo", Parser.getCommandWord("todo read book"));
        assertEquals("mark", Parser.getCommandWord("mark 2"));
        assertEquals("deadline", Parser.getCommandWord("deadline return book /by 2019-10-15"));
        assertEquals("find", Parser.getCommandWord("find book"));
    }

    @Test
    public void getCommandWord_emptyInput_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.getCommandWord(""));
        assertEquals("Command not recognised.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws SalException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", "mark", "Correct format: mark <task number>"));
        assertEquals(1, Parser.parseTaskIndex("delete 2", "delete", "Correct format: delete <task number>"));
        assertEquals(9, Parser.parseTaskIndex("unmark 10", "unmark", "Correct format: unmark <task number>"));
    }

    @Test
    public void parseTaskIndex_missingNumber_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseTaskIndex("delete", "delete", "Correct format: delete <task number>"));
        assertEquals("Correct format: delete <task number>", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonInteger_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseTaskIndex("mark two", "mark", "Correct format: mark <task number>"));
        assertEquals("Correct format: mark <task number>", exception.getMessage());
    }

    @Test
    public void parseTodo_validDescription_returnsTodo() throws SalException {
        Todo todo = Parser.parseTodo("todo read book");
        assertEquals("read book", todo.description);
        assertFalse(todo.isDone);
    }

    @Test
    public void parseTodo_descriptionWithExtraSpaces_trimsDescription() throws SalException {
        Todo todo = Parser.parseTodo("todo    buy milk   ");
        assertEquals("buy milk", todo.description);
    }

    @Test
    public void parseTodo_missingDescription_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.parseTodo("todo"));
        assertEquals("Correct format: todo <task name>", exception.getMessage());
    }

    @Test
    public void parseTodo_whitespaceOnlyDescription_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.parseTodo("todo    "));
        assertEquals("Correct format: todo <task name>", exception.getMessage());
    }

    @Test
    public void parseDeadline_validDate_returnsDeadline() throws SalException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2019-10-15");
        assertEquals("return book", deadline.description);
        assertFalse(deadline.by.hasTime());
        assertEquals(LocalDate.of(2019, 10, 15), deadline.by.getValue().toLocalDate());
    }

    @Test
    public void parseDeadline_validDateTime_returnsDeadlineWithTime() throws SalException {
        Deadline deadline = Parser.parseDeadline("deadline return book /by 2/12/2019 1800");
        assertEquals("return book", deadline.description);
        assertTrue(deadline.by.hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.by.getValue());
    }

    @Test
    public void parseDeadline_missingBy_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseDeadline("deadline return book"));
        assertEquals("Correct format: deadline <task name> /by <date/time>", exception.getMessage());
    }

    @Test
    public void parseDeadline_emptyDescription_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseDeadline("deadline /by 2019-10-15"));
        assertEquals("Correct format: deadline <task name> /by <date/time>", exception.getMessage());
    }

    @Test
    public void parseDeadline_emptyDate_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseDeadline("deadline return book /by "));
        assertEquals("Correct format: deadline <task name> /by <date/time>", exception.getMessage());
    }

    @Test
    public void parseDeadline_invalidDate_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseDeadline("deadline return book /by next Friday"));
        assertTrue(exception.getMessage().contains("Invalid date/time format"));
    }

    @Test
    public void parseEvent_validDates_returnsEvent() throws SalException {
        Event event = Parser.parseEvent("event project meeting /from 2019-10-15 /to 2019-10-16");
        assertEquals("project meeting", event.description);
        assertFalse(event.from.hasTime());
        assertFalse(event.to.hasTime());
        assertEquals(LocalDate.of(2019, 10, 15), event.from.getValue().toLocalDate());
        assertEquals(LocalDate.of(2019, 10, 16), event.to.getValue().toLocalDate());
    }

    @Test
    public void parseEvent_validDateTimes_returnsEventWithTimes() throws SalException {
        Event event = Parser.parseEvent(
                "event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        assertEquals("project meeting", event.description);
        assertTrue(event.from.hasTime());
        assertTrue(event.to.hasTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.from.getValue());
        assertEquals(LocalDateTime.of(2019, 12, 2, 16, 0), event.to.getValue());
    }

    @Test
    public void parseEvent_missingFrom_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseEvent("event project meeting /to 2019-10-16"));
        assertEquals("Correct format: event <task name> /from <start> /to <end>", exception.getMessage());
    }

    @Test
    public void parseEvent_missingTo_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseEvent("event project meeting /from 2019-10-15"));
        assertEquals("Correct format: event <task name> /from <start> /to <end>", exception.getMessage());
    }

    @Test
    public void parseEvent_emptyDescription_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseEvent("event /from 2019-10-15 /to 2019-10-16"));
        assertEquals("Correct format: event <task name> /from <start> /to <end>", exception.getMessage());
    }

    @Test
    public void parseFind_validKeyword_returnsKeyword() throws SalException {
        assertEquals("book", Parser.parseFind("find book"));
    }

    @Test
    public void parseFind_multiWordKeyword_returnsFullKeyword() throws SalException {
        assertEquals("return book", Parser.parseFind("find return book"));
    }

    @Test
    public void parseFind_extraSpaces_trimsKeyword() throws SalException {
        assertEquals("book", Parser.parseFind("find    book   "));
    }

    @Test
    public void parseFind_missingKeyword_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.parseFind("find"));
        assertEquals("Correct format: find <keyword>", exception.getMessage());
    }

    @Test
    public void parseFind_whitespaceOnlyKeyword_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.parseFind("find    "));
        assertEquals("Correct format: find <keyword>", exception.getMessage());
    }

    @Test
    public void parseTag_validNumberAndTag_returnsIndexAndName() throws SalException {
        Parser.ParsedTag parsed = Parser.parseTag("tag 2 fun");
        assertEquals(1, parsed.index);
        assertEquals("fun", parsed.tagName);
    }

    @Test
    public void parseTag_hashPrefix_stripsHash() throws SalException {
        Parser.ParsedTag parsed = Parser.parseTag("tag 1 #urgent");
        assertEquals(0, parsed.index);
        assertEquals("urgent", parsed.tagName);
    }

    @Test
    public void parseTag_hyphenAndUnderscore_allowed() throws SalException {
        Parser.ParsedTag parsed = Parser.parseTag("tag 3 cs-2103_t");
        assertEquals(2, parsed.index);
        assertEquals("cs-2103_t", parsed.tagName);
    }

    @Test
    public void parseTag_missingTag_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.parseTag("tag 1"));
        assertEquals("Correct format: tag <task number> <tag>", exception.getMessage());
    }

    @Test
    public void parseTag_nonIntegerIndex_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.parseTag("tag two fun"));
        assertEquals("Correct format: tag <task number> <tag>", exception.getMessage());
    }

    @Test
    public void parseTag_invalidTagName_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () -> Parser.parseTag("tag 1 hello world"));
        assertEquals("Correct format: tag <task number> <tag>", exception.getMessage());
    }

    @Test
    public void parseEvent_invalidDate_exceptionThrown() {
        SalException exception = assertThrows(SalException.class, () ->
                Parser.parseEvent("event meeting /from not-a-date /to 2019-10-16"));
        assertTrue(exception.getMessage().contains("Invalid date/time format"));
    }
}
