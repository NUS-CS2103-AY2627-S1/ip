package adrian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and storage representations of {@link FixedDurationTask} objects.
 */
public class FixedDurationTaskTest {

    /**
     * Verifies the display representation of an incomplete fixed-duration task.
     */
    @Test
    public void toString_unmarkedTask_correctString() {
        FixedDurationTask task = new FixedDurationTask("read sales report", 120);

        assertEquals(
                "[F][ ] read sales report (duration: 120 minutes)",
                task.toString());
    }

    /**
     * Verifies the display representation of a completed fixed-duration task.
     */
    @Test
    public void toString_markedTask_correctString() {
        FixedDurationTask task = new FixedDurationTask("read sales report", 120);
        task.markAsDone();

        assertEquals(
                "[F][X] read sales report (duration: 120 minutes)",
                task.toString());
    }

    /**
     * Verifies the storage representation of an incomplete fixed-duration task.
     */
    @Test
    public void toDataString_unmarkedTask_correctString() {
        FixedDurationTask task = new FixedDurationTask("read sales report", 120);

        assertEquals(
                "F | 0 | read sales report | 120",
                task.toDataString());
    }

    /**
     * Verifies that a fixed-duration task requires a positive duration.
     */
    @Test
    public void constructor_nonPositiveDuration_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedDurationTask("read sales report", 0));
        assertThrows(
                IllegalArgumentException.class,
                () -> new FixedDurationTask("read sales report", -1));
    }
}
