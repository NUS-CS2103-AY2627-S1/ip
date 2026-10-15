package chudgpt.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;
import chudgpt.ui.Ui;

/** Tests the behavior and persistence of the application's basic commands. */
public class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_addTask_addsSavesAndReturnsConfirmation() throws Exception {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList();
        Storage storage = createStorage();

        String response = new AddTaskCommand(task).execute(tasks, new Ui(), storage);

        assertEquals("Got it. I've added this task:\n  [T][ ][P:NONE   ] read book\n"
                + "Now you have 1 tasks in your list.", response);
        assertEquals("1. [T][ ][P:NONE   ] read book", tasks.toString());
        assertEquals("[T][ ][P:NONE   ] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_deleteTask_deletesSavesAndReturnsConfirmation() throws Exception {
        TaskList tasks = new TaskList(List.of(new ToDo("first"), new ToDo("second")));
        Storage storage = createStorage();

        String response = new DeleteTaskCommand(0).execute(tasks, new Ui(), storage);

        assertEquals("Got it. Noted. I've removed this task:\n  [T][ ][P:NONE   ] first\n"
                + "Now you have 1 tasks in your list.", response);
        assertEquals("1. [T][ ][P:NONE   ] second", tasks.toString());
        assertEquals("[T][ ][P:NONE   ] second", storage.load().get(0).toString());
    }

    @Test
    public void execute_changeTaskStatus_marksAndUnmarksSavesAndReturnsConfirmations() throws Exception {
        Task task = new ToDo("task");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = createStorage();
        Ui ui = new Ui();

        String markResponse = new ChangeTaskStatusCommand(0, true).execute(tasks, ui, storage);

        assertEquals("Nice! I've marked this task as completed!\n  [T][X][P:NONE   ] task", markResponse);
        assertTrue(task.isCompleted());
        assertTrue(storage.load().get(0).isCompleted());

        String unmarkResponse = new ChangeTaskStatusCommand(0, false).execute(tasks, ui, storage);

        assertEquals("OK, I've marked this task as incomplete.\n  [T][ ][P:NONE   ] task", unmarkResponse);
        assertFalse(task.isCompleted());
        assertFalse(storage.load().get(0).isCompleted());
    }

    @Test
    public void execute_find_returnsMatchingAndEmptyMessagesWithoutSaving() throws Exception {
        TaskList tasks = new TaskList(List.of(new ToDo("Read Book"), new ToDo("write report")));
        Ui ui = new Ui();

        assertEquals("Here are the matching tasks in your list:\n1. [T][ ][P:NONE   ] Read Book",
                new FindCommand("book").execute(tasks, ui, null));
        assertEquals("I couldn't find any tasks that contains that keyword.",
                new FindCommand("missing").execute(tasks, ui, null));
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt")));
    }

    @Test
    public void execute_findBlankKeyword_exceptionThrown() {
        ChudException exception = assertThrows(ChudException.class, () ->
                new FindCommand("   ").execute(new TaskList(), new Ui(), null));

        assertEquals("Please enter a keyword to search for", exception.getMessage());
    }

    @Test
    public void execute_hi_returnsGreetingWithoutChangingExitStatus() {
        HiCommand command = new HiCommand();

        assertEquals("Hi! I'm ChudGPT. How can I help you?",
                command.execute(new TaskList(), new Ui(), null));
        assertFalse(command.isExit());
    }

    @Test
    public void execute_save_writesTasksAndReturnsConfirmation() throws Exception {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));
        Storage storage = createStorage();

        String response = new SaveCommand().execute(tasks, new Ui(), storage);

        assertEquals("I've saved your current list of tasks.", response);
        assertEquals("T | 0 | NONE | task", Files.readString(temporaryDirectory.resolve("tasks.txt")));
    }

    @Test
    public void execute_exit_savesTasksReturnsGoodbyeAndSignalsExit() throws Exception {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));
        Storage storage = createStorage();
        ExitCommand command = new ExitCommand();

        String response = command.execute(tasks, new Ui(), storage);

        assertEquals("Bye. Hope to see you again soon!", response);
        assertEquals("T | 0 | NONE | task", Files.readString(temporaryDirectory.resolve("tasks.txt")));
        assertTrue(command.isExit());
    }

    /** Returns storage backed by the test's temporary task file. */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
