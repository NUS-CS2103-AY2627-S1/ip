package computa;

import org.junit.jupiter.api.Test;
import java.time.format.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    @Test void validEventDisplaysStartAndEnd() {
        Event event = new Event("meeting", "2026-12-01 1400", "2026-12-01 1500");
        assertEquals("[E][] meeting (from: Dec 01 2026 2:00 pm to: Dec 01 2026 3:00 pm)\n",
                event.getTaskDescription());
    }

    @Test void invalidEventDateIsRejected() {
        assertThrows(DateTimeParseException.class,
                () -> new Event("meeting", "01-12-2026 1400", "2026-12-01 1500"));
    }

    @Test void eventFileFormatStoresAllFields() {
        Event event = new Event("meeting", "2026-12-01 1400", "2026-12-01 1500");
        assertEquals("E | 0 | meeting |2026-12-01 1400 |2026-12-01 1500", event.toFileFormat());
    }
}
