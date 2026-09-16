package bags.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bags.exception.BagsException;
import bags.task.Deadline;
import bags.task.Event;
import bags.task.Task;
import bags.task.TaskType;
import bags.task.Todo;

/**
 * Tests the {@link Parser} class for converting stored task records
 * into their equivalent {@link Task} objects.
 *
 * <p>
 * These tests verify that Todo, Deadline, and Event task records are
 * correctly parsed into the correct format.
 * </p>
 */
public class ParseTest {

    @Test
    void parseTask_validReadingFileRecords_createsCorrectTasks()
            throws BagsException {
        Parser parser = new Parser();

        DateTimeFormatter inputFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<String> readingFileRecords = List.of(
                "T | [ ] | Read book",
                "D | [ ] | Submit assignment | 2026-08-25 23:59",
                "E | [ ] | Project meeting | 2026-08-27 14:00 | 2026-08-27 16:00"
        );

        List<Task> tasks = new ArrayList<>();

        for (String record : readingFileRecords) {
            Task task = parser.parseTask(record);
            tasks.add(task);
        }

        // Todo task
        assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("Read book", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());

        // Deadline task
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertEquals("Submit assignment", tasks.get(1).getDescription());
        assertFalse(tasks.get(1).isDone());

        String[] deadlineParts = readingFileRecords.get(1).split("\\|");
        String deadline = deadlineParts[3].trim();

        assertDoesNotThrow(() -> LocalDateTime.parse(deadline, inputFormatter));

        // Event task
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("Project meeting", tasks.get(2).getDescription());
        assertFalse(tasks.get(2).isDone());

        String[] eventParts = readingFileRecords.get(2).split("\\|");
        String from = eventParts[3].trim();
        String to = eventParts[4].trim();

        assertDoesNotThrow(() -> LocalDateTime.parse(from, inputFormatter));
        assertDoesNotThrow(() -> LocalDateTime.parse(to, inputFormatter));
    }

    @Test
    void parseCommand_supportedCommands_returnsCorrespondingCommand() {
        Parser parser = new Parser();

        assertEquals(Command.ADD_TASK, parser.parseCommand("add task"));
        assertEquals(Command.LIST, parser.parseCommand("list"));
        assertEquals(Command.MARK, parser.parseCommand("mark 1"));
        assertEquals(Command.UNMARK, parser.parseCommand("unmark 1"));
        assertEquals(Command.ECHO, parser.parseCommand("echo hello"));
        assertEquals(Command.SEARCH, parser.parseCommand("search book"));
        assertEquals(Command.DELETE, parser.parseCommand("delete 1"));
        assertEquals(Command.BYE, parser.parseCommand("bye"));
        assertEquals(Command.EDIT, parser.parseCommand("edit 1"));
    }

    @Test
    void parseCommand_emptyOrUnknownInput_returnsCorrespondingCommand() {
        Parser parser = new Parser();

        assertEquals(Command.EMPTY, parser.parseCommand("   "));
        assertEquals(Command.UNKNOWN, parser.parseCommand("remind me"));
    }

    @Test
    void parseTaskType_supportedAndUnsupportedTypes_returnsExpectedType() {
        Parser parser = new Parser();

        assertEquals(TaskType.TODO, parser.parseTaskType("todo Read book"));
        assertEquals(TaskType.DEADLINE, parser.parseTaskType("deadline Submit report"));
        assertEquals(TaskType.EVENT, parser.parseTaskType("event Project meeting"));
        assertNull(parser.parseTaskType("reminder Buy milk"));
    }

    @Test
    void parseTask_completedRecord_marksTaskAsDone() throws BagsException {
        Parser parser = new Parser();

        Task task = parser.parseTask("T | [X] | Read book");

        assertInstanceOf(Todo.class, task);
        assertTrue(task.isDone());
        assertEquals("Read book", task.getDescription());
    }

    @Test
    void parseTask_incompleteOrUnsupportedRecord_returnsNull() throws BagsException {
        Parser parser = new Parser();

        assertNull(parser.parseTask("T | [ ]"));
        assertNull(parser.parseTask("D | [ ] | Submit report"));
        assertNull(parser.parseTask("R | [ ] | Buy milk"));
    }

    @Test
    void parseTask_invalidDateRecord_throwsException() {
        Parser parser = new Parser();

        BagsException exception = assertThrows(BagsException.class, () ->
                parser.parseTask("D | [ ] | Submit report | not-a-date")
        );

        assertTrue(exception.getMessage().contains("Invalid date"));
    }
}
