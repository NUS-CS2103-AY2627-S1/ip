package auto.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests deadline date matching, rendering, and durable representation. */
class DeadlineTest {
    private static final LocalDate DUE_DATE = LocalDate.of(2026, 8, 22);

    @Test
    void occursOn_beforeExactAndAfterDueDate_matchesOnlyExactDate() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);

        assertFalse(deadline.occursOn(DUE_DATE.minusDays(1)));
        assertTrue(deadline.occursOn(DUE_DATE));
        assertFalse(deadline.occursOn(DUE_DATE.plusDays(1)));
    }

    @Test
    void toString_incompleteAndCompletedDeadline_displaysTypeStatusAndDate() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);
        assertEquals("[D][ ] submit report (by: Aug 22 2026)", deadline.toString());

        deadline.mark();
        assertEquals("[D][X] submit report (by: Aug 22 2026)", deadline.toString());
    }

    @Test
    void toDataString_completedDeadline_encodesNameDateAndStatus() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);
        deadline.mark();

        assertEquals("D | 1 | c3VibWl0IHJlcG9ydA== | QXVnIDIyIDIwMjY=",
                deadline.toDataString());
    }
}
