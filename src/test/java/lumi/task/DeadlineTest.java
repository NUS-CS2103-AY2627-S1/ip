package lumi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline rescheduling. */
public class DeadlineTest {
    @Test
    public void reschedule_completedDeadline_dueDateUpdatedAndStatusPreserved() {
        Deadline deadline = new Deadline("submit report",
                LocalDateTime.of(2019, 10, 15, 0, 0));
        deadline.markAsDone();

        deadline.reschedule(LocalDateTime.of(2019, 10, 20, 18, 0));

        assertEquals(LocalDateTime.of(2019, 10, 20, 18, 0), deadline.getBy());
        assertTrue(deadline.isDone());
    }
}
