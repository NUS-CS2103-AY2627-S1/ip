package rambo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the storage and display formats of {@link EventTask}.
 */
public class EventTaskTest {
    @Test
    void toDataString_eventTask_returnsStorageFormat() {
        EventTask task = new EventTask("project meeting", "2026-09-20", "2026-09-22");

        assertEquals("E||project meeting|2026-09-20|2026-09-22|0", task.toDataString());
    }

    @Test
    void toDataString_completedEventTask_includesDoneStatus() {
        EventTask task = new EventTask("project meeting", "2026-09-20", "2026-09-22");
        task.toggleDone();

        assertEquals("E|X|project meeting|2026-09-20|2026-09-22|0", task.toDataString());
    }

    @Test
    void toString_eventTask_returnsFriendlyDateRange() {
        EventTask task = new EventTask("project meeting", "2026-09-20", "2026-09-22");

        assertEquals("[E][] project meeting (from: Sep 20 2026 to: Sep 22 2026)", task.toString());
    }

    @Test
    void toString_prioritizedEventTask_includesPriority() {
        EventTask task = new EventTask("project meeting", "2026-09-20", "2026-09-22");
        task.setPriorityLevel(3);

        assertEquals("[E][][P3] project meeting (from: Sep 20 2026 to: Sep 22 2026)", task.toString());
    }
}
