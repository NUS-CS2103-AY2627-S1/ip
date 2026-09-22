package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests deadline occurrence checking and file format serialization of {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void occursOn_matchingAndNonMatchingDate_correctBooleanReturned() throws OzException {
        TaskDateTime deadline = TaskDateTime.parse("2026-09-01 2359");
        Deadline task = new Deadline("submit project", deadline);

        assertTrue(task.occursOn(LocalDate.of(2026, 9, 1)));
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    public void toFileFormat_unmarkedAndMarked_correctFormat() throws OzException {
        TaskDateTime deadline = TaskDateTime.parse("2026-09-01 2359");
        Deadline task = new Deadline("submit project", deadline);

        assertEquals("D | 0 | submit project | 2026-09-01 2359", task.toFileFormat());

        task.markAsDone();
        assertEquals("D | 1 | submit project | 2026-09-01 2359", task.toFileFormat());
    }

    @Test
    public void toString_unmarkedAndMarked_correctStringRepresentation() throws OzException {
        TaskDateTime deadline = TaskDateTime.parse("2026-09-01 2359");
        Deadline task = new Deadline("submit project", deadline);

        assertEquals("[D][ ] submit project (by: Sep 01 2026, 11:59pm)", task.toString());

        task.markAsDone();
        assertEquals("[D][X] submit project (by: Sep 01 2026, 11:59pm)", task.toString());
    }
}
