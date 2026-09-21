package goat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests how an event renders its two dates, which is where the field order matters. */
public class EventTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2019, 8, 6, 16, 0);

    @Test
    public void toString_withTimes_showsStartThenEnd() {
        Event event = new Event("project meeting", FROM, TO);
        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00pm to: Aug 06 2019, 4:00pm)",
                event.toString());
    }

    @Test
    public void toString_atMidnight_showsDatesOnly() {
        Event event = new Event("holiday",
                LocalDateTime.of(2019, 12, 24, 0, 0), LocalDateTime.of(2019, 12, 26, 0, 0));
        assertEquals("[E][ ] holiday (from: Dec 24 2019 to: Dec 26 2019)", event.toString());
    }

    @Test
    public void toString_done_showsX() {
        Event event = new Event("project meeting", FROM, TO);
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Aug 06 2019, 2:00pm to: Aug 06 2019, 4:00pm)",
                event.toString());
    }

    @Test
    public void toFileFormat_withTimes_writesStartThenEnd() {
        Event event = new Event("project meeting", FROM, TO);
        assertEquals("E | 0 | project meeting | 2019-08-06 1400 | 2019-08-06 1600",
                event.toFileFormat());
    }

    @Test
    public void toFileFormat_done_writesOne() {
        Event event = new Event("project meeting", FROM, TO);
        event.markAsDone();
        assertEquals("E | 1 | project meeting | 2019-08-06 1400 | 2019-08-06 1600",
                event.toFileFormat());
    }
}
