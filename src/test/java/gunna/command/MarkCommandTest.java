package gunna.command;

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
 * JUnit test class for testing the MarkCommand class.
 * Tests cover successful marking and error handling for invalid indices.
 */
public class MarkCommandTest {
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
    public void execute_validIndex_marksTaskAsDone() throws DukeException {
        tasks.add(new Todo("Buy milk"));
        MarkCommand command = new MarkCommand(0);

        command.execute(tasks, ui, storage);

        assertTrue(tasks.get(0).isDone(), "Task should be marked as done");
    }

    @Test
    public void execute_negativeIndex_throwsException() {
        tasks.add(new Todo("Buy milk"));
        MarkCommand command = new MarkCommand(-1);

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("not in the queue"),
                "Exception should indicate that the task is unavailable");
    }

    @Test
    public void execute_indexTooLarge_throwsException() {
        tasks.add(new Todo("Buy milk"));
        MarkCommand command = new MarkCommand(5);

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("not in the queue"));
    }

    @Test
    public void execute_emptyTaskList_throwsException() {
        MarkCommand command = new MarkCommand(0);

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception when task list is empty");
    }

    @Test
    public void execute_alreadyMarkedTask_remainsMarked() throws DukeException {
        tasks.add(new Todo("Buy milk"));
        tasks.mark(0);
        MarkCommand command = new MarkCommand(0);

        command.execute(tasks, ui, storage);

        assertTrue(tasks.get(0).isDone(), "Task should remain marked as done");
    }

    @Test
    public void execute_secondTaskInList_marksCorrectTask() throws DukeException {
        tasks.add(new Todo("Task 1"));
        tasks.add(new Todo("Task 2"));
        tasks.add(new Todo("Task 3"));
        MarkCommand command = new MarkCommand(1);

        command.execute(tasks, ui, storage);

        assertTrue(tasks.get(1).isDone(), "Second task should be marked");
        assertTrue(!tasks.get(0).isDone() && !tasks.get(2).isDone(),
                "Other tasks should not be marked");
    }
}
