package ari.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Checks deadline representation invariants and valid display/storage behavior.
 */
public class DeadlineTaskTest {
    @Test
    public void assertions_testRuntime_enabled() {
        assertTrue(DeadlineTask.class.desiredAssertionStatus());
    }

    @Test
    public void getDeadline_missingRepresentation_throwsAssertionError() {
        DeadlineTask task = new DeadlineTask("read", (String) null);
        assertThrows(AssertionError.class, task::getDeadline);
    }

    @Test
    public void toDataString_missingRepresentation_throwsAssertionError() {
        DeadlineTask task = new DeadlineTask("read", (LocalDate) null);
        assertThrows(AssertionError.class, task::toDataString);
    }

    @Test
    public void deadline_validRepresentations_preservesDisplayAndStorage() {
        DeadlineTask dated = new DeadlineTask("read", LocalDate.of(2026, 9, 15));
        DeadlineTask text = new DeadlineTask("read", "Sunday");
        assertEquals("Sep 15 2026", dated.getDeadline());
        assertEquals("D | 0 | read | 2026-09-15", dated.toDataString());
        assertEquals("Sunday", text.getDeadline());
        assertEquals("D | 0 | read | Sunday", text.toDataString());
    }
}
