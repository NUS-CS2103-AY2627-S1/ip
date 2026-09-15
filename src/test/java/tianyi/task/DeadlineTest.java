package tianyi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline date inclusion, persistence, and display formatting.
 */
public class DeadlineTest {
    @Test
    public void isOccurringOn_beforeOnAndAfterDeadline_returnsExpectedResults() {
        Deadline deadline = new Deadline("submit report", new TaskTime("2-12-2019 18:00"));

        assertTrue(deadline.isOccurringOn(new TaskTime("1-12-2019")));
        assertTrue(deadline.isOccurringOn(new TaskTime("2-12-2019")));
        assertFalse(deadline.isOccurringOn(new TaskTime("3-12-2019")));
    }

    @Test
    public void formatting_incompleteDeadline_includesDeadlineDetails() {
        Deadline deadline = new Deadline("submit report", new TaskTime("2-12-2019 18:00"));

        assertEquals("D | 0 | submit report | 2-12-2019 18:00", deadline.getData());
        assertEquals("[D][ ] submit report (by: Mon, Dec 02 2019, 6:00 PM)",
                deadline.toString());
    }

    @Test
    public void formatting_completedDeadline_includesCompletedStatus() {
        Deadline deadline = new Deadline("submit report", new TaskTime("2-12-2019"));
        deadline.markAsDone();

        assertEquals("D | 1 | submit report | 2-12-2019", deadline.getData());
        assertEquals("[D][X] submit report (by: Mon, Dec 02 2019)", deadline.toString());
    }
}
