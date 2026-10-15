import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tardt.exception.TardTException;
import tardt.priority.Priority;
import tardt.task.Deadline;
import tardt.task.Event;

/** Tests the date and time input formats accepted by deadline and event tasks. */
class DateTimeInputTest {
    @Test
    void deadline_commonDateFormats_areParsedWithMidnightDefault() throws TardTException {
        assertEquals("Sep 29 2026 00:00", new Deadline("submit work", "2026-09-29", Priority.LOW).getBy());
        assertEquals("Sep 29 2026 00:00", new Deadline("submit work", "29/09/2026", Priority.LOW).getBy());
        assertEquals("Sep 29 2026 00:00", new Deadline("submit work", "29 Sep 2026", Priority.LOW).getBy());
    }

    @Test
    void event_commonTimeFormats_areParsed() throws TardTException {
        Event event = new Event("meeting", "29 Sep 2026 2pm", "29 Sep 2026 2:30pm", Priority.MEDIUM);

        assertEquals("Sep 29 2026 14:00", event.getFrom());
        assertEquals("Sep 29 2026 14:30", event.getTo());
    }

    @Test
    void event_spaceSeparatedIsoDateAnd24HourTime_areParsed() throws TardTException {
        Event event = new Event("meeting", "2026-09-29 14:00", "2026-09-29 15:00", Priority.LOW);

        assertEquals("Sep 29 2026 14:00", event.getFrom());
        assertEquals("Sep 29 2026 15:00", event.getTo());
    }
}
