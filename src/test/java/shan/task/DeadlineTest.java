package shan.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    private static final LocalDate DEADLINE_DATE = LocalDate.of(2019, 12, 5);

    @Test
    void occursBetween_deadlineOnRangeStart_returnsTrue() {
        Deadline deadline = createDeadline();

        boolean result = deadline.occursBetween(
                DEADLINE_DATE, DEADLINE_DATE.plusDays(2));

        assertTrue(result);
    }

    @Test
    void occursBetween_deadlineOnRangeEnd_returnsTrue() {
        Deadline deadline = createDeadline();

        boolean result = deadline.occursBetween(
                DEADLINE_DATE.minusDays(2), DEADLINE_DATE);

        assertTrue(result);
    }

    @Test
    void occursBetween_deadlineOutsideRange_returnsFalse() {
        Deadline deadline = createDeadline();

        assertFalse(deadline.occursBetween(
                DEADLINE_DATE.minusDays(2), DEADLINE_DATE.minusDays(1)));
        assertFalse(deadline.occursBetween(
                DEADLINE_DATE.plusDays(1), DEADLINE_DATE.plusDays(2)));
    }

    @Test
    void occursOn_matchingDate_returnsTrue() {
        Deadline deadline = createDeadline();

        assertTrue(deadline.occursOn(DEADLINE_DATE));
    }

    private Deadline createDeadline() {
        return new Deadline("return book", DEADLINE_DATE.atTime(18, 0));
    }
}
