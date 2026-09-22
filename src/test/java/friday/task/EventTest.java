package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests tasks that occur over a period. */
class EventTest {

    @Test
    void toString_validEvent_returnsFormattedDisplay() {
        Event event = new Event(new String[] {"meeting", "2pm", "3pm"});

        assertEquals("[E][ ] meeting (from: 2pm to: 3pm)", event.toString());
    }

    @Test
    void constructor_valuesWithPrefixes_removesPrefixes() {
        Event event = new Event(new String[] {"meeting", "from 2pm", "to 3pm"});

        assertEquals("[E][ ] meeting (from: 2pm to: 3pm)", event.toString());
    }

    @Test
    void constructor_missingEnd_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                new Event(new String[] {"meeting", "2pm"}));
    }

    @Test
    void constructor_invalidStructuredTimes_rejectsEvent() {
        String[][] periods = {
            {"0pm", "2pm"}, {"13pm", "2pm"}, {"3pm", "2pm"}, {"2pm", "2pm"}, {"14:00", "13:00"},
            {"2026-02-30", "2026-03-02"}, {"2026-09-02", "2026-09-01"},
            {"2026-09-01T14:00", "2026-09-01T13:00"}, {"2026-09-01", "14:00"}
        };
        for (String[] period : periods) {
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    new Event(new String[] {"meeting", period[0], period[1]}));
            assertTrue(exception instanceof IllegalArgumentException
                    || exception instanceof DateTimeParseException);
        }
    }

    @Test
    void constructor_validPeriods_acceptsEvent() {
        new Event(new String[] {"meeting", "2024-02-29", "2024-03-01"});
        new Event(new String[] {"meeting", "11:30am", "2:30pm"});
        new Event(new String[] {"meeting", "Monday afternoon", "Tuesday morning"});
    }

}
