package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests date/time parsing together with task construction and task-list search.
 */
class DatetimeTaskIntegrationTest {
    @Test
    void typedTasks_withTextDateTimes_keepNormalizedValuesAndRemainSearchable() {
        Deadline deadline = new Deadline("launch", "Aug 6 2026 2pm");
        Event event = new Event("review", "6 August 2026 14:00", "4:00pm");
        TaskList tasks = new TaskList(List.of(deadline, event));

        assertEquals("2026-08-06T14:00", deadline.getExtra());
        assertEquals("2026-08-06T14:00|2026-08-06T16:00", event.getExtra());
        assertEquals(1, tasks.find("review").size());
        assertEquals("[E][ ] review (from: 06 Aug 26, 1400hrs to: 06 Aug 26, 1600hrs)",
                tasks.find("review").iterator().next().displayText());
    }
}
