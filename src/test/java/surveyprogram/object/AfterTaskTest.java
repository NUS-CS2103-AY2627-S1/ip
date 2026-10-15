package surveyprogram.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests both availability conditions supported by after-tasks. */
class AfterTaskTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 10, 12, 0);

    @Test
    void timeDependentTask_beforeAndAtReleaseTime_changesAvailability() {
        AfterTask task = new AfterTask("submit survey", NOW);

        assertFalse(task.isAvailable(NOW.minusNanos(1)));
        assertTrue(task.isAvailable(NOW));
        assertTrue(task.isAvailable(NOW.plusDays(1)));
        assertFalse(task.hasParent());
        assertNull(task.getParent());
        assertSame(NOW, task.getReleaseAt());
    }

    @Test
    void parentDependentTask_parentCompletion_changesAvailability() {
        Item parent = new Todo("finish report");
        AfterTask task = new AfterTask("submit report", parent);

        assertFalse(task.isAvailable(NOW));
        parent.setDone(true);

        assertTrue(task.isAvailable(NOW));
        assertTrue(task.hasParent());
        assertSame(parent, task.getParent());
        assertNull(task.getReleaseAt());
    }

    @Test
    void toString_lockedAndAvailableTasks_includesConditionAndLockState() {
        Item parent = new Todo("finish report");
        AfterTask parentDependentTask = new AfterTask("submit report", parent);
        LocalDateTime pastRelease = LocalDateTime.now().minusDays(1);
        AfterTask releasedTask = new AfterTask("check results", pastRelease);
        String formattedRelease = pastRelease.format(
                DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH));

        assertEquals("[A][L][ ] submit report (after task: finish report)",
                parentDependentTask.toString());
        assertEquals("[A][ ] check results (after: " + formattedRelease + ")", releasedTask.toString());
    }
}
