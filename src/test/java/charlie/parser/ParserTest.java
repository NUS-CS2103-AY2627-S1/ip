package charlie.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import charlie.command.AddCommand;
import charlie.command.CommandType;
import charlie.command.DeleteCommand;
import charlie.command.ExitCommand;
import charlie.command.FindCommand;
import charlie.command.ListCommand;
import charlie.command.MarkCommand;
import charlie.command.OnCommand;
import charlie.command.UnmarkCommand;
import charlie.command.UpdateCommand;
import charlie.exception.CharlieException;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.Todo;

public class ParserTest {
    @Test
    public void parse_allCommandTypes_returnsMatchingCommandClasses() {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2026-09-20"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo borrow book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline submit /by 2026-09-20"));
        assertInstanceOf(
                AddCommand.class,
                Parser.parse("event meeting /from 2026-09-20 0900 /to 2026-09-20 1000"));
        assertInstanceOf(UpdateCommand.class, Parser.parse("update 1 description changed"));
    }

    @Test
    public void parseCommand_leadingWhitespace_returnsCommand() {
        assertEquals(CommandType.LIST, Parser.parseCommand("   list"));
    }

    @Test
    public void parseCommand_emptyInput_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseCommand(""));
        assertEquals("Um... please give me a command first.", exception.getMessage());
    }

    @Test
    public void parseCommand_nullInput_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseCommand(null));
        assertEquals("Um... please give me a command first.", exception.getMessage());
    }

    @Test
    public void parse_listWithUnexpectedArgument_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("list extra"));
        assertEquals("The list command does not accept arguments.", exception.getMessage());
    }

    @Test
    public void parseDate_invalidDateFormat_exceptionThrown() {
        String invalidInput = "on 09-15-2026";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseDate(invalidInput));
        assertEquals("Date must be a valid date in yyyy-MM-dd format.", exception.getMessage());
    }

    @Test
    public void parseDate_validDate_returnsDate() {
        assertEquals("2026-09-20", Parser.parseDate("on 2026-09-20").toString());
    }

    @Test
    public void parseDate_wrongArgumentCount_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseDate("on 2026-09-20 extra"));

        assertEquals(
                "Please provide exactly one date in yyyy-MM-dd format.",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() {
        assertEquals(2, Parser.parseTaskIndex("mark 3"));
    }

    @Test
    public void parseTaskIndex_missingOrNonNumericNumber_exceptionThrown() {
        CharlieException missingNumber = assertThrows(
                CharlieException.class, () -> Parser.parseTaskIndex("mark"));
        CharlieException nonNumericNumber = assertThrows(
                CharlieException.class, () -> Parser.parseTaskIndex("mark first"));

        assertEquals("Please provide exactly one task number.", missingNumber.getMessage());
        assertEquals("Please enter a valid task number.", nonNumericNumber.getMessage());
    }

    @Test
    public void parseFindKeyword_multipleWords_returnsEntireSearchText() {
        assertEquals("return book", Parser.parseFindKeyword("find return book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseFindKeyword("find"));
        assertEquals("Please provide a keyword to find.", exception.getMessage());
    }

    @Test
    public void parseTask_eventEndsBeforeStart_exceptionThrown() {
        String invalidInput = "event meeting /from 2026-09-01 1600 /to 2026-09-01 1400";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.EVENT));
        assertEquals("Event end must be after its start.", exception.getMessage());
    }

    @Test
    public void parseTask_repeatedDescriptionWhitespace_returnsNormalizedDescription() {
        Task task = Parser.parseTask("todo   borrow    book  ", CommandType.TODO);
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    public void parseTask_duplicateDeadlineDelimiter_exceptionThrown() {
        String invalidInput = "deadline return book /by 2026-09-20 /by 2026-09-21";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.DEADLINE));
        assertEquals("A deadline must include /by exactly once.", exception.getMessage());
    }

    @Test
    public void parseTask_duplicateEventDelimiter_exceptionThrown() {
        String invalidInput = "event meeting /from 2026-09-20 0900 /from 2026-09-20 1000 "
                + "/to 2026-09-20 1100";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.EVENT));
        assertEquals(
                "An event must include /from and /to exactly once.",
                exception.getMessage());
    }

    @Test
    public void parseTask_descriptionContainingSaveDelimiter_exceptionThrown() {
        String invalidInput = "todo review A | B";
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(invalidInput, CommandType.TODO));
        assertEquals("A task description cannot contain |.", exception.getMessage());
    }

    @Test
    public void parseTask_validTaskTypes_returnsExpectedTasks() {
        Todo todo = assertInstanceOf(
                Todo.class, Parser.parseTask("todo borrow book", CommandType.TODO));
        Deadline deadline = assertInstanceOf(
                Deadline.class,
                Parser.parseTask("deadline submit report /by 2026-09-20", CommandType.DEADLINE));
        Event event = assertInstanceOf(
                Event.class,
                Parser.parseTask(
                        "event meeting /from 2026-09-20 0900 /to 2026-09-20 1000",
                        CommandType.EVENT));

        assertEquals("[T][ ] borrow book", todo.toString());
        assertEquals("[D][ ] submit report (by: Sep 20 2026)", deadline.toString());
        assertEquals(
                "[E][ ] meeting (from: Sep 20 2026, 9:00 AM to: Sep 20 2026, 10:00 AM)",
                event.toString());
    }

    @Test
    public void parseTask_missingDescription_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask("todo", CommandType.TODO));

        assertEquals("The task description cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseTask_invalidDeadlineParts_exceptionThrown() {
        assertTaskParseError(
                "A deadline must include /by followed by a date.",
                "deadline submit", CommandType.DEADLINE);
        assertTaskParseError(
                "Description cannot be empty.",
                "deadline /by 2026-09-20", CommandType.DEADLINE);
        assertTaskParseError(
                "Deadline cannot be empty.",
                "deadline submit /by", CommandType.DEADLINE);
        assertTaskParseError(
                "Deadline must be a valid date in yyyy-MM-dd format.",
                "deadline submit /by Sunday", CommandType.DEADLINE);
    }

    @Test
    public void parseTask_invalidEventParts_exceptionThrown() {
        assertTaskParseError(
                "Need to include /from or /to fields.",
                "event meeting", CommandType.EVENT);
        assertTaskParseError(
                "Invalid argument format: /from should appear before /to",
                "event meeting /to 2026-09-20 1000 /from 2026-09-20 0900",
                CommandType.EVENT);
        assertTaskParseError(
                "Description cannot be empty.",
                "event /from 2026-09-20 0900 /to 2026-09-20 1000",
                CommandType.EVENT);
        assertTaskParseError(
                "from/to fields cannot be empty.",
                "event meeting /from /to 2026-09-20 1000", CommandType.EVENT);
        assertTaskParseError(
                "Event dates must use the yyyy-MM-dd HHmm format.",
                "event meeting /from tomorrow /to later", CommandType.EVENT);
    }

    @Test
    public void parseUpdate_missingArguments_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("update 1 description"));
        assertEquals("Usage: update TASK_NUMBER FIELD NEW_VALUE.", exception.getMessage());
    }

    @Test
    public void parseUpdate_nonNumericTaskNumber_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("update first description changed"));
        assertEquals("Please enter a valid task number.", exception.getMessage());
    }

    @Test
    public void parseUpdate_unsupportedField_exceptionThrown() {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parse("update 1 priority high"));
        assertEquals(
                "Supported update fields are description, deadline, from, and to.",
                exception.getMessage());
    }

    private void assertTaskParseError(
            String expectedMessage, String input, CommandType commandType) {
        CharlieException exception = assertThrows(
                CharlieException.class, () -> Parser.parseTask(input, commandType));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
