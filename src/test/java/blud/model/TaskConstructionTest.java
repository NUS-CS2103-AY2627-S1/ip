package blud.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import blud.exception.DeadlineException;
import blud.exception.EventException;
import blud.exception.ToDoException;

/** Tests validation and storage representations of task subtypes. */
public class TaskConstructionTest {
    /** Verifies that todo validation rejects missing descriptions. */
    @Test
    public void createTodo_missingDescription_throwsTodoException() {
        assertThrows(ToDoException.class, () -> new ToDo(new String[] {"todo"}));
        assertThrows(ToDoException.class, () -> new ToDo(null));
    }

    /** Verifies that deadline validation rejects missing deadline fields. */
    @Test
    public void createDeadline_missingField_throwsDeadlineException() {
        assertThrows(DeadlineException.class,
                () -> new Deadline(new String[] {"deadline report"}));
        assertThrows(DeadlineException.class,
                () -> new Deadline(new String[] {"deadline report", "by "}));
    }

    /** Verifies that event validation rejects missing or reverse-ordered dates. */
    @Test
    public void createEvent_invalidDates_throwsEventException() {
        assertThrows(EventException.class,
                () -> new Event(new String[] {"event meeting", "from 1/6/2026 0900", "to 1/6/2026 0900"}));
        assertThrows(EventException.class,
                () -> new Event(new String[] {"event meeting", "from 1/6/2026 0900"}));
    }

    /** Verifies stable storage representations for each task subtype. */
    @Test
    public void toStorageString_validTasks_returnsExpectedFormat() {
        assertEquals("T | 0 | read", new ToDo(new String[] {"todo read"}).toStorageString());
        assertEquals("D | 0 | report | 1/6/2026 0900",
                new Deadline(new String[] {"deadline report", "by 1/6/2026 0900"}).toStorageString());
        assertEquals("E | 0 | meeting | 1/6/2026 0900 | 1/6/2026 1000",
                new Event(new String[] {"event meeting", "from 1/6/2026 0900", "to 1/6/2026 1000"}).
                        toStorageString());
    }
}
