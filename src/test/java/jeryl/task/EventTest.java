package jeryl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void toString_startAndEndDates_printsBothInMmmDdYyyyFormat() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", event.toString());
    }

    @Test
    public void toFileString_startAndEndDates_encodesBothInIsoFormat() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        assertEquals("E | 0 | project meeting | 2019-08-06 | 2019-08-07", event.toFileString());
    }
}
