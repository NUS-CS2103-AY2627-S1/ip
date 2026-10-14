package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void getFromAndGetTo_returnDatesGivenToConstructor() {
        LocalDate from = LocalDate.of(2019, 8, 6);
        LocalDate to = LocalDate.of(2019, 8, 7);
        Event event = new Event("project meeting", from, to);

        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
    }

    @Test
    void toString_notDone_showsBothDatesFormatted() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));

        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", event.toString());
    }

    @Test
    void toString_done_showsXInsideTypeTag() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        event.markAsDone();

        assertEquals("[E][X] project meeting (from: Aug 06 2019 to: Aug 07 2019)", event.toString());
    }

    @Test
    void toSaveFormat_savesBothDatesInIsoFormat() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));

        assertEquals("E | 0 | project meeting | 2019-08-06 | 2019-08-07", event.toSaveFormat());
    }

    @Test
    void toSaveFormat_done_savesDoneFlagAsOne() {
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 7));
        event.markAsDone();

        assertEquals("E | 1 | project meeting | 2019-08-06 | 2019-08-07", event.toSaveFormat());
    }
}
