package sioet.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.TaskList;
import sioet.task.Todo;
import sioet.ui.Ui;

public class CommandTest {
    private static final Path FILE_PATH = Path.of("data", "sioet.txt");

    private final Storage storage = new Storage();
    private final Ui ui = new Ui();

    @AfterEach
    public void cleanUp() throws IOException {
        Files.deleteIfExists(FILE_PATH);
    }

    @Test
    public void todoCommand_addsTodo() throws SioetException {
        TaskList tasks = new TaskList();

        TodoCommand command = new TodoCommand("read book");
        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals(
                "Got it. I've added this task:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 1 tasks in the list.",
                ui.getLastResponse()
        );
    }

    @Test
    public void todoCommand_blankDescription_throwsException() {
        TaskList tasks = new TaskList();

        TodoCommand command = new TodoCommand("   ");

        assertThrows(
                SioetException.class, () -> command.execute(tasks, ui, storage));

        assertEquals(0, tasks.size());
    }

    @Test
    public void deadlineCommand_addsDeadline() throws SioetException {
        TaskList tasks = new TaskList();

        DeadlineCommand command =
                new DeadlineCommand("return book /by 6/6/2026 2359");

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("return book", tasks.get(0).getDescription());
    }

    @Test
    public void deadlineCommand_missingBy_throwsException() {
        TaskList tasks = new TaskList();

        DeadlineCommand command =
                new DeadlineCommand("return book");

        assertThrows(
                SioetException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void eventCommand_addsEvent() throws SioetException {
        TaskList tasks = new TaskList();

        EventCommand command = new EventCommand(
                "project meeting /from 6/8/2026 1400 /to 6/8/2026 1600"
        );

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("project meeting", tasks.get(0).getDescription());
    }

    @Test
    public void eventCommand_missingFrom_throwsException() {
        TaskList tasks = new TaskList();

        EventCommand command =
                new EventCommand("project meeting /to 6/8/2026 1600");

        assertThrows(
                SioetException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void recurringTaskCommand_addsRecurringTask()
            throws SioetException {
        TaskList tasks = new TaskList();

        RecurringTaskCommand command =
                new RecurringTaskCommand("project meeting /week");

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("project meeting", tasks.get(0).getDescription());
    }

    @Test
    public void recurringTaskCommand_invalidRecurrence_throwsException() {
        TaskList tasks = new TaskList();

        RecurringTaskCommand command =
                new RecurringTaskCommand("project meeting /year");

        assertThrows(
                SioetException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void markCommand_marksTaskAsDone() throws SioetException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        MarkCommand command = new MarkCommand("1", true);
        command.execute(tasks, ui, storage);

        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void unmarkCommand_marksTaskAsNotDone() throws SioetException {
        TaskList tasks = new TaskList();

        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);

        MarkCommand command = new MarkCommand("1", false);
        command.execute(tasks, ui, storage);

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void markCommand_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        MarkCommand command = new MarkCommand("2", true);

        assertThrows(
                SioetException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void deleteCommand_removesTask() throws SioetException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("go shopping"));

        DeleteCommand command = new DeleteCommand("1");
        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("go shopping", tasks.get(0).getDescription());
    }

    @Test
    public void deleteCommand_multipleTasks_removesCorrectTasks()
            throws SioetException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("go shopping"));
        tasks.add(new Todo("finish homework"));

        DeleteCommand command = new DeleteCommand("1, 3");
        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("go shopping", tasks.get(0).getDescription());
    }

    @Test
    public void deleteCommand_invalidTaskNumber_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        DeleteCommand command = new DeleteCommand("2");

        assertThrows(
                SioetException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void findCommand_showsMatchingTasks() throws SioetException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("go shopping"));

        FindCommand command = new FindCommand("book");
        command.execute(tasks, ui, storage);

        assertTrue(
                ui.getLastResponse().contains("read book")
        );
    }

    @Test
    public void findCommand_noMatch_showsNoMatchingTasks()
            throws SioetException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        FindCommand command = new FindCommand("exam");
        command.execute(tasks, ui, storage);

        assertEquals(
                "No matching tasks found.",
                ui.getLastResponse()
        );
    }

    @Test
    public void findCommand_blankKeyword_throwsException() {
        assertThrows(
                SioetException.class, () -> new FindCommand(""));
    }

    @Test
    public void listCommand_showsAllTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("go shopping"));

        ListCommand command = new ListCommand();
        command.execute(tasks, ui, storage);

        assertTrue(ui.getLastResponse().contains("read book"));
        assertTrue(ui.getLastResponse().contains("go shopping"));
    }

    @Test
    public void exitCommand_isExit() {
        ExitCommand command = new ExitCommand();

        assertTrue(command.isExit());
    }

    @Test
    public void exitCommand_showsByeMessage() {
        TaskList tasks = new TaskList();

        ExitCommand command = new ExitCommand();
        command.execute(tasks, ui, storage);

        assertEquals(
                "Bye! Hope to see you again soon!",
                ui.getLastResponse()
        );
    }
}
