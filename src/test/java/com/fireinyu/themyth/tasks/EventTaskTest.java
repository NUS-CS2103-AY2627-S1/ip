package com.fireinyu.themyth.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.util.MythDateTime;

/**
 * Unit tests for {@link EventTask}.
 */
public class EventTaskTest {

    /**
     * Tests that an {@link EventTask} is initialized with correct description, type code, and completion state.
     */
    @Test
    public void constructor_initializesCorrectly() {
        MythDateTime from = MythDateTime.parse("2025-06-01-09-00-00");
        MythDateTime to = MythDateTime.parse("2025-06-01-17-00-00");
        EventTask task = new EventTask("Orientation Camp", from, to);

        assertEquals("Orientation Camp", task.getDescription());
        assertEquals("E", task.getTypeCode());
        assertFalse(task.isCompleted());
    }

    /**
     * Tests that {@link EventTask#contains(MythDateTime)} accurately detects whether a date falls within the duration.
     */
    @Test
    public void contains_checksTimeRange() {
        MythDateTime from = MythDateTime.parse("2025-06-01-09-00-00");
        MythDateTime to = MythDateTime.parse("2025-06-01-17-00-00");
        EventTask task = new EventTask("Orientation Camp", from, to);

        MythDateTime inside = MythDateTime.parse("2025-06-01-12-00-00");
        MythDateTime before = MythDateTime.parse("2025-06-01-08-00-00");
        MythDateTime after = MythDateTime.parse("2025-06-01-18-00-00");

        assertTrue(task.contains(inside));
        assertFalse(task.contains(before));
        assertFalse(task.contains(after));
    }

    /**
     * Tests serialization of an {@link EventTask} via {@link EventTask#extract()}.
     */
    @Test
    public void extract_serializesAttributes() {
        MythDateTime from = MythDateTime.parse("2025-06-01-09-00-00");
        MythDateTime to = MythDateTime.parse("2025-06-01-17-00-00");
        EventTask task = new EventTask("Orientation Camp", from, to);

        List<String> extracted = task.extract();
        assertEquals("E", extracted.get(0));
        assertEquals("false", extracted.get(1));
        assertEquals("Orientation Camp", extracted.get(4));
        assertEquals(from.dump(), extracted.get(5));
        assertEquals(to.dump(), extracted.get(6));
    }

    /**
     * Tests formatting of {@link EventTask#toString()}.
     */
    @Test
    public void toString_formatsCorrectly() {
        MythDateTime from = MythDateTime.parse("2025-06-01-09-00-00");
        MythDateTime to = MythDateTime.parse("2025-06-01-17-00-00");
        EventTask task = new EventTask("Orientation Camp", from, to);

        String expected = String.format("[E][ ] Orientation Camp (from: %s to: %s)", from, to);
        assertEquals(expected, task.toString());

        task.mark();
        String expectedDone = String.format("[E][X] Orientation Camp (from: %s to: %s)", from, to);
        assertEquals(expectedDone, task.toString());
    }

    /**
     * Tests marking and unmarking an {@link EventTask}.
     */
    @Test
    public void markAndUnmark_updatesStatus() {
        MythDateTime from = MythDateTime.parse("2025-06-01-09-00-00");
        MythDateTime to = MythDateTime.parse("2025-06-01-17-00-00");
        EventTask task = new EventTask("Orientation Camp", from, to);

        assertFalse(task.isCompleted());
        task.mark();
        assertTrue(task.isCompleted());
        task.unmark();
        assertFalse(task.isCompleted());
    }
}
