package command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import exception.CbtException;
import task.Deadline;
import task.TaskList;
import task.Todo;

/** Tests chronological task sorting and sort-criterion validation. */
public class SortCommandTest {
    /** Verifies that the date criterion reorders and displays tasks. */
    @Test
    public void execute_dateCriterion_tasksSortedAndChangedResultReturned() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("undated"));
        tasks.addTask(new Deadline("later", LocalDateTime.of(2026, 8, 28, 12, 0)));
        tasks.addTask(new Deadline("earlier", LocalDateTime.of(2026, 8, 27, 12, 0)));

        CommandResult result = new SortCommand("DATE").execute(tasks);

        String expected = "Flight plan aligned by date:" + System.lineSeparator()
                + "1.[D][ ] earlier (by: Aug 27 2026, 12:00pm)" + System.lineSeparator()
                + "2.[D][ ] later (by: Aug 28 2026, 12:00pm)" + System.lineSeparator()
                + "3.[T][ ] undated";
        assertEquals(expected, result.message());
        assertTrue(result.isTaskListChanged());
        assertFalse(result.isExit());
    }

    /** Verifies that sorting an empty list succeeds with a heading only. */
    @Test
    public void execute_emptyList_headingReturned() throws CbtException {
        CommandResult result = new SortCommand("date").execute(new TaskList());

        assertEquals("Flight plan aligned by date:", result.message());
        assertTrue(result.isTaskListChanged());
    }

    /** Verifies that missing, unsupported, and extra criteria are rejected. */
    @Test
    public void execute_invalidCriteria_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(CbtException.class, () -> new SortCommand("").execute(tasks));
        assertThrows(CbtException.class, () -> new SortCommand("name").execute(tasks));
        assertThrows(CbtException.class, () -> new SortCommand("date asc").execute(tasks));
    }
}
