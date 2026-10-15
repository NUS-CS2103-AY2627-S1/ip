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
 * JUnit test class for testing the DeleteCommand class.
 * Tests cover successful deletion and error handling for invalid indices.
 */
public class DeleteCommandTest {
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
    public void execute_validIndex_deletesTask() throws DukeException {
        tasks.add(new Todo("Buy milk"));
        tasks.add(new Todo("Read book"));
        DeleteCommand command = new DeleteCommand(0);

        assertEquals(2, tasks.size(), "Should have 2 tasks before deletion");
        command.execute(tasks, ui, storage);
        assertEquals(1, tasks.size(), "Should have 1 task after deletion");
        assertEquals("Read book", tasks.get(0).getDescription(),
                "Remaining task should be 'Read book'");
    }

    @Test
    public void execute_negativeIndex_throwsException() {
        tasks.add(new Todo("Buy milk"));
        DeleteCommand command = new DeleteCommand(-1);

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("not in the queue"),
                "Exception should indicate that the task is unavailable");
    }

    @Test
    public void execute_indexTooLarge_throwsException() {
        tasks.add(new Todo("Buy milk"));
        DeleteCommand command = new DeleteCommand(5);

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("not in the queue"));
    }

    @Test
    public void execute_emptyTaskList_throwsException() {
        DeleteCommand command = new DeleteCommand(0);

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception when task list is empty");
    }

    @Test
    public void execute_middleTaskInList_deletesCorrectTask() throws DukeException {
        tasks.add(new Todo("Task 1"));
        tasks.add(new Todo("Task 2"));
        tasks.add(new Todo("Task 3"));
        DeleteCommand command = new DeleteCommand(1);

        command.execute(tasks, ui, storage);

        assertEquals(2, tasks.size(), "Should have 2 tasks remaining");
        assertEquals("Task 1", tasks.get(0).getDescription());
        assertEquals("Task 3", tasks.get(1).getDescription());
    }

    @Test
    public void execute_lastTask_leavesListWithRemainingTasks() throws DukeException {
        tasks.add(new Todo("Task 1"));
        tasks.add(new Todo("Task 2"));
        DeleteCommand command = new DeleteCommand(1);

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getDescription());
    }
}
