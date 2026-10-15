package vector.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import vector.VectorException;
import vector.storage.Storage;
import vector.task.Deadline;
import vector.task.Event;
import vector.task.TaskList;
import vector.task.Todo;
import vector.ui.Ui;

public class ScheduleCommandTest {
    private static final String TEMP_FILE = "temp_schedule_test.txt";
    private Storage storage;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        File file = new File(TEMP_FILE);
        if (file.exists()) {
            file.delete();
        }
        storage = new Storage(TEMP_FILE);
        ui = new Ui();
    }

    @AfterEach
    public void tearDown() {
        File file = new File(TEMP_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void execute_matchingDateTasks_returnsMatchingListString() {
        TaskList tasks = new TaskList();
        try {
            tasks.add(new Todo("read book"));
            tasks.add(new Deadline("return book", "2023-08-01 2359"));
            tasks.add(new Event("project meeting", "2023-08-01 1400", "2023-08-01 1600"));
            tasks.add(new Deadline("submit assignment", "2023-08-02 2359"));

            ScheduleCommand command = new ScheduleCommand(LocalDate.of(2023, 8, 1));
            String result = command.execute(tasks, ui, storage);

            assertTrue(result.contains("Scanning schedule for 2023-08-01. Results:"));
            assertTrue(result.contains("1.[D][ ] return book (by: Aug 1 2023, 11:59 PM)"));
            assertTrue(result.contains("2.[E][ ] project meeting (from: Aug 1 2023, 2:00 PM to: Aug 1 2023, 4:00 PM)"));
            assertFalse(result.contains("submit assignment"));
            assertFalse(result.contains("read book"));
        } catch (VectorException e) {
            fail("Exception thrown on valid schedule command: " + e.getMessage());
        }
    }

    @Test
    public void execute_noMatchingTasks_returnsEmptyScheduleMessage() {
        TaskList tasks = new TaskList();
        try {
            tasks.add(new Deadline("return book", "2023-08-01 2359"));
            ScheduleCommand command = new ScheduleCommand(LocalDate.of(2023, 8, 2));
            String result = command.execute(tasks, ui, storage);
            assertEquals("(No tasks found)", result);
        } catch (VectorException e) {
            fail("Exception thrown on valid schedule command: " + e.getMessage());
        }
    }

    private void assertFalse(boolean condition) {
        org.junit.jupiter.api.Assertions.assertFalse(condition);
    }
}
