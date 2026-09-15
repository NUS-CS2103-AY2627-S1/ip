package tianyi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests event range inclusion, persistence, and display formatting.
 */
public class EventTest {
    @Test
    public void isOccurringOn_datesAroundRange_returnsExpectedResults() {
        Event event = new Event("workshop", new TaskTime("2-12-2019 09:00"),
                new TaskTime("4-12-2019 17:00"));

        assertFalse(event.isOccurringOn(new TaskTime("1-12-2019")));
        assertTrue(event.isOccurringOn(new TaskTime("2-12-2019")));
        assertTrue(event.isOccurringOn(new TaskTime("3-12-2019")));
        assertTrue(event.isOccurringOn(new TaskTime("4-12-2019")));
        assertFalse(event.isOccurringOn(new TaskTime("5-12-2019")));
    }

    @Test
    public void formatting_incompleteEvent_includesRangeDetails() {
        Event event = new Event("workshop", new TaskTime("2-12-2019 09:00"),
                new TaskTime("3-12-2019 17:00"));

        assertEquals("E | 0 | workshop | 2-12-2019 09:00 | 3-12-2019 17:00",
                event.getData());
        assertEquals("[E][ ] workshop (from: Mon, Dec 02 2019, 9:00 AM "
                + "to: Tue, Dec 03 2019, 5:00 PM)", event.toString());
    }

    @Test
    public void formatting_completedEvent_includesCompletedStatus() {
        Event event = new Event("workshop", new TaskTime("2-12-2019"),
                new TaskTime("3-12-2019"));
        event.markAsDone();

        assertEquals("E | 1 | workshop | 2-12-2019 | 3-12-2019", event.getData());
        assertEquals("[E][X] workshop (from: Mon, Dec 02 2019 to: Tue, Dec 03 2019)",
                event.toString());
    }
}
