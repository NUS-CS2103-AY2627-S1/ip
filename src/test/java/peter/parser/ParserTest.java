package peter.parser;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import peter.exception.PeterException;
import peter.task.Deadline;
import peter.task.Event;
import peter.task.Task;
import peter.task.Todo;


public class ParserTest {

    @Test
    public void parseTodo_validInput_success() throws PeterException {
        Task task = Parser.parseTodo("todo read book");
        assertTrue(task instanceof Todo);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () -> {
            Parser.parseTodo("todo   ");
        });
        assertEquals("That waypoint needs a description. Try: todo read book", exception.getMessage());
    }

    @Test
    public void parseDeadline_validInput_success() throws PeterException {
        Task task = Parser.parseDeadline("deadline return book /by 2019-12-02 1800");
        assertTrue(task instanceof Deadline);
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", task.toString());
    }

    @Test
    public void parseDeadline_missingByFormat_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseDeadline("deadline return book Sunday");
        });
    }

    @Test
    public void parseDeadline_flexibleWhitespace_success() throws PeterException {
        Task task = Parser.parseDeadline("  deadline   return   book   /by   2019-12-02   1800  ");

        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", task.toString());
    }

    @Test
    public void parseDeadline_duplicateBy_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseDeadline("deadline return book /by 2019-12-02 /by 1800");
        });
    }

    @Test
    public void parseDeadline_nonExistentDate_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseDeadline("deadline return book /by 2026-02-30 1800");
        });
    }

    @Test
    public void parseEvent_validTimeRange_success() throws PeterException {
        Task task = Parser.parseEvent("event meeting /from 0930 /to 1600");

        assertTrue(task instanceof Event);
        assertEquals("[E][ ] meeting (from: 0930 to: 1600)", task.toString());
    }

    @Test
    public void parseEvent_endNotAfterStart_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseEvent("event meeting /from 1600 /to 1600");
        });
        assertThrows(PeterException.class, () -> {
            Parser.parseEvent("event meeting /from 1700 /to 1600");
        });
    }

    @Test
    public void parseEvent_invalidTime_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseEvent("event meeting /from 2560 /to 2700");
        });
    }

    @Test
    public void parseIndex_validInteger_returnsZeroBasedIndex() throws PeterException {
        int index = Parser.parseIndex("mark 2");
        assertEquals(1, index);
    }

    @Test
    public void parseIndex_invalidInteger_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseIndex("mark abc");
        });
    }

    @Test
    public void parseIndex_extraParameter_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseIndex("mark 1 extra");
        });
    }

    @Test
    public void parseViewDate_nonExistentDate_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseViewDate("view 2026-02-30");
        });
    }

    @Test
    public void parseTodo_reservedDelimiter_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.parseTodo("todo read | write notes");
        });
    }

    @Test
    public void requireNoArguments_trailingParameter_exceptionThrown() {
        assertThrows(PeterException.class, () -> {
            Parser.requireNoArguments("list now", "list");
        });
    }

    @Test
    public void parseTodo_internalWhitespace_normalized() throws PeterException {
        Task task = Parser.parseTodo("  TODO   read\t\tbook   carefully  ");

        assertEquals("read book carefully", task.getDescription());
    }

    @Test
    public void parseTodo_nullOrWrongCommand_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parseTodo(null));
        assertThrows(PeterException.class, () -> Parser.parseTodo("deadline read book"));
    }

    @Test
    public void parseTodo_unicodeDescription_preserved() throws PeterException {
        Task task = Parser.parseTodo("todo 完成报告 🧭");

        assertEquals("完成报告 🧭", task.getDescription());
    }

    @Test
    public void parseTodo_embeddedControlCharacter_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parseTodo("todo read\0book"));
    }

    @Test
    public void parseDeadline_missingDescriptionOrDate_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parseDeadline("deadline /by 2026-09-10"));
        assertThrows(PeterException.class, () -> Parser.parseDeadline("deadline read book /by"));
        assertThrows(PeterException.class, () -> Parser.parseDeadline("deadline read book/by 2026-09-10"));
    }

    @Test
    public void parseDeadline_dateOnly_success() throws PeterException {
        Deadline deadline = (Deadline) Parser.parseDeadline("DEADLINE submit /BY 2026-09-10");

        assertEquals(LocalDate.of(2026, 9, 10), deadline.getBy().toLocalDate());
    }

    @Test
    public void parseEvent_missingOrDuplicateMarkers_exceptionThrown() {
        String duplicateFrom = "event meeting /from 0900 /from 1000 /to 1100";
        String duplicateTo = "event meeting /from 0900 /to 1000 /to 1100";
        assertThrows(PeterException.class, () -> Parser.parseEvent("event meeting /to 1600"));
        assertThrows(PeterException.class, () -> Parser.parseEvent("event meeting /from 0900"));
        assertThrows(PeterException.class, () -> Parser.parseEvent(duplicateFrom));
        assertThrows(PeterException.class, () -> Parser.parseEvent(duplicateTo));
    }

    @Test
    public void parseEvent_missingDescriptionOrTime_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parseEvent("event /from 0900 /to 1000"));
        assertThrows(PeterException.class, () -> Parser.parseEvent("event meeting /from /to 1000"));
        assertThrows(PeterException.class, () -> Parser.parseEvent("event meeting /from 0900 /to"));
    }

    @Test
    public void parseIndex_missingNonPositiveAndOverflow_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parseIndex(null));
        assertThrows(PeterException.class, () -> Parser.parseIndex("mark"));
        assertThrows(PeterException.class, () -> Parser.parseIndex("mark 0"));
        assertThrows(PeterException.class, () -> Parser.parseIndex("mark -1"));
        assertThrows(PeterException.class, () -> Parser.parseIndex("mark 999999999999999999999999"));
    }

    @Test
    public void parseIndex_leadingAndRepeatedWhitespace_success() throws PeterException {
        assertEquals(41, Parser.parseIndex("  mark     42  "));
    }

    @Test
    public void parseViewDate_validMissingAndExtraValues_handledCorrectly() throws PeterException {
        assertEquals(LocalDate.of(2026, 9, 10), Parser.parseViewDate("  VIEW   2026-09-10  "));
        assertThrows(PeterException.class, () -> Parser.parseViewDate("view"));
        assertThrows(PeterException.class, () -> Parser.parseViewDate("view 2026-09-10 extra"));
    }

    @Test
    public void parseFindKeyword_validAndInvalidValues_handledCorrectly() throws PeterException {
        assertEquals("read book", Parser.parseFindKeyword(" find   read   book "));
        assertThrows(PeterException.class, () -> Parser.parseFindKeyword("find"));
        assertThrows(PeterException.class, () -> Parser.parseFindKeyword("find read | book"));
    }

    @Test
    public void requireNoArguments_nullBlankAndValidCommands_handledCorrectly() throws PeterException {
        Parser.requireNoArguments("  LIST  ", "list");
        Parser.requireNoArguments("", "");
        assertThrows(PeterException.class, () -> Parser.requireNoArguments(null, "list"));
        assertThrows(PeterException.class, () -> Parser.requireNoArguments("sort", "list"));
    }
}
