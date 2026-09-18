package meka.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the persisted and displayed representations of {@link Deadline} tasks.
 */
public class DeadlineTest {

    @Test
    public void toDataString_newDeadline_returnsIncompleteDeadlineData() {
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 3, 9, 5));

        assertEquals("D | 0 | submit report | 2026-09-03T09:05",
                deadline.toDataString());
    }

    @Test
    public void toDataString_markedDeadline_returnsCompletedDeadlineData() {
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 3, 9, 5));
        deadline.markAsDone();

        assertEquals("D | 1 | submit report | 2026-09-03T09:05",
                deadline.toDataString());
    }

    @Test
    public void toString_deadlineAtMidnight_returnsTwelveAmDisplayTime() {
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 3, 0, 0));

        assertEquals("[D][ ] submit report (by: Sep 03 2026, 12:00 AM)",
                deadline.toString());
    }

    @Test
    public void toString_markedDeadlineAtNoon_returnsDoneIconAndTwelvePmDisplayTime() {
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 3, 12, 0));
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Sep 03 2026, 12:00 PM)",
                deadline.toString());
    }
}
