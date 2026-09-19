package mochi.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mochi.MochiException;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Todo;

/**
 * Tests conversion of user input into executable commands.
 */
public class ParserTest {
    @Test
    public void parse_simpleCommands_returnsExpectedCommandTypes() throws MochiException {
        assertAll(
                () -> assertEquals(Parser.CommandType.LIST, Parser.parse("list").getType()),
                () -> assertEquals(Parser.CommandType.BYE, Parser.parse("bye").getType())
        );
    }

    @Test
    public void parse_taskNumberCommands_returnsTypeAndTaskNumber() throws MochiException {
        Parser.Command markCommand = Parser.parse("mark 2");
        Parser.Command unmarkCommand = Parser.parse("unmark 3");
        Parser.Command deleteCommand = Parser.parse("delete 4");

        assertAll(
                () -> assertEquals(Parser.CommandType.MARK, markCommand.getType()),
                () -> assertEquals(2, markCommand.getTaskNumber()),
                () -> assertEquals(Parser.CommandType.UNMARK, unmarkCommand.getType()),
                () -> assertEquals(3, unmarkCommand.getTaskNumber()),
                () -> assertEquals(Parser.CommandType.DELETE, deleteCommand.getType()),
                () -> assertEquals(4, deleteCommand.getTaskNumber())
        );
    }

    @Test
    public void parse_findCommand_returnsTypeAndKeyword() throws MochiException {
        Parser.Command command = Parser.parse("find return book");

        assertAll(
                () -> assertEquals(Parser.CommandType.FIND, command.getType()),
                () -> assertEquals("return book", command.getKeyword())
        );
    }

    @Test
    public void parse_addCommands_returnsCorrectTaskTypes() throws MochiException {
        Parser.Command todoCommand = Parser.parse("todo read book");
        Parser.Command deadlineCommand =
                Parser.parse("deadline return book /by 2026-06-06 1800");
        Parser.Command eventCommand = Parser.parse(
                "event project meeting /from 2026-08-06 1400 /to 2026-08-06 1600");

        assertAll(
                () -> assertEquals(Parser.CommandType.ADD, todoCommand.getType()),
                () -> assertInstanceOf(Todo.class, todoCommand.getTask()),
                () -> assertEquals("[T][ ] read book", todoCommand.getTask().toString()),
                () -> assertInstanceOf(Deadline.class, deadlineCommand.getTask()),
                () -> assertEquals("[D][ ] return book (by: Jun 06 2026, 6:00 PM)",
                        deadlineCommand.getTask().toString()),
                () -> assertInstanceOf(Event.class, eventCommand.getTask()),
                () -> assertEquals(
                        "[E][ ] project meeting (from: Aug 06 2026, 2:00 PM "
                                + "to: Aug 06 2026, 4:00 PM)",
                        eventCommand.getTask().toString())
        );
    }

    @Test
    public void parse_commandAliases_returnsEquivalentCommands() throws MochiException {
        Parser.Command todoCommand = Parser.parse("t read book");
        Parser.Command deadlineCommand =
                Parser.parse("d return book /by 2026-06-06 1800");
        Parser.Command eventCommand = Parser.parse(
                "e project meeting /from 2026-08-06 1400 /to 2026-08-06 1600");
        Parser.Command listCommand = Parser.parse("l");
        Parser.Command findCommand = Parser.parse("f book");

        assertAll(
                () -> assertEquals(Parser.CommandType.ADD, todoCommand.getType()),
                () -> assertInstanceOf(Todo.class, todoCommand.getTask()),
                () -> assertEquals("[T][ ] read book", todoCommand.getTask().toString()),
                () -> assertEquals(Parser.CommandType.ADD, deadlineCommand.getType()),
                () -> assertInstanceOf(Deadline.class, deadlineCommand.getTask()),
                () -> assertEquals("[D][ ] return book (by: Jun 06 2026, 6:00 PM)",
                        deadlineCommand.getTask().toString()),
                () -> assertEquals(Parser.CommandType.ADD, eventCommand.getType()),
                () -> assertInstanceOf(Event.class, eventCommand.getTask()),
                () -> assertEquals(
                        "[E][ ] project meeting (from: Aug 06 2026, 2:00 PM "
                                + "to: Aug 06 2026, 4:00 PM)",
                        eventCommand.getTask().toString()),
                () -> assertEquals(Parser.CommandType.LIST, listCommand.getType()),
                () -> assertEquals(Parser.CommandType.FIND, findCommand.getType()),
                () -> assertEquals("book", findCommand.getKeyword())
        );
    }

    @Test
    public void parse_missingArguments_exceptionThrown() {
        assertAll(
                () -> assertThrows(MochiException.class, () -> Parser.parse("todo")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("deadline")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("event")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("mark")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("unmark")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("delete")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("find"))
        );
    }

    @Test
    public void parse_commandWithSurroundingWhitespace_ignoresExtraWhitespace()
            throws MochiException {
        Parser.Command command = Parser.parse("  todo   read book  ");

        assertEquals("[T][ ] read book", command.getTask().toString());
    }

    @Test
    public void parse_simpleCommandWithArguments_exceptionThrown() {
        assertAll(
                () -> assertThrows(MochiException.class, () -> Parser.parse("bye now")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("list now"))
        );
    }

    @Test
    public void parse_invalidTaskNumber_exceptionThrown() {
        assertAll(
                () -> assertThrows(MochiException.class, () -> Parser.parse("mark two")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("unmark 1.5")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("delete 1 2"))
        );
    }

    @Test
    public void parse_malformedDatedTasks_exceptionThrown() {
        assertAll(
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse("deadline return book 2026-06-06 1800")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse("deadline return book /by 2026-02-30 1800")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse("deadline /by 2026-06-06 1800")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse("deadline return book /by ")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse("event meeting /from 2026-08-06 1400")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse(
                                "event /from 2026-08-06 1400 /to 2026-08-06 1600")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse(
                                "event meeting /from  /to 2026-08-06 1600")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse(
                                "event meeting /from 2026-08-06 1400 /to ")),
                () -> assertThrows(MochiException.class,
                        () -> Parser.parse(
                                "event meeting /from 2026-08-06 1600 /to 2026-08-06 1400"))
        );
    }

    @Test
    public void parse_unknownOrBlankCommand_exceptionThrown() {
        assertAll(
                () -> assertThrows(MochiException.class, () -> Parser.parse("dance")),
                () -> assertThrows(MochiException.class, () -> Parser.parse("   "))
        );
    }
}
