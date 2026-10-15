package alex.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import alex.util.TaskDateTime;

/**
 * Tests event display behavior.
 */
public class EventTest {
    @Test
    public void toString_timedEvent_formatsDateRangeAndCompletionStatus() {
        Event event = new Event(
                "team meeting",
                new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(9, 0)),
                new TaskDateTime(LocalDate.of(2019, 12, 2), LocalTime.of(10, 30)));

        assertEquals(
                "[E][ ] team meeting (from: Dec 2 2019 0900 to: Dec 2 2019 1030)",
                event.toString());

        event.markAsDone();
        assertEquals(
                "[E][X] team meeting (from: Dec 2 2019 0900 to: Dec 2 2019 1030)",
                event.toString());
    }
}
