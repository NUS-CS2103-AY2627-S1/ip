package lily.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event}. Like {@link DeadlineTest}, the focus is the integration between
 * this class's overridden methods and {@link lily.parser.DateTimeParser} /
 * {@link lily.storage.Storage#escapeField(String)}, plus the one detail specific to
 * Event: it carries two dates ({@code from} and {@code to}) instead of one, both of
 * which must be formatted and escaped independently.
 */
public class EventTest {

    @Test
    public void toString_bothDatesAtMidnight_displaysDatesOnly() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 8, 6, 0, 0), LocalDateTime.of(2019, 8, 7, 0, 0));
        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", event.toString());
    }

    @Test
    public void toString_bothDatesWithTime_displaysDatesAndTimes() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0), LocalDateTime.of(2019, 8, 6, 16, 0));
        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)",
                event.toString());
    }

    @Test
    public void toString_explicitMidnight_displaysTime() {
        Event event = new Event("midnight meeting", LocalDateTime.of(2019, 8, 6, 0, 0), true,
                LocalDateTime.of(2019, 8, 7, 0, 0), true);
        assertEquals("[E][ ] midnight meeting (from: Aug 06 2019, 12:00AM to: Aug 07 2019, 12:00AM)",
                event.toString());
    }

    @Test
    public void toString_markedDone_showsXInBox() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0), LocalDateTime.of(2019, 8, 6, 16, 0));
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Aug 06 2019, 2:00PM to: Aug 06 2019, 4:00PM)",
                event.toString());
    }

    @Test
    public void toFileString_usesFixedStorageDateFormatForBothDates() {
        Event event = new Event("project meeting",
                LocalDateTime.of(2019, 8, 6, 14, 0), LocalDateTime.of(2019, 8, 6, 16, 0));
        assertEquals("E | 0 | project meeting | 2019-08-06T14:00 | 2019-08-06T16:00 | true | true", event.toFileString());
    }

    @Test
    public void toFileString_descriptionContainingBackslash_backslashIsEscaped() {
        Event event = new Event("meet \\ discuss roadmap",
                LocalDateTime.of(2019, 8, 6, 14, 0), LocalDateTime.of(2019, 8, 6, 16, 0));
        assertEquals("E | 0 | meet \\\\ discuss roadmap | 2019-08-06T14:00 | 2019-08-06T16:00 | true | true",
                event.toFileString());
    }
}
