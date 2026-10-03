package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests the observable behavior of deadline tasks.
 */
class DeadlineTest {
    private static final LocalDate DEADLINE_DATE =
            LocalDate.of(2026, 8, 10);

    @Test
    void getBy_deadline_returnsDueDate() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);

        assertEquals(DEADLINE_DATE, deadline.getBy());
    }

    @Test
    void getStatus_incompleteDeadline_includesDateAndDescription() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);

        assertEquals(
                "[D][ ] submit report (by: Aug 10 2026)",
                deadline.getStatus()
        );
    }

    @Test
    void getStatus_completedDeadline_showsCompletedStatus() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);
        deadline.markAsDone();

        assertEquals(
                "[D][X] submit report (by: Aug 10 2026)",
                deadline.getStatus()
        );
    }

    @Test
    void serialize_incompleteDeadline_returnsStorageFormat() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);

        assertEquals(
                "D\t0\tsubmit report\t2026-08-10\t\t",
                deadline.serialize()
        );
    }

    @Test
    void serialize_completedDeadline_returnsCompletedStorageFormat() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);
        deadline.markAsDone();

        assertEquals(
                "D\t1\tsubmit report\t2026-08-10\t\t",
                deadline.serialize()
        );
    }

    @Test
    void occursOn_deadlineDate_returnsTrue() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);

        assertTrue(deadline.occursOn(DEADLINE_DATE));
    }

    @Test
    void occursOn_differentDate_returnsFalse() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);

        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 11)));
    }

    @Test
    void constructor_nullDate_throwsAssertionError() {
        assertThrows(
                AssertionError.class,
                () -> new Deadline("submit report", null));
    }

    @Test
    void getStatus_nonEnglishDefaultLocale_keepsEnglishDateFormat() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINA);

            assertEquals(
                    "[D][ ] submit report (by: Aug 10 2026)",
                    new Deadline("submit report", DEADLINE_DATE).getStatus());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
