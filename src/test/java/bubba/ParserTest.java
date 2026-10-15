package bubba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Checks command interpretation and rejection of malformed user input.
 */
public class ParserTest {
    @Test
    public void parse_commandsWithoutArguments_returnsCorrectTypes() throws BubbaException {
        assertEquals(Parser.Type.LIST, Parser.parse("list").type());
        assertEquals(Parser.Type.HELP, Parser.parse("help").type());
        assertEquals(Parser.Type.EXIT, Parser.parse("bye").type());
    }

    @Test
    public void parse_surroundingWhitespace_ignoresWhitespace() throws BubbaException {
        assertEquals(Parser.Type.LIST, Parser.parse("   list   ").type());
        assertEquals("read book", Parser.parse("   todo read book   ").task().description);
    }

    @Test
    public void parse_numberedCommands_preservesTaskNumber() throws BubbaException {
        String[] inputs = {"mark 2", "unmark 2", "delete 2"};
        Parser.Type[] types = {Parser.Type.MARK, Parser.Type.UNMARK, Parser.Type.DELETE};
        for (int i = 0; i < inputs.length; i++) {
            Parser.ParsedCommand command = Parser.parse(inputs[i]);
            assertEquals(types[i], command.type(), inputs[i]);
            assertEquals(2, command.taskNumber(), inputs[i]);
        }
    }

    @Test
    public void parse_find_preservesTrimmedKeyword() throws BubbaException {
        Parser.ParsedCommand command = Parser.parse("find   return book  ");
        assertEquals(Parser.Type.FIND, command.type());
        assertEquals("return book", command.keyword());
    }

    @Test
    public void parse_todo_createsTaskWithDescription() throws BubbaException {
        Parser.ParsedCommand command = Parser.parse("todo read book");
        assertEquals(Parser.Type.ADD, command.type());
        Todo task = assertInstanceOf(Todo.class, command.task());
        assertEquals("read book", task.description);
    }

    @Test
    public void parse_deadline_parsesDateAndDescription() throws BubbaException {
        Parser.ParsedCommand command = Parser.parse("deadline return book /by 2/12/2026 1800");
        assertEquals(Parser.Type.ADD, command.type());
        Deadline task = assertInstanceOf(Deadline.class, command.task());
        assertEquals("return book", task.description);
        assertEquals(LocalDateTime.of(2026, 12, 2, 18, 0), task.by);
    }

    @Test
    public void parse_event_parsesBothTimes() throws BubbaException {
        Parser.ParsedCommand command = Parser.parse(
                "event meeting /from 3/12/2026 1400 /to 3/12/2026 1600");
        assertEquals(Parser.Type.ADD, command.type());
        Event task = assertInstanceOf(Event.class, command.task());
        assertEquals("meeting", task.description);
        assertEquals(LocalDateTime.of(2026, 12, 3, 14, 0), task.from);
        assertEquals(LocalDateTime.of(2026, 12, 3, 16, 0), task.to);
    }

    @Test
    public void parse_missingOrInvalidTaskNumber_throwsException() {
        for (String input : new String[] {"mark", "unmark", "delete", "mark abc",
            "unmark 1.5", "delete 2147483648"}) {
            assertThrows(BubbaException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    public void parse_missingTaskDetails_throwsException() {
        for (String input : new String[] {"find", "find   ", "todo", "todo   ",
            "deadline", "deadline book",
            "deadline book /by ", "event", "event meeting",
            "event meeting /from 3/12/2026 1400",
            "event meeting /from  /to 3/12/2026 1600",
            "event meeting /from 3/12/2026 1400 /to "}) {
            assertThrows(BubbaException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    public void parse_invalidDateFormat_throwsException() {
        for (String input : new String[] {"deadline book /by tomorrow",
            "event meeting /from tomorrow /to 3/12/2026 1600",
            "event meeting /from 3/12/2026 1400 /to tomorrow"}) {
            assertThrows(BubbaException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    public void parse_eventWithInvalidTimeRange_throwsException() {
        for (String input : new String[] {
            "event meeting /from 3/12/2026 1600 /to 3/12/2026 1400",
            "event meeting /from 3/12/2026 1600 /to 3/12/2026 1600"}) {
            assertThrows(BubbaException.class, () -> Parser.parse(input), input);
        }
    }

    @Test
    public void parse_unknownOrEmptyCommand_throwsException() {
        for (String input : new String[] {"", "hello", "list extra", "help extra", "todoSomething"}) {
            assertThrows(BubbaException.class, () -> Parser.parse(input), input);
        }
    }
}
