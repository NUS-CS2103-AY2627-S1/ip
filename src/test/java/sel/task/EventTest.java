package sel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import sel.exception.SelException;

public class EventTest {

    @Test
    public void gettersReturnExactValuesPassedIn() throws SelException {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        Event event = new Event("project meeting", from, to);
        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
    }

    @Test
    public void toString_notDone_formatsBothDatesCorrectly() throws SelException {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        Event event = new Event("project meeting", from, to);
        assertEquals("[E][ ] project meeting(from:Dec 2 2019, 2:00PM to:Dec 2 2019, 4:00PM)",
            event.toString());
    }

    @Test
    public void toString_marked_showsXInStatusBox() throws SelException {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        Event event = new Event("project meeting", from, to);
        event.mark();
        assertEquals("[E][X] project meeting(from:Dec 2 2019, 2:00PM to:Dec 2 2019, 4:00PM)",
            event.toString());
    }

    @Test
    public void toString_spanningDifferentDays_showsBothDatesFully() throws SelException {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 23, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 3, 1, 0);
        Event event = new Event("overnight shift", from, to);
        assertEquals("[E][ ] overnight shift(from:Dec 2 2019, 11:00PM to:Dec 3 2019, 1:00AM)",
            event.toString());
    }

    @Test
    public void constructor_endBeforeStart_throws() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 18, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        assertThrows(SelException.class, () -> new Event("backwards", from, to));
    }

    @Test
    public void constructor_endSameAsStart_throws() {
        LocalDateTime at = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertThrows(SelException.class, () -> new Event("zero length", at, at));
    }

    @Test
    public void constructor_oneMinuteLong_isAccepted() throws SelException {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 18, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 18, 1);
        assertEquals(to, new Event("quick", from, to).getTo());
    }

    @Test
    public void constructor_missingDate_throws() {
        LocalDateTime at = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertThrows(SelException.class, () -> new Event("no end", at, null));
        assertThrows(SelException.class, () -> new Event("no start", null, at));
    }

    @Test
    public void hasSameDetailsAs_needsSameDescriptionAndBothDates() throws SelException {
        LocalDateTime from = LocalDateTime.of(2019, 12, 2, 14, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 2, 16, 0);
        LocalDateTime laterTo = LocalDateTime.of(2019, 12, 2, 17, 0);
        LocalDateTime earlierFrom = LocalDateTime.of(2019, 12, 2, 13, 0);

        Event event = new Event("meeting", from, to);

        assertTrue(event.hasSameDetailsAs(new Event("meeting", from, to)));
        assertTrue(event.hasSameDetailsAs(new Event("MEETING", from, to)));
        assertFalse(event.hasSameDetailsAs(new Event("meeting", from, laterTo)));
        assertFalse(event.hasSameDetailsAs(new Event("meeting", earlierFrom, to)));
        assertFalse(event.hasSameDetailsAs(new Event("other", from, to)));
        assertFalse(event.hasSameDetailsAs(new ToDo("meeting")));
        assertFalse(event.hasSameDetailsAs(null));
    }
}
