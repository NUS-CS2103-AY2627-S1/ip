package oz.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests assertions that protect the core task model's internal invariants.
 */
public class TaskInvariantTest {

    @Test
    public void taskConstructor_invalidDescription_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new ToDo(null));
        assertThrows(AssertionError.class, () -> new ToDo("   "));
    }

    @Test
    public void deadlineConstructor_nullDeadline_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Deadline("submit report", null));
    }

    @Test
    public void eventConstructor_nullDateTime_assertionErrorThrown() {
        TaskDateTime dateTime = TaskDateTime.fromDateTime(LocalDateTime.of(2026, 9, 10, 18, 0));

        assertThrows(AssertionError.class, () -> new Event("meeting", null, dateTime));
        assertThrows(AssertionError.class, () -> new Event("meeting", dateTime, null));
    }

    @Test
    public void taskDateTimeFactories_nullInput_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> TaskDateTime.fromDate(null));
        assertThrows(AssertionError.class, () -> TaskDateTime.fromDateTime(null));
    }

    @Test
    public void taskListConstructor_nullElement_assertionErrorThrown() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(tasks));
    }

    @Test
    public void add_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
    }
}
