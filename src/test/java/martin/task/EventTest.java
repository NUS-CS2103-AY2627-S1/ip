package martin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import martin.exception.IllegalCommandException;

/** Unit tests for event priority parsing. */
public class EventTest {

    @Test
    public void parseEventFromInputString_priorityParameter_createsEventWithPriority() {
        Event event = Event.parseEventFromInputString(
                "event project meeting /from 2026/08/26 1400 /to 2026/08/26 1500 /p high");

        assertEquals(Priority.HIGH, event.getPriority());
        assertEquals("[E][ ][HIGH] project meeting (from: Aug 26 2026, 14:00 to: Aug 26 2026, 15:00)",
                event.toString());
    }

    @Test
    public void parseEventFromInputString_equalStartAndEnd_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () -> Event.parseEventFromInputString(
                "event project meeting /from 2026/08/26 1400 /to 2026/08/26 1400"));
    }

    @Test
    public void parseEventFromInputString_repeatedFromDelimiter_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () -> Event.parseEventFromInputString(
                "event project meeting /from 2026/08/26 1400 /from 2026/08/26 1430 /to 2026/08/26 1500"));
    }
}
