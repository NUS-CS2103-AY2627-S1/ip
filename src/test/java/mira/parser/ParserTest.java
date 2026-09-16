package mira.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import mira.command.Command;
import mira.command.CommandType;
import mira.exception.MiraException;
import mira.task.Deadline;
import mira.task.Event;
import mira.task.Todo;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_validTaskCommands_createsTypedTasks() throws MiraException {
        Command todoCommand = parser.parse("todo read a book");
        assertEquals(CommandType.TODO, todoCommand.getType());
        Todo todo = assertInstanceOf(Todo.class, todoCommand.getTask());
        assertEquals("read a book", todo.getDescription());

        Command deadlineCommand = parser.parse("deadline return book /by 2026-08-28");
        assertEquals(CommandType.DEADLINE, deadlineCommand.getType());
        Deadline deadline = assertInstanceOf(Deadline.class, deadlineCommand.getTask());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 28), deadline.getBy());

        Command eventCommand = parser.parse("event launch /from 9am /to 10am");
        assertEquals(CommandType.EVENT, eventCommand.getType());
        Event event = assertInstanceOf(Event.class, eventCommand.getTask());
        assertEquals("launch", event.getDescription());
        assertEquals("9am", event.getFrom());
        assertEquals("10am", event.getTo());
    }

    @Test
    void parse_todoAlias_createsTodoTask() throws MiraException {
        Command command = parser.parse("t read a book");

        assertEquals(CommandType.TODO, command.getType());
        Todo todo = assertInstanceOf(Todo.class, command.getTask());
        assertEquals("read a book", todo.getDescription());
    }

    @Test
    void parse_taskNumberCommands_acceptsOnlyOnePositiveInteger() throws MiraException {
        assertEquals(12, parser.parse("mark 12").getTaskNumber());
        assertEquals(3, parser.parse("unmark 3").getTaskNumber());
        assertEquals(1, parser.parse("delete 1").getTaskNumber());

        assertThrows(MiraException.class, () -> parser.parse("mark"));
        assertThrows(MiraException.class, () -> parser.parse("mark 0"));
        assertThrows(MiraException.class, () -> parser.parse("mark one"));
        assertThrows(MiraException.class, () -> parser.parse("mark 1 extra"));
        assertThrows(MiraException.class, () -> parser.parse("mark 999999999999999999999999"));
    }

    @Test
    void parse_findCommand_preservesKeywordPhrase() throws MiraException {
        Command command = parser.parse("find Book chapter");

        assertEquals(CommandType.FIND, command.getType());
        assertEquals("Book chapter", command.getKeyword());
        assertThrows(MiraException.class, () -> parser.parse("find"));
    }

    @Test
    void parse_malformedTaskCommands_throwsMiraException() {
        assertThrows(MiraException.class, () -> parser.parse(""));
        assertThrows(MiraException.class, () -> parser.parse("todo"));
        assertThrows(MiraException.class, () -> parser.parse("deadline impossible /by 2023-02-29"));
        assertThrows(MiraException.class, () -> parser.parse("deadline x /by 2026-08-28 /by 2026-08-29"));
        assertThrows(MiraException.class, () -> parser.parse("event x /from a /to b /to c"));
        assertThrows(MiraException.class, () -> parser.parse("unknown command"));
        assertThrows(MiraException.class, () -> parser.parse("list extra"));
    }

    @Test
    void parse_whitespaceAndCase_preservesDescriptionAndAcceptsCommand()
            throws MiraException {
        Command command = parser.parse("  ToDo\tRead  Chapter TWO  ");
        assertEquals(CommandType.TODO, command.getType());
        assertEquals("Read  Chapter TWO", command.getTask().getDescription());
        assertEquals(CommandType.LIST, parser.parse("\tLIST  ").getType());
        assertThrows(MiraException.class, () -> parser.parse(null));
    }

    @Test
    void parse_leapDay_acceptsRealLeapDay() throws MiraException {
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parse("deadline renew /by 2028-02-29").getTask());
        assertEquals(LocalDate.of(2028, 2, 29), deadline.getBy());
    }

    @TestFactory
    Stream<DynamicTest> parse_invalidBoundaries_rejectsWithoutCrashing() {
        return Stream.of(
                " ", "todo\nlist", "todo a\rb", "bye extra", "find   ",
                "delete -1", "unmark 1.5", "mark 2147483648", "delete +1",
                "deadline a /by 2026-04-31", "deadline a /by 2026-13-01",
                "deadline a /by 2026-00-10", "deadline a /by 2026-9-01",
                "deadline a /by tomorrow", "deadline /by 2026-09-18",
                "event /from a /to b", "event a /from /to b", "event a /from b /to",
                "event a /to b /from c", "event a /from b /from c /to d")
                .map(input -> DynamicTest.dynamicTest(
                        "Reject: " + input.replace('\n', ' '), () -> {
                            assertThrows(MiraException.class, () -> parser.parse(input));
                        }));
    }
}
