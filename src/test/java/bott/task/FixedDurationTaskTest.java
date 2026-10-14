package bott.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link FixedDurationTask}'s two textual representations: {@code toString}
 * (display format "(for: Xh Ym)", with a zero component dropped) and
 * {@code toFileFormat} (save format, the raw minute count).
 */
public class FixedDurationTaskTest {

    @Test
    public void toString_wholeHours_showsHoursOnly() {
        FixedDurationTask task = new FixedDurationTask("read sales report", 120);
        assertEquals("[F][ ] read sales report (for: 2h)", task.toString());
    }

    @Test
    public void toString_underOneHour_showsMinutesOnly() {
        FixedDurationTask task = new FixedDurationTask("call bank", 45);
        assertEquals("[F][ ] call bank (for: 45m)", task.toString());
    }

    @Test
    public void toString_hoursAndMinutes_showsBoth() {
        FixedDurationTask task = new FixedDurationTask("deep work", 90);
        assertEquals("[F][ ] deep work (for: 1h 30m)", task.toString());
    }

    @Test
    public void toString_done_showsDoneIcon() {
        FixedDurationTask task = new FixedDurationTask("read sales report", 120);
        task.markAsDone();
        assertEquals("[F][X] read sales report (for: 2h)", task.toString());
    }

    @Test
    public void toFileFormat_notDone_correctFormat() {
        FixedDurationTask task = new FixedDurationTask("read sales report", 120);
        assertEquals("F | 0 | read sales report | 120", task.toFileFormat());
    }

    @Test
    public void toFileFormat_done_correctFormat() {
        FixedDurationTask task = new FixedDurationTask("read sales report", 90);
        task.markAsDone();
        assertEquals("F | 1 | read sales report | 90", task.toFileFormat());
    }
}
