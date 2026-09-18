package blud.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the formatting of deadline tasks. */
public class DeadlineTest {
    /** Verifies that a deadline task has the expected display format. */
    @Test
    public void testFormat() {
        Deadline newDeadline = new Deadline(new String[] {"Deadline test1", "by 2/12/2019 1800"});
        assertEquals("[D][ ] test1 (by: Dec 02 2019, 6:00 pm)", newDeadline.toString());
    }
}
