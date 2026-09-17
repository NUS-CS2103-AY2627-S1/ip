package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import rocky.RockyException;

/** Tests for creating an {@link Event} from typed start/end dates, and for how it is displayed. */
public class EventTest {
    private static final String INVALID_DATES_MESSAGE =
            "Please give event dates as yyyy-mm-dd, e.g. 2019-10-15.";

    @Test
    public void constructor_isoDates_detailsStoredAndNotDone() throws RockyException {
        Event event = new Event("meeting", "2019-12-02", "2019-12-03");

        assertEquals("meeting", event.getDescription());
        assertEquals("2019-12-02", event.getStartDate());
        assertEquals("2019-12-03", event.getEndDate());
        assertFalse(event.isDone());
    }

    @Test
    public void constructor_sameStartAndEndDate_accepted() throws RockyException {
        Event event = new Event("hackathon", "2019-12-02", "2019-12-02");

        assertEquals("2019-12-02", event.getStartDate());
        assertEquals("2019-12-02", event.getEndDate());
    }

    @Test
    public void constructor_invalidStartDate_exceptionThrown() {
        RockyException e = assertThrows(RockyException.class,
                () -> new Event("meeting", "tomorrow", "2019-12-03"));
        assertEquals(INVALID_DATES_MESSAGE, e.getMessage());
    }

    @Test
    public void constructor_invalidEndDate_exceptionThrown() {
        RockyException e = assertThrows(RockyException.class,
                () -> new Event("meeting", "2019-12-02", "2019/12/03"));
        assertEquals(INVALID_DATES_MESSAGE, e.getMessage());
    }

    @Test
    public void constructor_bothDatesInvalid_exceptionThrown() {
        RockyException e = assertThrows(RockyException.class, () -> new Event("meeting", "", "someday"));
        assertEquals(INVALID_DATES_MESSAGE, e.getMessage());
    }

    @Test
    public void toString_notDone_emptyCheckboxAndFormattedDates() throws RockyException {
        assertEquals("[E][ ] meeting (from: Dec 02 2019 to: Dec 03 2019)",
                new Event("meeting", "2019-12-02", "2019-12-03").toString());
    }

    @Test
    public void toString_done_tickedCheckbox() throws RockyException {
        Event event = new Event("meeting", "2019-12-02", "2019-12-03");
        event.mark();

        assertEquals("[E][x] meeting (from: Dec 02 2019 to: Dec 03 2019)", event.toString());
    }
}
