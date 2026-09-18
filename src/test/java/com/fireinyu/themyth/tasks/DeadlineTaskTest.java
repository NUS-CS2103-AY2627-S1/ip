package com.fireinyu.themyth.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.util.MythDateTime;

/**
 * Unit tests for {@link DeadlineTask}.
 */
public class DeadlineTaskTest {

    /**
     * Tests that a {@link DeadlineTask} is initialized with correct description, type code, and completion state.
     */
    @Test
    public void constructor_initializesCorrectly() {
        MythDateTime due = MythDateTime.parse("2025-12-31-23-59-59");
        DeadlineTask task = new DeadlineTask("Submit report", due);

        assertEquals("Submit report", task.getDescription());
        assertEquals("D", task.getTypeCode());
        assertFalse(task.isCompleted());
    }

    /**
     * Tests the {@link DeadlineTask#isDueBy(MythDateTime)} method for dates before and after the deadline.
     */
    @Test
    public void isDueBy_checksDueDateCorrectly() {
        MythDateTime due = MythDateTime.parse("2025-10-15-18-00-00");
        DeadlineTask task = new DeadlineTask("Submit report", due);

        MythDateTime later = MythDateTime.parse("2025-10-16-00-00-00");
        MythDateTime earlier = MythDateTime.parse("2025-10-14-00-00-00");

        assertTrue(task.isDueBy(later));
        assertFalse(task.isDueBy(earlier));
    }

    /**
     * Tests serialization of a {@link DeadlineTask} via {@link DeadlineTask#extract()}.
     */
    @Test
    public void extract_serializesAttributes() {
        MythDateTime due = MythDateTime.parse("2025-10-15-18-00-00");
        DeadlineTask task = new DeadlineTask("Submit report", due);

        List<String> extracted = task.extract();
        assertEquals("D", extracted.get(0));
        assertEquals("false", extracted.get(1));
        assertEquals("Submit report", extracted.get(4));
        assertEquals(due.dump(), extracted.get(5));
    }

    /**
     * Tests formatting of {@link DeadlineTask#toString()}.
     */
    @Test
    public void toString_formatsCorrectly() {
        MythDateTime due = MythDateTime.parse("2025-10-15-18-00-00");
        DeadlineTask task = new DeadlineTask("Submit report", due);

        String expected = String.format("[D][ ] Submit report (by: %s)", due);
        assertEquals(expected, task.toString());

        task.mark();
        String expectedDone = String.format("[D][X] Submit report (by: %s)", due);
        assertEquals(expectedDone, task.toString());
    }

    /**
     * Tests marking and unmarking a {@link DeadlineTask}.
     */
    @Test
    public void markAndUnmark_updatesStatus() {
        MythDateTime due = MythDateTime.parse("2025-10-15-18-00-00");
        DeadlineTask task = new DeadlineTask("Submit report", due);

        assertFalse(task.isCompleted());
        task.mark();
        assertTrue(task.isCompleted());
        task.unmark();
        assertFalse(task.isCompleted());
    }
}
