package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for {@link Event}'s display string and its data-file encoding. */
public class EventTest {

    @Test
    public void toString_dateOnlyEndpoints_showsBothFormattedDates() {
        Event event = new Event("camp", DateTime.parse("2019-06-01"), DateTime.parse("2019-06-03"));
        assertEquals("[E][ ] camp (from: Jun 01 2019 to: Jun 03 2019)", event.toString());
    }

    @Test
    public void toString_sameDayWithTimes_showsTimes() {
        Event event = new Event("meeting",
                DateTime.parse("2019-12-02 1400"), DateTime.parse("2019-12-02 1600"));
        assertEquals("[E][ ] meeting (from: Dec 02 2019 2:00pm to: Dec 02 2019 4:00pm)",
                event.toString());
    }

    @Test
    public void toString_done_showsCrossInStatusBox() {
        Event event = new Event("camp", DateTime.parse("2019-06-01"), DateTime.parse("2019-06-03"));
        event.markAsDone();
        assertEquals("[E][X] camp (from: Jun 01 2019 to: Jun 03 2019)", event.toString());
    }

    @Test
    public void toFileFormat_encodesTypeFlagDescriptionAndBothEndpoints() {
        Event event = new Event("meeting",
                DateTime.parse("2019-12-02 1400"), DateTime.parse("2019-12-02 1600"));
        assertEquals("E | 0 | meeting | 2019-12-02 1400 | 2019-12-02 1600", event.toFileFormat());
    }

    @Test
    public void toFileFormat_done_usesOneFlag() {
        Event event = new Event("camp", DateTime.parse("2019-06-01"), DateTime.parse("2019-06-03"));
        event.markAsDone();
        assertEquals("E | 1 | camp | 2019-06-01 | 2019-06-03", event.toFileFormat());
    }

    // ---- recurrence -----------------------------------------------------

    @Test
    public void toString_recurring_showsEverySuffix() {
        Event event = new Event("standup",
                DateTime.parse("2025-01-06 0900"), DateTime.parse("2025-01-06 0930"), RecurrencePeriod.WEEK);
        assertEquals("[E][ ] standup (from: Jan 06 2025 9:00am to: Jan 06 2025 9:30am) (every: week)",
                event.toString());
    }

    @Test
    public void toFileFormat_recurring_appendsPeriodAsSixthField() {
        Event event = new Event("standup",
                DateTime.parse("2025-01-06 0900"), DateTime.parse("2025-01-06 0930"), RecurrencePeriod.WEEK);
        assertEquals("E | 0 | standup | 2025-01-06 0900 | 2025-01-06 0930 | week", event.toFileFormat());
    }

    @Test
    public void isRecurring_nonRecurring_isFalse() {
        Event event = new Event("camp", DateTime.parse("2019-06-01"), DateTime.parse("2019-06-03"));
        assertFalse(event.isRecurring());
    }

    @Test
    public void isRecurring_recurring_isTrue() {
        Event event = new Event("standup",
                DateTime.parse("2025-01-06 0900"), DateTime.parse("2025-01-06 0930"), RecurrencePeriod.WEEK);
        assertTrue(event.isRecurring());
    }

    @Test
    public void markAsDone_recurring_advancesBothEndpointsPreservingDuration() {
        Event event = new Event("standup",
                DateTime.parse("2025-01-06 0900"), DateTime.parse("2025-01-06 0930"), RecurrencePeriod.WEEK);
        event.markAsDone();
        assertEquals("[E][ ] standup (from: Jan 13 2025 9:00am to: Jan 13 2025 9:30am) (every: week)",
                event.toString());
    }
}
