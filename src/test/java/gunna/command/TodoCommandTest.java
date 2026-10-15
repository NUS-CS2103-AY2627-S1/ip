package gunna.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Todo;

/**
 * JUnit test class for testing the TodoCommand class.
 * Tests cover successful todo creation and validation of empty descriptions.
 */
public class TodoCommandTest {
    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage("data/test_command_tasks.txt");
    }

    @Test
    public void execute_validDescription_addsTodoTask() throws DukeException {
        TodoCommand command = new TodoCommand("Buy milk");

        assertEquals(0, tasks.size(), "Task list should be empty initially");
        command.execute(tasks, ui, storage);
        assertEquals(1, tasks.size(), "Task list should have 1 task");
        assertTrue(tasks.get(0) instanceof Todo, "Task should be a Todo");
        assertEquals("Buy milk", tasks.get(0).getDescription());
    }

    @Test
    public void execute_emptyDescription_throwsException() {
        TodoCommand command = new TodoCommand("");

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("Task details are required"),
                "Exception should indicate that task details are required");
    }

    @Test
    public void execute_descriptionWithSpaces_addsTodoTask() throws DukeException {
        TodoCommand command = new TodoCommand("Buy milk and eggs from store");

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("Buy milk and eggs from store", tasks.get(0).getDescription());
    }

    @Test
    public void execute_multipleTodos_addsAllTasks() throws DukeException {
        TodoCommand command1 = new TodoCommand("Task 1");
        TodoCommand command2 = new TodoCommand("Task 2");
        TodoCommand command3 = new TodoCommand("Task 3");

        command1.execute(tasks, ui, storage);
        command2.execute(tasks, ui, storage);
        command3.execute(tasks, ui, storage);

        assertEquals(3, tasks.size(), "Should have 3 tasks");
        assertEquals("Task 1", tasks.get(0).getDescription());
        assertEquals("Task 2", tasks.get(1).getDescription());
        assertEquals("Task 3", tasks.get(2).getDescription());
    }

    @Test
    public void execute_descriptionWithSpecialCharacters_addsTodoTask() throws DukeException {
        TodoCommand command = new TodoCommand("Buy milk | eggs & bread");

        command.execute(tasks, ui, storage);

        assertEquals("Buy milk | eggs & bread", tasks.get(0).getDescription(),
                "Description with special characters should be preserved");
    }
}
