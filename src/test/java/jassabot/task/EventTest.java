package jassabot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests Event display and storage representations.
 */
public class EventTest {

    @Test
    public void toString_eventDateRange_formatsBothEndpoints() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 30));

        assertEquals("[E][ ] project meeting (from: Dec 2 2019, 2:00 PM "
                        + "to: Dec 2 2019, 4:30 PM)",
                event.toString());
    }

    @Test
    public void toDataString_markedEvent_usesStableFormatAndEscapesDescription() {
        Event event = new Event(
                "A \\ B",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 30));
        event.markAsDone();

        assertEquals("E | 1 | A \\\\ B | 2019-12-02T14:00 | 2019-12-02T16:30",
                event.toDataString());
    }
}
