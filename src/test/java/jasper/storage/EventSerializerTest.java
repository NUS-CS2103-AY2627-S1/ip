package jasper.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import jasper.JasperException;
import jasper.task.Event;

public class EventSerializerTest {
    private final LocalDateTime fromDate = LocalDateTime.of(2023, 10, 31, 14, 0);
    private final LocalDateTime toDate = LocalDateTime.of(2023, 10, 31, 16, 0);

    @Test
    public void serialize_uncompletedEvent_correctFormat() {
        Event event = new Event("project meeting", fromDate, toDate);
        assertEquals("E | 0 | project meeting | 2023-10-31T14:00 | 2023-10-31T16:00",
                EventSerializer.serialize(event));
    }

    @Test
    public void deserialize_validString_eventReturned() throws JasperException {
        Event event = EventSerializer.deserialize("E | 0 | project meeting | 2023-10-31T14:00 | 2023-10-31T16:00");
        assertEquals("project meeting", event.getDescription());
        assertFalse(event.isDone());
        assertEquals(fromDate.toString(), event.getFrom());
        assertEquals(toDate.toString(), event.getTo());
    }

    @Test
    public void deserialize_missingParts_exceptionThrown() {
        assertThrows(JasperException.class, () ->
                EventSerializer.deserialize("E | 0 | project meeting | 2023-10-31T14:00")
        );
    }
}
