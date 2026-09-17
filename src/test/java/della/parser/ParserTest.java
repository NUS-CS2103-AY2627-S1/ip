package della.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import della.command.Command;
import della.task.Deadline;
import della.task.Event;
import della.task.Task;
import della.task.Todo;

public class ParserTest {
    @Test
    public void parseCommand_recognisedCommands_correctCommandReturned() {
        assertEquals(Command.TODO, Parser.parseCommand("todo read book"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline report"));
        assertEquals(Command.EVENT, Parser.parseCommand("event meeting"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.MARK, Parser.parseCommand("mark 1"));
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark 1"));
        assertEquals(Command.DELETE, Parser.parseCommand("delete 1"));
        assertEquals(Command.FIND, Parser.parseCommand("find book"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    @Test
    public void parseCommand_unknownCommand_unknownReturned() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("hello"));
    }

    @Test
    public void parseArguments_inputWithArguments_argumentsReturned() {
        assertEquals("read book", Parser.parseArguments("todo read book"));
    }

    @Test
    public void parseTaskNumber_validNumber_numberReturned() {
        assertEquals(12, Parser.parseTaskNumber("12"));
    }

    @Test
    public void parseTaskNumber_invalidNumber_exceptionThrown() {
        assertThrows(NumberFormatException.class, () -> Parser.parseTaskNumber("one"));
    }

    @Test
    public void parseTodo_validDescription_todoReturned() {
        assertEquals(new Todo("read book"), Parser.parseTodo("read book"));
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_validFutureDate_deadlineReturned() {
        Deadline deadline = Parser.parseDeadline("submit report /by 31/12/2099 1200");
        assertEquals(new Deadline("submit report", LocalDateTime.of(2099, 12, 31, 12, 0)), deadline);
        assertEquals("D|0|submit report|Dec 31 2099 12:00pm", deadline.formatForStorage());
    }

    @Test
    public void parseDeadline_missingBy_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseDeadline("submit report"));
    }

    @Test
    public void parseDeadline_invalidDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> Parser.parseDeadline("report /by 31/12/2099 1260"));
    }

    @Test
    public void parseEvent_validFutureDates_eventReturned() {
        Event event = Parser.parseEvent("meeting /from 31/12/2099 1200 /to 31/12/2099 1300");
        assertEquals(new Event("meeting", LocalDateTime.of(2099, 12, 31, 12, 0),
                LocalDateTime.of(2099, 12, 31, 13, 0)), event);
    }

    @Test
    public void parseEvent_missingMarker_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseEvent("meeting /from 31/12/2099 1200"));
    }

    @Test
    public void parseEvent_endBeforeStart_exceptionThrown() {
        assertThrows(
                IllegalArgumentException.class, () -> Parser.parseEvent(
                        "meeting /from 31/12/2099 1300 /to 31/12/2099 1200"));
    }

    @Test
    public void parseFindTask_matchingWord_matchingTasksReturned() {
        ArrayList<Task> tasks = new ArrayList<>(List.of(new Todo("read book"), new Todo("buy milk")));
        assertEquals(List.of(tasks.get(0)), Parser.parseFindTask("book", tasks));
    }

    @Test
    public void parseFindTask_emptyKeyword_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parseFindTask("", new ArrayList<>()));
    }
}
