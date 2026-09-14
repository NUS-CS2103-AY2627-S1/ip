package johnny.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import johnny.JohnnyException;
import johnny.task.Deadline;
import johnny.task.Event;
import johnny.task.Todo;

public class ParserTest {

    // --- parseCommand ---

    @Test
    public void parseCommand_todo_correctEnum() {
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
    }

    @Test
    public void parseCommand_bye_correctEnum() {
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_list_correctEnum() {
        assertEquals(Command.LIST, Parser.parseCommand("list"));
    }

    @Test
    public void parseCommand_mark_correctEnum() {
        assertEquals(Command.MARK, Parser.parseCommand("mark 1"));
    }

    @Test
    public void parseCommand_unmark_correctEnum() {
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark 1"));
    }

    @Test
    public void parseCommand_delete_correctEnum() {
        assertEquals(Command.DELETE, Parser.parseCommand("delete 1"));
    }

    @Test
    public void parseCommand_deadline_correctEnum() {
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline homework /by 2024-01-01"));
    }

    @Test
    public void parseCommand_event_correctEnum() {
        assertEquals(Command.EVENT, Parser.parseCommand("event meeting /from 2024-01-01 /to 2024-01-02"));
    }

    @Test
    public void parseCommand_undo_correctEnum() {
        assertEquals(Command.UNDO, Parser.parseCommand("undo"));
    }

    @Test
    public void parseCommand_caseInsensitive_correctEnum() {
        assertEquals(Command.TODO, Parser.parseCommand("ToDo read book"));
    }

    @Test
    public void parseCommand_allUpperCase_correctEnum() {
        assertEquals(Command.LIST, Parser.parseCommand("LIST"));
    }

    @Test
    public void parseCommand_unknownCommand_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("blah"));
    }

    @Test
    public void parseCommand_emptyInput_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand(""));
    }

    @Test
    public void parseCommand_surroundingAndRepeatedSpaces_correctEnum() {
        assertEquals(Command.TODO, Parser.parseCommand("   todo    read book   "));
    }

    // --- parseArguments ---

    @Test
    public void parseArguments_hasArguments_returnsArguments() {
        assertEquals("read book", Parser.parseArguments("todo read book"));
    }

    @Test
    public void parseArguments_noArguments_returnsEmpty() {
        assertEquals("", Parser.parseArguments("list"));
    }

    @Test
    public void parseArguments_preservesInternalSpaces_returnsFullArgument() {
        assertEquals("read   many   books", Parser.parseArguments("todo read   many   books"));
    }

    @Test
    public void parseArguments_surroundingAndRepeatedCommandSpaces_returnsTrimmedArguments() {
        assertEquals("read book", Parser.parseArguments("   todo    read book   "));
    }

    // --- parseTaskIndex ---

    @Test
    public void parseTaskIndex_validIndex_returnsZeroBased() throws JohnnyException {
        assertEquals(1, Parser.parseTaskIndex("2", 3));
    }

    @Test
    public void parseTaskIndex_firstIndex_returnsZero() throws JohnnyException {
        assertEquals(0, Parser.parseTaskIndex("1", 3));
    }

    @Test
    public void parseTaskIndex_lastIndex_returnsLastZeroBased() throws JohnnyException {
        assertEquals(2, Parser.parseTaskIndex("3", 3));
    }

    @Test
    public void parseTaskIndex_emptyArguments_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseTaskIndex("", 3));
        assertTrue(e.getMessage().contains("provide a task number"));
    }

    @Test
    public void parseTaskIndex_whitespaceOnly_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("   ", 3));
    }

    @Test
    public void parseTaskIndex_nonNumeric_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseTaskIndex("abc", 3));
        assertTrue(e.getMessage().contains("abc"));
        assertTrue(e.getMessage().contains("not a valid task number"));
    }

    @Test
    public void parseTaskIndex_outOfRangeHigh_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseTaskIndex("5", 3));
        assertTrue(e.getMessage().contains("out of range"));
        assertTrue(e.getMessage().contains("3 tasks"));
    }

    @Test
    public void parseTaskIndex_outOfRangeLow_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("0", 3));
    }

    @Test
    public void parseTaskIndex_negativeNumber_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("-1", 3));
    }

    @Test
    public void parseTaskIndex_oneAboveMax_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTaskIndex("4", 3));
    }

    // --- parseTodo ---

    @Test
    public void parseTodo_validDescription_returnsTodo() throws JohnnyException {
        Todo todo = Parser.parseTodo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTodo_validDescription_correctFileString() throws JohnnyException {
        Todo todo = Parser.parseTodo("read book");
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void parseTodo_leadingTrailingSpaces_trimmed() throws JohnnyException {
        Todo todo = Parser.parseTodo("  read book  ");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTodo_emptyDescription_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseTodo_whitespaceOnly_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseTodo("   "));
    }

    @Test
    public void parseTodo_storageDelimiterInDescription_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseTodo("wash | fold laundry"));
        assertTrue(e.getMessage().contains("cannot contain"));
    }

    // --- parseDeadline ---

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws JohnnyException {
        Deadline deadline = Parser.parseDeadline("homework /by 2024-09-15");
        assertTrue(deadline.toString().contains("[D]"));
        assertTrue(deadline.toString().contains("homework"));
        assertTrue(deadline.toString().contains("Sept 15 2024")
                || deadline.toString().contains("Sep 15 2024"));
    }

    @Test
    public void parseDeadline_validInput_correctFileString() throws JohnnyException {
        Deadline deadline = Parser.parseDeadline("homework /by 2024-09-15");
        assertEquals("D | 0 | homework | 2024-09-15", deadline.toFileString());
    }

    @Test
    public void parseDeadline_missingByDelimiter_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseDeadline("homework"));
        assertTrue(e.getMessage().contains("Invalid deadline format"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseDeadline(" /by 2024-09-15"));
        assertTrue(e.getMessage().contains("description"));
    }

    @Test
    public void parseDeadline_emptyDate_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseDeadline("homework /by "));
        assertTrue(e.getMessage().contains("date"));
    }

    @Test
    public void parseDeadline_invalidDateFormat_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseDeadline("homework /by Sunday"));
        assertTrue(e.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    public void parseDeadline_partialDate_throwsException() {
        assertThrows(JohnnyException.class, () ->
                Parser.parseDeadline("homework /by 2024-13-01"));
    }

    @Test
    public void parseDeadline_repeatedSpaces_returnsDeadline() throws JohnnyException {
        Deadline deadline = Parser.parseDeadline("homework    /by    2024-09-15");
        assertEquals("D | 0 | homework | 2024-09-15", deadline.toFileString());
    }

    @Test
    public void parseDeadline_repeatedByParameter_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseDeadline("homework /by 2024-09-15 /by 2024-09-16"));
        assertTrue(e.getMessage().contains("only once"));
    }

    @Test
    public void parseDeadline_storageDelimiterInDescription_throwsException() {
        assertThrows(JohnnyException.class, () ->
                Parser.parseDeadline("read | review /by 2024-09-15"));
    }

    // --- parseEvent ---

    @Test
    public void parseEvent_validInput_returnsEvent() throws JohnnyException {
        Event event = Parser.parseEvent("meeting /from 2024-03-01 /to 2024-03-03");
        assertTrue(event.toString().contains("[E]"));
        assertTrue(event.toString().contains("meeting"));
        assertTrue(event.toString().contains("Mar 01 2024"));
        assertTrue(event.toString().contains("Mar 03 2024"));
    }

    @Test
    public void parseEvent_validInput_correctFileString() throws JohnnyException {
        Event event = Parser.parseEvent("meeting /from 2024-03-01 /to 2024-03-03");
        assertEquals("E | 0 | meeting | 2024-03-01 | 2024-03-03", event.toFileString());
    }

    @Test
    public void parseEvent_missingFromDelimiter_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /to 2024-03-03"));
        assertTrue(e.getMessage().contains("Invalid event format"));
    }

    @Test
    public void parseEvent_missingToDelimiter_throwsException() {
        assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /from 2024-03-01"));
    }

    @Test
    public void parseEvent_missingBothDelimiters_throwsException() {
        assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting"));
    }

    @Test
    public void parseEvent_emptyDescription_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent(" /from 2024-03-01 /to 2024-03-03"));
        assertTrue(e.getMessage().contains("description"));
    }

    @Test
    public void parseEvent_emptyFromDate_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /from  /to 2024-03-03"));
        assertTrue(e.getMessage().contains("start date"));
    }

    @Test
    public void parseEvent_emptyToDate_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /from 2024-03-01 /to "));
        assertTrue(e.getMessage().contains("end date"));
    }

    @Test
    public void parseEvent_invalidStartDate_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /from Monday /to 2024-03-03"));
        assertTrue(e.getMessage().contains("start date"));
    }

    @Test
    public void parseEvent_invalidEndDate_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /from 2024-03-01 /to Friday"));
        assertTrue(e.getMessage().contains("end date"));
    }

    @Test
    public void parseEvent_fromAfterTo_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /to 2024-03-01 /from 2024-03-03"));
        assertTrue(e.getMessage().contains("/from must come before /to"));
    }

    @Test
    public void parseEvent_repeatedSpaces_returnsEvent() throws JohnnyException {
        Event event = Parser.parseEvent(
                "meeting    /from    2024-03-01    /to    2024-03-03");
        assertEquals("E | 0 | meeting | 2024-03-01 | 2024-03-03", event.toFileString());
    }

    @Test
    public void parseEvent_repeatedFromParameter_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () -> Parser.parseEvent(
                "meeting /from 2024-03-01 /from 2024-03-02 /to 2024-03-03"));
        assertTrue(e.getMessage().contains("only once"));
    }

    @Test
    public void parseEvent_repeatedToParameter_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () -> Parser.parseEvent(
                "meeting /from 2024-03-01 /to 2024-03-02 /to 2024-03-03"));
        assertTrue(e.getMessage().contains("only once"));
    }

    @Test
    public void parseEvent_sameStartAndEnd_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /from 2024-03-01 /to 2024-03-01"));
        assertTrue(e.getMessage().contains("start date must be before"));
    }

    @Test
    public void parseEvent_startAfterEnd_throwsException() {
        JohnnyException e = assertThrows(JohnnyException.class, () ->
                Parser.parseEvent("meeting /from 2024-03-03 /to 2024-03-01"));
        assertTrue(e.getMessage().contains("start date must be before"));
    }

    @Test
    public void parseEvent_storageDelimiterInDescription_throwsException() {
        assertThrows(JohnnyException.class, () -> Parser.parseEvent(
                "plan | review /from 2024-03-01 /to 2024-03-03"));
    }
}
