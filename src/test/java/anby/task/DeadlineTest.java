package anby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_validDate_returnsFormattedDate() {
        Deadline deadline = new Deadline("eat burger", "2026-09-18");

        assertEquals("[D][ ] eat burger (by: Sep 18 2026)", deadline.toString());
    }

    @Test
    public void toString_doneDeadline_returnsDoneDisplayString() {
        Deadline deadline = new Deadline("eat burger", "2026-09-18");

        deadline.markAsDone();

        assertEquals("[D][X] eat burger (by: Sep 18 2026)", deadline.toString());
    }

    @Test
    public void toFileString_undoneDeadline_returnsDeadlineStorageString() {
        Deadline deadline = new Deadline("eat burger", "2026-09-18");

        assertEquals("D | 0 | eat burger | 2026-09-18", deadline.toFileString());
    }

    @Test
    public void toFileString_doneDeadline_returnsDoneDeadlineStorageString() {
        Deadline deadline = new Deadline("eat burger", "2026-09-18");

        deadline.markAsDone();

        assertEquals("D | 1 | eat burger | 2026-09-18", deadline.toFileString());
    }

    @Test
    public void constructor_invalidDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("eat burger", "18-09-2026"));
    }
}
