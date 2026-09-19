package nudge.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests assertions that protect task invariants from programming mistakes.
 */
class TaskAssertionTest {
    @Test
    void task_nullDescription_assertionFails() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    void task_blankDescription_assertionFails() {
        assertThrows(AssertionError.class, () -> new Task("   "));
    }

    @Test
    void deadline_nullDueDate_assertionFails() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    void event_nullStartDate_assertionFails() {
        LocalDate endDate = LocalDate.of(2026, 9, 12);

        assertThrows(AssertionError.class, () -> new Event("project meeting", null, endDate));
    }

    @Test
    void event_nullEndDate_assertionFails() {
        LocalDate startDate = LocalDate.of(2026, 9, 11);

        assertThrows(AssertionError.class, () -> new Event("project meeting", startDate, null));
    }
}
