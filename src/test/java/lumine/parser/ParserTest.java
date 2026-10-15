package lumine.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lumine.LumineException;
import lumine.command.AddCommand;
import lumine.command.DateCommand;
import lumine.command.DeleteCommand;
import lumine.command.ExitCommand;
import lumine.command.FindCommand;
import lumine.command.ListCommand;
import lumine.command.MarkCommand;
import lumine.command.UndoCommand;
import lumine.command.UnmarkCommand;
import lumine.task.Deadline;
import lumine.task.Event;
import lumine.task.Todo;

/**
 * Tests {@link Parser}. Each test targets one behavior of one method so that
 * a failure pinpoints exactly what broke.
 */
class ParserTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    // -------------------------------------------------------------------------
    // normalize parser
    // -------------------------------------------------------------------------

    @Test
    void normalize_leadingAndTrailingSpaces_trimmed() {
        assertEquals("test", parser.normalize("  test  "));
    }

    @Test
    void normalize_noExtraSpaces_unchanged() {
        assertEquals("test", parser.normalize("test"));
    }

    @Test
    void normalize_blankString_returnsEmpty() {
        assertEquals("", parser.normalize("  "));
    }

    @Test
    void normalize_nullCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.normalize(null));
    }

    // -------------------------------------------------------------------------
    // isCommand parser
    // -------------------------------------------------------------------------

    @Test
    void isCommand_exactMatch_returnsTrue() {
        assertTrue(parser.isCommand("list", "list"));
    }

    @Test
    void isCommand_commandWithArgument_returnsTrue() {
        assertTrue(parser.isCommand("todo test", "todo"));
    }

    @Test
    void isCommand_differentCommand_returnsFalse() {
        assertFalse(parser.isCommand("deadline test /by Monday", "todo"));
    }

    @Test
    void isCommand_prefixWithoutSpace_returnsFalse() {
        assertFalse(parser.isCommand("todoX test", "todo"));
    }

    @Test
    void isCommand_nullCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.isCommand(null, "todo"));
    }

    @Test
    void isCommand_blankCommandName_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.isCommand("todo test", " "));
    }

    // -------------------------------------------------------------------------
    // parseTaskNumber parser
    // -------------------------------------------------------------------------

    @Test
    void parseTaskNumber_validNumber_returnsParsedInt() {
        assertEquals(3, parser.parseTaskNumber("mark 3", "mark"));
    }

    @Test
    void parseTaskNumber_nonNumericArgument_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseTaskNumber("mark test", "mark"));
    }

    @Test
    void parseTaskNumber_emptyArgument_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseTaskNumber("mark", "mark"));
    }

    @Test
    void parseTaskNumber_mismatchedCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parseTaskNumber("delete 1", "mark"));
    }

    @Test
    void parseTaskNumber_unsupportedCommandName_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parseTaskNumber("list 1", "list"));
    }

    // -------------------------------------------------------------------------
    // parseTodoCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parseTodoCommand_validDescription_returnsCorrectToString() {
        Todo todo = parser.parseTodoCommand("todo test");
        assertEquals("[T][ ] test", todo.toString());
    }

    @Test
    void parseTodoCommand_extraInternalSpaces_descriptionPreserved() {
        Todo todo = parser.parseTodoCommand("todo test1  test2");
        assertEquals("[T][ ] test1  test2", todo.toString());
    }

    @Test
    void parseTodoCommand_emptyDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseTodoCommand("todo"));
    }

    @Test
    void parseTodoCommand_whitespaceOnlyDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseTodoCommand("todo   "));
    }

    @Test
    void parseTodoCommand_wrongCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parseTodoCommand("find test"));
    }

    // -------------------------------------------------------------------------
    // parseDeadlineCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parseDeadlineCommand_validPlainTextBy_returnsCorrectToString() {
        Deadline d = parser.parseDeadlineCommand("deadline test /by Monday");
        assertEquals("[D][ ] test (by: Monday)", d.toString());
    }

    @Test
    void parseDeadlineCommand_structuredDateByValue_formatsDisplayDate() {
        Deadline d = parser.parseDeadlineCommand("deadline test /by 2026 01 01");
        assertEquals("[D][ ] test (by: Jan 01 2026)", d.toString());
    }

    @Test
    void parseDeadlineCommand_missingByClause_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseDeadlineCommand("deadline test"));
    }

    @Test
    void parseDeadlineCommand_emptyDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseDeadlineCommand("deadline /by Monday"));
    }

    @Test
    void parseDeadlineCommand_emptyByValue_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseDeadlineCommand("deadline test /by"));
    }

    @Test
    void parseDeadlineCommand_wrongCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parseDeadlineCommand("todo test"));
    }

    // -------------------------------------------------------------------------
    // parseEventCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parseEventCommand_validPlainTextTimes_returnsCorrectToString() {
        Event e = parser.parseEventCommand("event test /from Mon 2pm /to 4pm");
        assertEquals("[E][ ] test (from: Mon 2pm to: 4pm)", e.toString());
    }

    @Test
    void parseEventCommand_missingFromClause_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseEventCommand("event test /to 4pm"));
    }

    @Test
    void parseEventCommand_missingToClause_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseEventCommand("event test /from Mon 2pm"));
    }

    @Test
    void parseEventCommand_emptyDescription_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseEventCommand("event /from Mon 2pm /to 4pm"));
    }

    @Test
    void parseEventCommand_emptyFromValue_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseEventCommand("event test /from /to 4pm"));
    }

    @Test
    void parseEventCommand_emptyToValue_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseEventCommand("event test /from Mon 2pm /to"));
    }

    @Test
    void parseEventCommand_wrongCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parseEventCommand("todo test"));
    }

    // -------------------------------------------------------------------------
    // parseDateCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parseDateCommand_validDate_returnsLocalDate() {
        LocalDate date = parser.parseDateCommand("date 2026 01 01");
        assertEquals(LocalDate.of(2026, 1, 1), date);
    }

    @Test
    void parseDateCommand_wrongFormat_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseDateCommand("date 01/01/2026"));
    }

    @Test
    void parseDateCommand_nonExistentDate_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseDateCommand("date 2026 13 32"));
    }

    @Test
    void parseDateCommand_missingArgument_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseDateCommand("date"));
    }

    @Test
    void parseDateCommand_wrongCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parseDateCommand("todo test"));
    }

    // -------------------------------------------------------------------------
    // parseFindCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parseFindCommand_validKeyword_returnsKeyword() {
        assertEquals("book", parser.parseFindCommand("find book"));
    }

    @Test
    void parseFindCommand_emptyKeyword_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parseFindCommand("find"));
        assertThrows(LumineException.class, () -> parser.parseFindCommand("find   "));
    }

    @Test
    void parseFindCommand_wrongCommand_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> parser.parseFindCommand("todo test"));
    }

    /* --------------------------------Commands-------------------------------- */

    // -------------------------------------------------------------------------
    // exitCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_exitCommand_returnsExitCommand() {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ExitCommand.class, parser.parse("  bye  "));
    }

    // -------------------------------------------------------------------------
    // listCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_listCommand_returnsListCommand() {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(ListCommand.class, parser.parse(" list "));
    }

    // -------------------------------------------------------------------------
    // undoCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_undoCommand_returnsUndoCommand() {
        assertInstanceOf(UndoCommand.class, parser.parse("undo"));
        assertInstanceOf(UndoCommand.class, parser.parse("  undo  "));
    }

    // -------------------------------------------------------------------------
    // addCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_addTodoCommand_returnsAddCommand() {
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
    }

    // -------------------------------------------------------------------------
    // deadlineCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_addDeadlineCommand_returnsAddCommand() {
        assertInstanceOf(AddCommand.class, parser.parse("deadline submit /by Monday"));
    }

    // -------------------------------------------------------------------------
    // eventCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_addEventCommand_returnsAddCommand() {
        assertInstanceOf(AddCommand.class, parser.parse("event party /from Mon 2pm /to 4pm"));
    }

    // -------------------------------------------------------------------------
    // markCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_markCommand_returnsMarkCommand() {
        assertInstanceOf(MarkCommand.class, parser.parse("mark 2"));
    }

    // -------------------------------------------------------------------------
    // unmarkCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_unmarkCommand_returnsUnmarkCommand() {
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 3"));
    }

    // -------------------------------------------------------------------------
    // deleteCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_deleteCommand_returnsDeleteCommand() {
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
    }

    // -------------------------------------------------------------------------
    // dateCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_dateCommand_returnsDateCommand() {
        assertInstanceOf(DateCommand.class, parser.parse("date 2026 01 01"));
    }

    // -------------------------------------------------------------------------
    // findCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_findCommand_returnsFindCommand() {
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    // -------------------------------------------------------------------------
    // unknownCommand parser
    // -------------------------------------------------------------------------

    @Test
    void parse_unknownCommand_throwsLumineException() {
        assertThrows(LumineException.class, () -> parser.parse("hello"));
        assertThrows(LumineException.class, () -> parser.parse("todoX read book"));
    }
}
