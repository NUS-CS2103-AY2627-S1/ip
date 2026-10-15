package serina.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the identity rules used to reject duplicate tasks. */
public class TaskIdentityTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 14);

    @Test
    public void hasSameIdentity_normalizedDescriptionAndStatus_ignoresCosmeticDifferences() {
        Todo first = new Todo(" Inspect   ENGINES ", TaskStatus.DONE);
        Todo second = new Todo("inspect engines", TaskStatus.NOT_DONE);

        assertTrue(first.hasSameIdentity(second));
        assertTrue(second.hasSameIdentity(first));
        assertFalse(first.hasSameIdentity(null));
    }

    @Test
    public void hasSameIdentity_differentTypeDescriptionOrDate_returnsFalse() {
        Deadline deadline = new Deadline("report", DATE);

        assertFalse(deadline.hasSameIdentity(new Todo("report")));
        assertFalse(deadline.hasSameIdentity(new Deadline("different", DATE)));
        assertFalse(deadline.hasSameIdentity(new Deadline("report", DATE.plusDays(1))));
    }
}
