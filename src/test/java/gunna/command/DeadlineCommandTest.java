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
import gunna.task.Deadline;

/**
 * JUnit test class for testing the DeadlineCommand class.
 * Tests cover successful deadline creation and validation of descriptions and dates.
 */
public class DeadlineCommandTest {
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
    public void execute_validDescriptionAndDate_addsDeadlineTask() throws DukeException {
        DeadlineCommand command = new DeadlineCommand("Submit report", "2024-06-15");

        assertEquals(0, tasks.size(), "Task list should be empty initially");
        command.execute(tasks, ui, storage);
        assertEquals(1, tasks.size(), "Task list should have 1 task");
        assertTrue(tasks.get(0) instanceof Deadline, "Task should be a Deadline");
        assertEquals("Submit report", tasks.get(0).getDescription());
    }

    @Test
    public void execute_emptyDescription_throwsException() {
        DeadlineCommand command = new DeadlineCommand("", "2024-06-15");

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("Task details"),
                "Exception should indicate that task details are required");
    }

    @Test
    public void execute_emptyBy_throwsException() {
        DeadlineCommand command = new DeadlineCommand("Submit report", "");

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("Deadline date is required"),
                "Exception should indicate that a deadline date is required");
    }

    @Test
    public void execute_invalidDateFormat_throwsException() {
        DeadlineCommand command = new DeadlineCommand("Submit report", "invalid-date");

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("Date format invalid"),
                "Exception should indicate invalid date format");
    }

    @Test
    public void execute_dateWithWrongSeparator_throwsException() {
        DeadlineCommand command = new DeadlineCommand("Submit report", "2024/06/15");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception for date with wrong separator");
    }

    @Test
    public void execute_dateWithoutYear_throwsException() {
        DeadlineCommand command = new DeadlineCommand("Submit report", "06-15");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception for incomplete date");
    }

    @Test
    public void execute_multipleDeadlines_addsAllTasks() throws DukeException {
        DeadlineCommand command1 = new DeadlineCommand("Task 1", "2024-06-15");
        DeadlineCommand command2 = new DeadlineCommand("Task 2", "2024-07-20");

        command1.execute(tasks, ui, storage);
        command2.execute(tasks, ui, storage);

        assertEquals(2, tasks.size(), "Should have 2 tasks");
        assertTrue(tasks.get(0) instanceof Deadline);
        assertTrue(tasks.get(1) instanceof Deadline);
    }

    @Test
    public void execute_validLeapYearDate_addsDeadlineTask() throws DukeException {
        DeadlineCommand command = new DeadlineCommand("Task", "2024-02-29");

        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size(), "Should accept valid leap year date");
    }

    @Test
    public void execute_invalidMonth_throwsException() {
        DeadlineCommand command = new DeadlineCommand("Task", "2024-13-01");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should reject invalid month");
    }

    @Test
    public void execute_invalidDay_throwsException() {
        DeadlineCommand command = new DeadlineCommand("Task", "2024-06-32");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should reject invalid day");
    }
}
