package et.parser;

import et.command.AddCommand;
import et.command.Command;
import et.command.DeleteCommand;
import et.command.ExitCommand;
import et.command.FindCommand;
import et.command.ListCommand;
import et.command.MarkCommand;
import et.command.SortCommand;
import et.command.UnmarkCommand;
import et.exception.ETException;
import et.storage.Storage;
import et.task.Deadline;
import et.task.Event;
import et.task.Task;
import et.task.TaskList;
import et.task.Todo;
import et.ui.Ui;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests conversion of command text into commands and their corresponding tasks.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommand_todo_createsTodoWithDescription() throws ETException {
        Task task = executeAddCommand("todo read notes");

        assertInstanceOf(Todo.class, task);
        assertEquals("read notes", task.getDescription());
    }

    @Test
    void parseCommand_deadline_createsDeadlineWithDateOnly() throws ETException {
        Task task = executeAddCommand("deadline submit assignment /by 2019-1-5");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit assignment", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 1, 5, 0, 0), deadline.getBy());
        assertFalse(deadline.hasTime());
    }

    @Test
    void parseCommand_event_createsEventWithDatesAndTimes() throws ETException {
        Task task = executeAddCommand(
                "event project meeting /from Dec 2, 2019 9:00 am /to 2019-12-2 10:00");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 9, 0), event.getFrom());
        assertTrue(event.hasStartTime());
        assertEquals(LocalDateTime.of(2019, 12, 2, 10, 0), event.getTo());
        assertTrue(event.hasEndTime());
    }

    @Test
    void parseCommand_controlCommands_createsMatchingCommandTypes() throws ETException {
        assertInstanceOf(ListCommand.class, parser.parseCommand("list"));
        assertInstanceOf(SortCommand.class, parser.parseCommand("sort"));
        assertInstanceOf(FindCommand.class, parser.parseCommand("find notes"));
        assertInstanceOf(ExitCommand.class, parser.parseCommand("bye"));
        assertInstanceOf(MarkCommand.class, parser.parseCommand("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parseCommand("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parseCommand("delete 1"));
    }

    @Test
    void parseCommand_taskIndexCommands_updatesRequestedTask() throws ETException {
        TaskList tasks = new TaskList(List.of(new Todo("first task"), new Todo("second task")));
        RecordingStorage storage = new RecordingStorage();
        Ui ui = new Ui();

        parser.parseCommand("mark 2").execute(tasks, ui, storage);
        assertTrue(tasks.getTask(1).isDone());

        parser.parseCommand("unmark 2").execute(tasks, ui, storage);
        assertFalse(tasks.getTask(1).isDone());

        parser.parseCommand("delete 1").execute(tasks, ui, storage);
        assertEquals(1, tasks.size());
        assertEquals("second task", tasks.getTask(0).getDescription());
        assertEquals(tasks.getTasks(), storage.lastSavedTasks);
    }

    @Test
    void parseCommand_missingRequiredTaskInformation_etExceptionThrown() {
        assertThrows(ETException.class, () -> parser.parseCommand("todo"));
        assertThrows(ETException.class, () -> parser.parseCommand("deadline submit assignment"));
        assertThrows(ETException.class, () -> parser.parseCommand("deadline /by 2019-1-5"));
        assertThrows(ETException.class, () -> parser.parseCommand("event project meeting /from 2/1/2019"));
        assertThrows(ETException.class, () -> parser.parseCommand("event /from 2/1/2019 /to 3/1/2019"));
        assertThrows(ETException.class, () -> parser.parseCommand("mark"));
        assertThrows(ETException.class, () -> parser.parseCommand("find"));
    }

    @Test
    void parseCommand_reversedEventOrInvalidCommand_etExceptionThrown() {
        assertThrows(ETException.class,
                () -> parser.parseCommand("event backwards /from 2/12/2019 1800 /to 2/12/2019 1700"));
        assertThrows(ETException.class, () -> parser.parseCommand("list now"));
        assertThrows(ETException.class, () -> parser.parseCommand("sort now"));
        assertThrows(ETException.class, () -> parser.parseCommand("bye now"));
        assertThrows(ETException.class, () -> parser.parseCommand("remind me"));
    }

    /**
     * Parses and executes an add command, returning the task it adds.
     *
     * @param input the command text to parse
     * @return the added task
     * @throws ETException if the command cannot be parsed or executed
     */
    private Task executeAddCommand(String input) throws ETException {
        Command command = parser.parseCommand(input);
        assertInstanceOf(AddCommand.class, command);
        TaskList tasks = new TaskList();
        command.execute(tasks, new Ui(), new RecordingStorage());
        return tasks.getTask(0);
    }

    /** Records saved tasks in memory so command tests do not write to disk. */
    private static class RecordingStorage extends Storage {
        private List<Task> lastSavedTasks = List.of();

        @Override
        public void save(List<Task> tasks) {
            lastSavedTasks = List.copyOf(tasks);
        }
    }
}
