package surveyprogram.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline formatting, accessors, and date matching. */
class DeadlineTest {
    private static final LocalDateTime DUE = LocalDateTime.of(2026, 8, 24, 17, 30);
    @Test
    void inRange_dueDate_matchesOnlyExactDate() {
        Deadline deadline = new Deadline("submit report", DUE);
        assertFalse(deadline.inRange(LocalDate.of(2026, 8, 23)));
        assertTrue(deadline.inRange(LocalDate.of(2026, 8, 24)));
        assertFalse(deadline.inRange(LocalDate.of(2026, 8, 25)));
    }

    @Test
    void toString_incompleteAndCompletedDeadline_formatsAllDetails() {
        Deadline deadline = new Deadline("submit report", DUE);
        assertEquals("[D][ ] submit report (by: Aug 24 2026, 5:30 PM)", deadline.toString());
        deadline.setDone(true);
        assertEquals("[D][X] submit report (by: Aug 24 2026, 5:30 PM)", deadline.toString());
    }

    @Test
    void getBy_returnsConstructionDateTime() {
        assertEquals(DUE, new Deadline("submit report", DUE).getBy());
    }
}
