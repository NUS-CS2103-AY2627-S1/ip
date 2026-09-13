package nelson.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nelson.exception.NelsonException;

/** Tests deadline validation, status changes, and serialization. */
public class DeadlineTest {
    @Test
    public void newDeadline_formatsDisplayAndStorageValues() throws NelsonException {
        Deadline deadline = new Deadline("submit iP", "2026-09-18");

        assertEquals("[D][ ] submit iP (by: Sep 18 2026)", deadline.toString());
        assertEquals("D | 0 | submit iP | 2026-09-18", deadline.toStorageString());
    }

    @Test
    public void markAsDone_completedDeadline_updatesBothFormats() throws NelsonException {
        Deadline deadline = new Deadline("submit iP", "2026-09-18");

        deadline.markAsDone();

        assertEquals("[D][X] submit iP (by: Sep 18 2026)", deadline.toString());
        assertEquals("D | 1 | submit iP | 2026-09-18", deadline.toStorageString());
    }

    @Test
    public void constructor_nonexistentDate_throwsNelsonException() {
        assertThrows(NelsonException.class, () -> new Deadline("submit iP", "2026-02-30"));
    }
}
