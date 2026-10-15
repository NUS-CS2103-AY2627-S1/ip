package bo.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bo.BoException;
import bo.model.Deadline;
import bo.model.Event;
import bo.model.Task;
import bo.model.Todo;

/** Tests the command parsing and validation rules used by Bo. */
class ParserTest {
    /** The stateless parser shared by these tests. */
    private final Parser parser = new Parser();

    /** Verifies that a list command produces a list command with no task data. */
    @Test
    void parse_listCommand_returnsListCommand() throws BoException {
        Parser.Command command = parser.parse("list");

        assertAll(
                () -> assertEquals(Parser.Type.LIST, command.type()),
                () -> assertEquals(-1, command.taskIndex()),
                () -> assertNull(command.task()));
    }

    /** Verifies that task mutation commands convert one-based input to zero-based indexes. */
    @Test
    void parse_taskMutationCommands_returnsZeroBasedIndex() throws BoException {
        assertAll(
                () -> assertMutation("delete 1", Parser.Type.DELETE, 0),
                () -> assertMutation("mark 2", Parser.Type.MARK, 1),
                () -> assertMutation("unmark 3", Parser.Type.UNMARK, 2));
    }

    /** Verifies that surrounding and repeated whitespace does not change command parsing. */
    @Test
    void parse_commandsWithExtraWhitespace_normalizesInput() throws BoException {
        Parser.Command todoCommand = parser.parse("  todo   read   book  ");
        Parser.Command deadlineCommand = parser.parse(
                " deadline   submit   form   /by   2019-10-15 ");

        assertAll(
                () -> assertEquals("read book", todoCommand.task().getDescription()),
                () -> assertEquals("submit form", deadlineCommand.task().getDescription()),
                () -> assertEquals("Oct 15 2019", assertInstanceOf(
                        Deadline.class, deadlineCommand.task()).getDisplayBy()));
    }

    /** Verifies that each add command creates the correct task type and fields. */
    @Test
    void parse_addCommands_createsExpectedTasks() throws BoException {
        Parser.Command todoCommand = parser.parse("todo read book");
        Parser.Command deadlineCommand = parser.parse("deadline return book /by 2019-10-15");
        Parser.Command eventCommand = parser.parse(
                "event project meeting /from 2019-10-15 /to 2019-10-16 0905");

        Todo todo = assertInstanceOf(Todo.class, todoCommand.task());
        Deadline deadline = assertInstanceOf(Deadline.class, deadlineCommand.task());
        Event event = assertInstanceOf(Event.class, eventCommand.task());
        assertAll(
                () -> assertEquals(Parser.Type.ADD, todoCommand.type()),
                () -> assertEquals("read book", todo.getDescription()),
                () -> assertEquals("return book", deadline.getDescription()),
                () -> assertEquals("Oct 15 2019", deadline.getDisplayBy()),
                () -> assertEquals("project meeting", event.getDescription()),
                () -> assertEquals("Oct 15 2019", event.getDisplayFrom()),
                () -> assertEquals("Oct 16 2019 09:05", event.getDisplayTo()));
    }

    /** Verifies that a find command preserves the complete search keyword. */
    @Test
    void parse_findCommand_returnsSearchKeyword() throws BoException {
        Parser.Command command = parser.parse("find book shelf");

        assertAll(
                () -> assertEquals(Parser.Type.FIND, command.type()),
                () -> assertEquals(-1, command.taskIndex()),
                () -> assertNull(command.task()),
                () -> assertEquals("book shelf", command.keyword()));
    }

    /** Verifies that sort accepts no arguments and creates a sort command. */
    @Test
    void parse_sortCommand_returnsSortCommand() throws BoException {
        Parser.Command command = parser.parse("sort");

        assertAll(
                () -> assertEquals(Parser.Type.SORT, command.type()),
                () -> assertEquals(-1, command.taskIndex()),
                () -> assertNull(command.task()),
                () -> assertNull(command.keyword()));
    }

    /** Verifies that malformed commands return the documented user-facing errors. */
    @Test
    void parse_invalidCommands_throwsExpectedErrors() {
        assertAll(
                () -> assertParsingFails(null, "Please enter a command instead of an empty line."),
                () -> assertParsingFails("   ", "Please enter a command instead of an empty line."),
                () -> assertParsingFails("", "Please enter a command instead of an empty line."),
                () -> assertParsingFails("blah", "I'm sorry, but I don't know what that means :-("),
                () -> assertParsingFails("delete", "Please use delete followed by one task number, e.g. delete 1."),
                () -> assertParsingFails("mark abc", "The task number must be a whole number."),
                () -> assertParsingFails("delete 0", "The task number must be a positive whole number."),
                () -> assertParsingFails("find", "Please use find followed by a keyword, e.g. find book."),
                () -> assertParsingFails("list now", "Please use list without additional arguments."),
                () -> assertParsingFails("sort date", "Please use sort without additional arguments."),
                () -> assertParsingFails("todo", "The description of a todo cannot be empty."),
                () -> assertParsingFails("deadline return book",
                        "A deadline must include a /by date, e.g. deadline return book /by Friday."),
                () -> assertParsingFails("deadline /by Friday",
                        "A deadline must include a /by date, e.g. deadline return book /by Friday."),
                () -> assertParsingFails("deadline return book /by 2019-02-29",
                        "The /by date of a deadline is invalid. Please use a valid date or time."),
                () -> assertParsingFails("deadline return book /by Friday /by Monday",
                        "A deadline can include only one /by date."),
                () -> assertParsingFails("event project meeting /from Monday",
                        "An event must include a /to time."),
                () -> assertParsingFails("event project meeting /to Tuesday /from Monday",
                        "Please put the /from time before the /to time."),
                () -> assertParsingFails(
                        "event project meeting /from 2019-10-16 1000 /to 2019-10-16 1000",
                        "The /from time of an event must be before its /to time."),
                () -> assertParsingFails(
                        "event project meeting /from 2019-10-16 1000 /from 2019-10-17 /to 2019-10-18",
                        "An event can include only one /from time."));
    }

    /** Asserts the command type and zero-based index of a mutation command. */
    private void assertMutation(String input, Parser.Type expectedType, int expectedIndex)
            throws BoException {
        Parser.Command command = parser.parse(input);

        assertEquals(expectedType, command.type());
        assertEquals(expectedIndex, command.taskIndex());
        assertNull(command.task());
    }

    /** Asserts that parsing fails with a particular user-facing message. */
    private void assertParsingFails(String input, String expectedMessage) {
        BoException exception = assertThrows(BoException.class, () -> parser.parse(input));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
