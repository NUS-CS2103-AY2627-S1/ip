package bingus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bingus.command.Command;
import bingus.command.UpdateCommand;
import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.TaskList;
import bingus.task.Todo;
import bingus.ui.Ui;

/**
 * Tests parsing and execution of task update commands.
 */
class ParserUpdateTest {
    /** Directory created by JUnit for temporary test data. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Creates storage backed by a test-specific save file.
     *
     * @return storage for executing parsed commands.
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
    }

    @Test
    void parseUpdateDescription_todo_returnsExecutableUpdateCommand() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        Command command = parser.parse("update 1 /desc read two chapters", tasks);
        command.execute(tasks, new Ui(), createStorage());

        assertInstanceOf(UpdateCommand.class, command);
        Todo updatedTodo = assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("read two chapters", updatedTodo.getDescription());
    }

    @Test
    void parseUpdateDescription_deadline_preservesDueDate() {
        Parser parser = new Parser();
        LocalDateTime dueDate = LocalDateTime.of(2026, 9, 15, 23, 59);
        TaskList tasks = new TaskList(List.of(new Deadline("submit draft", dueDate)));

        Command command = parser.parse("update 1 /desc submit final report", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Deadline updatedDeadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("submit final report", updatedDeadline.getDescription());
        assertEquals(dueDate, updatedDeadline.getBy());
    }

    @Test
    void parseUpdateDescription_event_preservesStartAndEndTimes() {
        Parser parser = new Parser();
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 10, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 9, 10, 16, 0);
        TaskList tasks = new TaskList(List.of(new Event("project meeting", startTime, endTime)));

        Command command = parser.parse("update 1 /desc weekly team meeting", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Event updatedEvent = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("weekly team meeting", updatedEvent.getDescription());
        assertEquals(startTime, updatedEvent.getFrom());
        assertEquals(endTime, updatedEvent.getTo());
    }

    @Test
    void parseUpdateDeadline_validDateTime_preservesDescriptionAndUpdatesDueDate() {
        Parser parser = new Parser();
        LocalDateTime originalDueDate = LocalDateTime.of(2026, 9, 15, 23, 59);
        TaskList tasks = new TaskList(List.of(new Deadline("submit report", originalDueDate)));

        Command command = parser.parse("update 1 /by 2026-09-20 1800", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Deadline updatedDeadline = assertInstanceOf(Deadline.class, tasks.get(0));
        assertEquals("submit report", updatedDeadline.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 20, 18, 0), updatedDeadline.getBy());
    }

    @Test
    void parseUpdateDeadline_missingDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDateTime.of(2026, 9, 15, 23, 59))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by", tasks));
    }

    @Test
    void parseUpdateDeadline_invalidDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDateTime.of(2026, 9, 15, 23, 59))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by 2026-02-30 1200", tasks));
    }

    @Test
    void parseUpdateDeadline_todo_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by 2026-09-20 1800", tasks));
    }

    @Test
    void parseUpdateDeadline_event_throwsException() {
        Parser parser = new Parser();
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 20, 14, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 9, 20, 16, 0);
        TaskList tasks = new TaskList(List.of(new Event("project meeting", startTime, endTime)));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /by 2026-09-20 1800", tasks));
    }

    @Test
    void parseUpdateEvent_validStartTime_preservesDescriptionAndEndTime() {
        Parser parser = new Parser();
        LocalDateTime originalStartTime = LocalDateTime.of(2026, 9, 20, 14, 0);
        LocalDateTime originalEndTime = LocalDateTime.of(2026, 9, 20, 16, 0);
        TaskList tasks = new TaskList(List.of(
                new Event("project meeting", originalStartTime, originalEndTime)));

        Command command = parser.parse("update 1 /from 2026-09-20 1500", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Event updatedEvent = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("project meeting", updatedEvent.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 20, 15, 0), updatedEvent.getFrom());
        assertEquals(originalEndTime, updatedEvent.getTo());
    }

    @Test
    void parseUpdateEvent_validEndTime_preservesDescriptionAndStartTime() {
        Parser parser = new Parser();
        LocalDateTime originalStartTime = LocalDateTime.of(2026, 9, 20, 14, 0);
        LocalDateTime originalEndTime = LocalDateTime.of(2026, 9, 20, 16, 0);
        TaskList tasks = new TaskList(List.of(
                new Event("project meeting", originalStartTime, originalEndTime)));

        Command command = parser.parse("update 1 /to 2026-09-20 1730", tasks);
        command.execute(tasks, new Ui(), createStorage());

        Event updatedEvent = assertInstanceOf(Event.class, tasks.get(0));
        assertEquals("project meeting", updatedEvent.getDescription());
        assertEquals(originalStartTime, updatedEvent.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 20, 17, 30), updatedEvent.getTo());
    }

    @Test
    void parseUpdateEvent_missingDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /from", tasks));
    }

    @Test
    void parseUpdateEvent_invalidDateTime_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /to 2026-02-30 1200", tasks));
    }

    @Test
    void parseUpdateEvent_startNotBeforeEnd_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /from 2026-09-20 1600", tasks));
    }

    @Test
    void parseUpdateEvent_endNotAfterStart_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Event(
                "project meeting",
                LocalDateTime.of(2026, 9, 20, 14, 0),
                LocalDateTime.of(2026, 9, 20, 16, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /to 2026-09-20 1400", tasks));
    }

    @Test
    void parseUpdateEvent_todo_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /from 2026-09-20 1500", tasks));
    }

    @Test
    void parseUpdateEvent_deadline_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 18, 0))));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /to 2026-09-20 1900", tasks));
    }

    @Test
    void parseUpdate_missingArguments_throwsException() {
        Parser parser = new Parser();

        assertThrows(BingusException.class, () -> parser.parse("update", new TaskList()));
    }

    @Test
    void parseUpdate_missingField_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1", tasks));
    }

    @Test
    void parseUpdate_missingDescription_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /desc", tasks));
    }

    @Test
    void parseUpdate_unknownField_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 1 /unknown new value", tasks));
    }

    @Test
    void parseUpdate_outOfRangeTaskId_throwsException() {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(BingusException.class, () -> parser.parse("update 2 /desc read two chapters", tasks));
    }
}

