package gunna.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Deadline;
import gunna.task.Todo;

/**
 * JUnit test class for testing the FindCommand class.
 * Tests cover date searching functionality and validation of date formats.
 */
public class FindCommandTest {
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
    public void execute_validDate_executesSuccessfully() throws DukeException {
        tasks.add(new Deadline("Task 1", LocalDate.of(2024, 6, 15)));
        tasks.add(new Todo("Task 2"));
        FindCommand command = new FindCommand("2024-06-15");

        // Should not throw exception
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_emptyDateString_throwsException() {
        FindCommand command = new FindCommand("");

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("Date required"),
                "Exception should indicate that a date is required");
    }

    @Test
    public void execute_invalidDateFormat_throwsException() {
        tasks.add(new Todo("Task 1"));
        FindCommand command = new FindCommand("invalid-date");

        DukeException exception = assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        });
        assertTrue(exception.getMessage().contains("Date format invalid"),
                "Exception should indicate invalid date format");
    }

    @Test
    public void execute_dateWithWrongSeparator_throwsException() {
        FindCommand command = new FindCommand("2024/06/15");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception for date with wrong separator");
    }

    @Test
    public void execute_incompleteDate_throwsException() {
        FindCommand command = new FindCommand("2024-06");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception for incomplete date");
    }

    @Test
    public void execute_invalidMonth_throwsException() {
        FindCommand command = new FindCommand("2024-13-01");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception for invalid month");
    }

    @Test
    public void execute_invalidDay_throwsException() {
        FindCommand command = new FindCommand("2024-06-32");

        assertThrows(DukeException.class, () -> {
            command.execute(tasks, ui, storage);
        }, "Should throw exception for invalid day");
    }

    @Test
    public void execute_emptyTaskList_executesSuccessfully() throws DukeException {
        FindCommand command = new FindCommand("2024-06-15");

        // Should not throw exception even with empty task list
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_dateWithLeadingZeros_executesSuccessfully() throws DukeException {
        tasks.add(new Deadline("Task", LocalDate.of(2024, 6, 5)));
        FindCommand command = new FindCommand("2024-06-05");

        // Should not throw exception
        command.execute(tasks, ui, storage);
    }

    @Test
    public void execute_validLeapYearDate_executesSuccessfully() throws DukeException {
        FindCommand command = new FindCommand("2024-02-29");

        // Should not throw exception for valid leap year date
        command.execute(tasks, ui, storage);
    }
}
