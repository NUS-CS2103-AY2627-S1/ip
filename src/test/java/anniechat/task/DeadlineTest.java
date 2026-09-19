package anniechat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the behaviour of {@link Deadline}. */
public class DeadlineTest {
    @Test
    public void constructor_validDate_formatsDeadlineForDisplay() {
        Deadline deadline = new Deadline("deadline return book /by 2019-10-15");

        assertEquals("return book (by: Oct 15 2019)", deadline.getTaskDesc());
        assertEquals("D", deadline.getTaskIcon());
    }
}
