package min.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import min.exception.MinException;
import min.note.Note;
import min.task.Deadline;
import min.task.Event;
import min.task.Todo;
import min.ui.Messages;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommand_validCommands_returnsCorrespondingCommands() throws MinException {
        assertEquals(Command.BYE, parser.parseCommand("bye"));
        assertEquals(Command.LIST, parser.parseCommand("list"));
        assertEquals(Command.LISTTASKS, parser.parseCommand("listtasks"));
        assertEquals(Command.LISTNOTES, parser.parseCommand("listnotes"));
        assertEquals(Command.FIND, parser.parseCommand("find book"));
        assertEquals(Command.MARK, parser.parseCommand("mark 1"));
        assertEquals(Command.UNMARK, parser.parseCommand("unmark 1"));
        assertEquals(Command.DELETE, parser.parseCommand("delete 1"));
        assertEquals(Command.TODO, parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE,
                parser.parseCommand("deadline submit report /by 2026-08-28"));
        assertEquals(Command.EVENT,
                parser.parseCommand("event meeting /from 2pm /to 4pm"));
        assertEquals(Command.NOTE, parser.parseCommand("note watch Dune"));
        assertEquals(Command.DELETENOTE, parser.parseCommand("deletenote 1"));
    }

    @Test
    void parseCommand_bareArgumentCommands_returnsCorrespondingCommands() throws MinException {
        assertEquals(Command.FIND, parser.parseCommand("find"));
        assertEquals(Command.MARK, parser.parseCommand("mark"));
        assertEquals(Command.UNMARK, parser.parseCommand("unmark"));
        assertEquals(Command.DELETE, parser.parseCommand("delete"));
        assertEquals(Command.TODO, parser.parseCommand("todo"));
        assertEquals(Command.DEADLINE, parser.parseCommand("deadline"));
        assertEquals(Command.EVENT, parser.parseCommand("event"));
        assertEquals(Command.NOTE, parser.parseCommand("note"));
        assertEquals(Command.DELETENOTE, parser.parseCommand("deletenote"));
    }

    @Test
    void parseCommand_invalidCommands_throwsMinException() {
        String expectedMessage = Messages.formatInvalidCommand(Command.getAllWords());

        assertThrowsMinException(expectedMessage, () -> parser.parseCommand(""));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("dance"));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("byebye"));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("list now"));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("listnotes now"));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("notes"));
        assertThrowsMinException(expectedMessage, () -> parser.parseCommand("help me"));
    }

    @Test
    void parseTaskIndex_validTaskNumbers_returnsZeroBasedIndexes() throws MinException {
        assertEquals(0, parser.parseTaskIndex("mark 1", Command.MARK, 3));
        assertEquals(2, parser.parseTaskIndex("delete 3", Command.DELETE, 3));
    }

    @Test
    void parseTaskIndex_missingTaskNumber_throwsMinException() {
        assertThrowsMinException(Messages.formatMissingIndex("task", "mark"),
                () -> parser.parseTaskIndex("mark", Command.MARK, 3));
    }

    @Test
    void parseTaskIndex_missingNumberWithEmptyTaskList_throwsMissingNumberException() {
        assertThrowsMinException(Messages.formatMissingIndex("task", "mark"),
                () -> parser.parseTaskIndex("mark", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_nonWholeNumber_throwsMinException() {
        assertThrowsMinException(Messages.formatIndexNotANumber("task"),
                () -> parser.parseTaskIndex("mark two", Command.MARK, 3));
        assertThrowsMinException(Messages.formatIndexNotANumber("task"),
                () -> parser.parseTaskIndex("mark 1.5", Command.MARK, 3));
    }

    @Test
    void parseTaskIndex_decimalNumberWithEmptyTaskList_throwsWholeNumberException() {
        assertThrowsMinException(Messages.formatIndexNotANumber("task"),
                () -> parser.parseTaskIndex("mark 1.5", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_emptyTaskList_throwsMinException() {
        assertThrowsMinException(Messages.formatNoItems("task", "mark"),
                () -> parser.parseTaskIndex("mark 1", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_negativeNumberWithEmptyTaskList_throwsNoTasksException() {
        assertThrowsMinException(Messages.formatNoItems("task", "mark"),
                () -> parser.parseTaskIndex("mark -1", Command.MARK, 0));
    }

    @Test
    void parseTaskIndex_negativeNumberWithNonEmptyTaskList_throwsRangeException() {
        assertThrowsMinException(Messages.formatIndexOutOfRange("task", 3),
                () -> parser.parseTaskIndex("mark -1", Command.MARK, 3));
    }

    @Test
    void parseTaskIndex_outOfRangeTaskNumber_throwsMinException() {
        assertThrowsMinException(Messages.formatIndexOutOfRange("task", 3),
                () -> parser.parseTaskIndex("mark 0", Command.MARK, 3));
        assertThrowsMinException(Messages.formatIndexOutOfRange("task", 3),
                () -> parser.parseTaskIndex("mark 4", Command.MARK, 3));
    }

    @Test
    void parseTodo_validInput_returnsTodo() throws MinException {
        Todo todo = parser.parseTodo("todo   read book");

        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    void parseTodo_missingDescription_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_TODO,
                () -> parser.parseTodo("todo"));
    }

    @Test
    void parseTodo_descriptionContainingFieldSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_TASK_TEXT,
                () -> parser.parseTodo("todo read | book"));
    }

    @Test
    void parseFindKeyword_validInput_returnsKeyword() throws MinException {
        assertEquals("read book", parser.parseFindKeyword("find   read book"));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_FIND,
                () -> parser.parseFindKeyword("find"));
    }

    @Test
    void parseDeadline_validInput_returnsDeadline() throws MinException {
        Deadline deadline = parser.parseDeadline("deadline submit report /by 2026-08-28");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("D | 0 | submit report | 2026-08-28", deadline.toFileString());
    }

    @Test
    void parseDeadline_missingDetails_throwsMinException() {
        String expectedMessage = Messages.INVALID_DEADLINE;

        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline /by 2026-08-28"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report /by"));
    }

    @Test
    void parseDeadline_invalidDate_throwsMinException() {
        String expectedMessage = Messages.INVALID_DEADLINE_DATE;

        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report /by 28-08-2026"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseDeadline("deadline submit report /by 2026-02-30"));
    }

    @Test
    void parseDeadline_descriptionContainingFieldSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_TASK_TEXT,
                () -> parser.parseDeadline(
                        "deadline submit | report /by 2026-08-28"));
    }

    @Test
    void parseEvent_validInput_returnsEvent() throws MinException {
        Event event = parser.parseEvent("event meeting /from 2pm /to 4pm");

        assertEquals("meeting", event.getDescription());
        assertEquals("E | 0 | meeting | 2pm | 4pm", event.toFileString());
    }

    @Test
    void parseEvent_missingDetails_throwsMinException() {
        String expectedMessage = Messages.INVALID_EVENT;

        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting /from 2pm"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event /from 2pm /to 4pm"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting /from  /to 4pm"));
        assertThrowsMinException(expectedMessage,
                () -> parser.parseEvent("event meeting /from 2pm /to"));
    }

    @Test
    void parseEvent_repeatedFromSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_EVENT,
                () -> parser.parseEvent("event meeting /from 2pm /from 3pm /to 4pm"));
    }

    @Test
    void parseEvent_repeatedToSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_EVENT,
                () -> parser.parseEvent("event meeting /from 2pm /to 4pm /to 5pm"));
    }

    @Test
    void parseEvent_descriptionContainingFieldSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_TASK_TEXT,
                () -> parser.parseEvent(
                        "event team | meeting /from 2pm /to 4pm"));
    }

    @Test
    void parseEvent_startTimeContainingFieldSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_TASK_TEXT,
                () -> parser.parseEvent(
                        "event meeting /from Friday | 2pm /to 4pm"));
    }

    @Test
    void parseEvent_endTimeContainingFieldSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_TASK_TEXT,
                () -> parser.parseEvent(
                        "event meeting /from 2pm /to Friday | 4pm"));
    }

    @Test
    void parseNote_validInput_returnsNote() throws MinException {
        Note note = parser.parseNote("note watch Dune");

        assertEquals("watch Dune", note.getText());
        assertEquals("N | watch Dune", note.toFileString());
    }

    @Test
    void parseNote_surroundingWhitespace_isTrimmed() throws MinException {
        assertEquals("watch Dune", parser.parseNote("note   watch Dune  ").getText());
    }

    @Test
    void parseNote_missingText_throwsMinException() {
        String expectedMessage = Messages.INVALID_NOTE;

        assertThrowsMinException(expectedMessage, () -> parser.parseNote("note"));
        assertThrowsMinException(expectedMessage, () -> parser.parseNote("note    "));
    }

    @Test
    void parseNote_textContainingFieldSeparator_throwsMinException() {
        assertThrowsMinException(Messages.INVALID_NOTE_TEXT,
                () -> parser.parseNote("note watch Dune | part two"));
    }

    @Test
    void parseNoteIndex_validNoteNumbers_returnsZeroBasedIndexes() throws MinException {
        assertEquals(0, parser.parseNoteIndex("deletenote 1", 3));
        assertEquals(2, parser.parseNoteIndex("deletenote 3", 3));
    }

    @Test
    void parseNoteIndex_missingNoteNumber_throwsMinException() {
        assertThrowsMinException(Messages.formatMissingIndex("note", "delete"),
                () -> parser.parseNoteIndex("deletenote", 3));
    }

    @Test
    void parseNoteIndex_nonNumericNoteNumber_throwsMinException() {
        assertThrowsMinException(Messages.formatIndexNotANumber("note"),
                () -> parser.parseNoteIndex("deletenote first", 3));
    }

    @Test
    void parseNoteIndex_noteNumberOutsideList_throwsMinException() {
        String expectedMessage = Messages.formatIndexOutOfRange("note", 3);

        assertThrowsMinException(expectedMessage, () -> parser.parseNoteIndex("deletenote 0", 3));
        assertThrowsMinException(expectedMessage, () -> parser.parseNoteIndex("deletenote 4", 3));
    }

    @Test
    void parseNoteIndex_emptyNoteList_throwsMinException() {
        assertThrowsMinException(Messages.formatNoItems("note", "delete"),
                () -> parser.parseNoteIndex("deletenote 1", 0));
    }

    private void assertThrowsMinException(String expectedMessage, Executable executable) {
        MinException exception = assertThrows(MinException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
