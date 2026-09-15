package tianyi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import tianyi.TianyiException;
import tianyi.storage.Storage;
import tianyi.task.Deadline;
import tianyi.task.Event;
import tianyi.task.Task;
import tianyi.task.TaskList;
import tianyi.task.TaskTime;
import tianyi.task.ToDo;

/**
 * Tests parsing and validation for all supported command forms.
 */
public class CommandParserTest {
    private final CommandParser parser = new CommandParser();
    private final Storage storage = new NoOpStorage();

    @Test
    public void parse_taskCreationCommands_returnsAddCommands()
            throws TianyiException {
        TaskList tasks = new TaskList();

        assertInstanceOf(AddCommand.class, parser.parse("ToDo Buy Milk", tasks));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline submit report /by 2-12-2019 18:00", tasks));
        assertInstanceOf(AddCommand.class,
                parser.parse("event workshop /from 2-12-2019 /to 3-12-2019 16:00", tasks));
    }

    @Test
    public void parse_markCommand_marksRequestedTask()
            throws TianyiException {
        TaskList tasks = createTwoTodoTasks();

        parser.parse("mark 2", tasks).execute(tasks, storage);

        assertEquals("T | 0 | first", tasks.getTasks().get(0).getData());
        assertEquals("T | 1 | second", tasks.getTasks().get(1).getData());
    }

    @Test
    public void parse_unmarkCommand_unmarksRequestedTask()
            throws TianyiException {
        TaskList tasks = createTwoTodoTasks();
        tasks.markTask(0);

        parser.parse("unmark 1", tasks).execute(tasks, storage);

        assertEquals("T | 0 | first", tasks.getTasks().get(0).getData());
    }

    @Test
    public void parse_deleteCommand_deletesRequestedTask()
            throws TianyiException {
        TaskList tasks = createTwoTodoTasks();

        parser.parse("delete 2", tasks).execute(tasks, storage);

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | first", tasks.getTasks().get(0).getData());
    }

    @Test
    public void parse_listWithoutDate_listsAllTasks()
            throws TianyiException {
        TaskList tasks = createTwoTodoTasks();

        String response = parser.parse("list", tasks).execute(tasks, storage).getFullMessage();

        assertEquals("Here's your task list. Let's have a look:\n"
                + "1.[T][ ] first\n"
                + "2.[T][ ] second", response);
    }

    @Test
    public void parse_listWithDate_listsTasksOccurringOnDate()
            throws TianyiException {
        TaskList tasks = new TaskList(List.of(
                new ToDo("buy milk"),
                new Deadline("submit report", new TaskTime("3-12-2019 18:00")),
                new Event("workshop", new TaskTime("2-12-2019"),
                        new TaskTime("3-12-2019 16:00"))));

        String response = parser.parse("list 2-12-2019", tasks).execute(tasks, storage).getFullMessage();

        assertEquals("Here are your deadlines and events for 2-12-2019:\n"
                + "2.[D][ ] submit report (by: Tue, Dec 03 2019, 6:00 PM)\n"
                + "3.[E][ ] workshop (from: Mon, Dec 02 2019 "
                + "to: Tue, Dec 03 2019, 4:00 PM)", response);
    }

    @Test
    public void parse_findCommand_listsTasksContainingKeyword()
            throws TianyiException {
        TaskList tasks = new TaskList(List.of(
                new ToDo("read book"),
                new ToDo("buy milk"),
                new ToDo("return book")));

        String response = parser.parse("find book", tasks).execute(tasks, storage).getFullMessage();

        assertEquals("Here's what I found for you:\n"
                + "1.[T][ ] read book\n"
                + "3.[T][ ] return book", response);
    }

    @Test
    public void parse_byeCommand_returnsExitCommand()
            throws TianyiException {
        Command command = parser.parse("bye", new TaskList());

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.execute(new TaskList(), storage).isExit());
    }

    @Test
    public void parse_helpCommand_returnsCommandDescriptions()
            throws TianyiException {
        TaskList tasks = new TaskList();

        String response = parser.parse("help", tasks).execute(tasks, storage).getFullMessage();

        assertEquals("Here is the list of commands:\n" + CommandType.getCommands()
                + "\n\nI hope it helps you!", response);
    }

    @Test
    public void parse_nonExitCommand_isExitReturnsFalse()
            throws TianyiException {
        Command command = parser.parse("list", new TaskList());

        assertFalse(command.execute(new TaskList(), storage).isExit());
    }

    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        assertParseFails("mark", createTwoTodoTasks(),
                "Please specify a task number.\nTry: mark 1");
    }

    @Test
    public void parse_taskNumberWithEmptyList_exceptionThrown() {
        assertParseFails("delete 1", new TaskList(),
                "There is no task in your list.\n"
                        + "Please add a task.\n"
                        + "Try: todo borrow book");
    }

    @Test
    public void parse_invalidTaskNumbers_exceptionThrown() {
        TaskList tasks = createTwoTodoTasks();

        assertParseFails("mark abc", tasks,
                "abc is not a valid task number.\nTry: mark 1");
        assertParseFails("mark 0", tasks,
                "Task number 0 does not exist.\n"
                        + "Please enter a number from 1 to 2.\n"
                        + "Try: mark 1");
        assertParseFails("mark 3", tasks,
                "Task number 3 does not exist.\n"
                        + "Please enter a number from 1 to 2.\n"
                        + "Try: mark 1");
    }

    @Test
    public void parse_listWithInvalidDate_exceptionThrown() {
        assertParseFails("list 31-2-2019", new TaskList(),
                "Invalid [list] date.\nPlease use d-M-yyyy.\nTry: list 2-12-2019");
        assertParseFails("list 2-12-2019 18:00", new TaskList(),
                "Invalid [list] date.\nPlease use d-M-yyyy.\nTry: list 2-12-2019");
    }

    @Test
    public void parse_byeWithArgument_exceptionThrown() {
        assertParseFails("bye now", new TaskList(),
                "[bye] does not accept any arguments.\nTry: bye");
    }

    @Test
    public void parse_helpWithArgument_exceptionThrown() {
        assertParseFails("help commands", new TaskList(),
                "[help] does not accept any arguments.\nTry: help");
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertParseFails("find", new TaskList(),
                "The keyword of [find] cannot be empty.\nTry: find book");
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertParseFails("abracadabra", new TaskList(),
                "I'm sorry, but I don't know what that means.");
    }

    private TaskList createTwoTodoTasks() {
        return new TaskList(List.of(new ToDo("first"), new ToDo("second")));
    }

    private void assertParseFails(String input, TaskList tasks, String expectedMessage) {
        TianyiException exception = assertThrows(
                TianyiException.class, () -> parser.parse(input, tasks));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /**
     * Avoids file-system writes while exercising parsed commands.
     */
    private static class NoOpStorage extends Storage {
        NoOpStorage() {
            super("unused");
        }

        @Override
        public void save(List<Task> tasks) {
            // Saving is outside the scope of parser tests.
        }
    }
}
