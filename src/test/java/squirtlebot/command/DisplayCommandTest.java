package squirtlebot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import squirtlebot.storage.Storage;
import squirtlebot.task.TaskList;
import squirtlebot.task.ToDo;
import squirtlebot.ui.Ui;

/**
 * Tests commands that display tasks or terminate the application.
 */
public class DisplayCommandTest {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        storage = new Storage();
        storage.disable();
        tasks = new TaskList();
        tasks.add(new ToDo("read textbook"));
        tasks.add(new ToDo("buy groceries"));
        ui = new Ui();
    }

    @Test
    public void list_execute_displaysAllTasks() {
        new ListCommand().execute(tasks, ui, storage);

        assertEquals("1. [T] [ ] read textbook\n2. [T] [ ] buy groceries", ui.getSavedMessage());
    }

    @Test
    public void list_emptyTaskList_displaysEmptyListMessage() {
        tasks.clear();

        new ListCommand().execute(tasks, ui, storage);

        assertEquals("Your list of tasks is currently empty :)", ui.getSavedMessage());
    }

    @Test
    public void find_execute_displaysOnlyMatchingTasks() {
        new FindCommand(new String[]{"find", "read"}).execute(tasks, ui, storage);

        assertEquals("1. [T] [ ] read textbook", ui.getSavedMessage());
    }

    @Test
    public void find_noMatch_displaysEmptyTaskList() {
        new FindCommand(new String[]{"find", "exercise"}).execute(tasks, ui, storage);

        assertEquals("No tasks match your search :(", ui.getSavedMessage());
    }

    @Test
    public void exit_execute_setsMessageAndRequestsExit() {
        ExitCommand command = new ExitCommand();

        command.execute(tasks, ui, storage);

        assertTrue(command.shouldExit());
        assertEquals("Bye. Hope to see you soon :(", ui.getSavedMessage());
    }

    @Test
    public void list_shouldExit_returnsFalse() {
        assertFalse(new ListCommand().shouldExit());
    }
}
