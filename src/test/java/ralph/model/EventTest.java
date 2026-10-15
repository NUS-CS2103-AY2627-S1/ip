package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void toString_bothMidnight_datesOnly() {
        Event e = new Event("holiday", LocalDateTime.of(2023, 12, 25, 0, 0), LocalDateTime.of(2023, 12, 26, 0, 0));
        assertEquals("[E][ ] holiday (from: Dec 25 2023 to: Dec 26 2023)", e.toString());
    }

    @Test
    void toString_fromMidnightToWithTime_timeShown() {
        Event e = new Event("conference", LocalDateTime.of(2023, 9, 1, 0, 0), LocalDateTime.of(2023, 9, 1, 18, 30));
        assertEquals("[E][ ] conference (from: Sep 01 2023 to: Sep 01 2023 18:30)", e.toString());
    }

    @Test
    void toString_bothWithTimes_timeShown() {
        Event e = new Event("meeting", LocalDateTime.of(2023, 9, 2, 9, 0), LocalDateTime.of(2023, 9, 2, 10, 15));
        assertEquals("[E][ ] meeting (from: Sep 02 2023 09:00 to: Sep 02 2023 10:15)", e.toString());
    }
}
