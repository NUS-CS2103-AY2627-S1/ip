package zhangwei.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event}, which carries two dates rather than one. The tests
 * check that each date is kept and shown in its own place, so a mix-up between
 * the start and the end would be caught.
 */
public class EventTest {

    @Test
    public void getFrom_eventCreatedWithDates_startDateReturned() {
        Event event = new Event("project meeting",
                LocalDate.of(2019, 12, 3), LocalDate.of(2019, 12, 4));
        assertEquals(LocalDate.of(2019, 12, 3), event.getFrom());
    }

    @Test
    public void getTo_eventCreatedWithDates_endDateReturned() {
        Event event = new Event("project meeting",
                LocalDate.of(2019, 12, 3), LocalDate.of(2019, 12, 4));
        assertEquals(LocalDate.of(2019, 12, 4), event.getTo());
    }

    @Test
    public void toString_notDoneEvent_bothDatesShownInDisplayFormat() {
        Event event = new Event("project meeting",
                LocalDate.of(2019, 12, 3), LocalDate.of(2019, 12, 4));
        assertEquals("[E][ ] project meeting (from: Dec 3 2019 to: Dec 4 2019)",
                event.toString());
    }

    @Test
    public void toString_doneEvent_crossedBoxShown() {
        Event event = new Event("project meeting",
                LocalDate.of(2019, 12, 3), LocalDate.of(2019, 12, 4));
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Dec 3 2019 to: Dec 4 2019)",
                event.toString());
    }

    @Test
    public void toString_sameStartAndEndDate_bothShown() {
        Event event = new Event("orientation",
                LocalDate.of(2021, 8, 9), LocalDate.of(2021, 8, 9));
        assertEquals("[E][ ] orientation (from: Aug 9 2021 to: Aug 9 2021)",
                event.toString());
    }

    @Test
    public void toString_eventSpanningYears_bothYearsShown() {
        Event event = new Event("winter break",
                LocalDate.of(2019, 12, 20), LocalDate.of(2020, 1, 5));
        assertEquals("[E][ ] winter break (from: Dec 20 2019 to: Jan 5 2020)",
                event.toString());
    }
}
