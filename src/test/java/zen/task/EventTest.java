package zen.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;


public class EventTest {
    private static final LocalDateTime START_DATE_TIME = LocalDateTime.of(2026, 10, 10, 10, 30, 0);
    private static final LocalDateTime END_DATE_TIME = LocalDateTime.of(2026, 10, 15, 10, 30, 0);
    private static final LocalDate START_DATE = START_DATE_TIME.toLocalDate();
    private static final LocalDate END_DATE = END_DATE_TIME.toLocalDate();
    private static final String DESCRIPTION = "party";

    @Test
    public void toStorageString_isNotDone_formatsCorrectly() {
        Event event = new Event(DESCRIPTION, START_DATE_TIME, END_DATE_TIME);
        String expectedOutput = String.format("E | %d | %s | %s to %s | none", 0, DESCRIPTION,
                START_DATE_TIME, END_DATE_TIME);
        assertEquals(expectedOutput, event.toStorageString());
    }

    @Test
    public void toStorageString_isDone_formatsCorrectly() {
        Event event = new Event(DESCRIPTION, START_DATE_TIME, END_DATE_TIME);
        event.markAsDone();
        String expectedOutput = String.format("E | %d | %s | %s to %s | none", 1, DESCRIPTION,
                START_DATE_TIME, END_DATE_TIME);
        assertEquals(expectedOutput, event.toStorageString());
    }

    @Test
    public void occursOn_startDate_returnsTrue() {
        Event event = new Event(DESCRIPTION, START_DATE_TIME, END_DATE_TIME);
        assertTrue(event.occursOn(START_DATE));
    }

    @Test
    public void occursOn_middleDate_returnsTrue() {
        Event event = new Event(DESCRIPTION, START_DATE_TIME, END_DATE_TIME);
        assertTrue(event.occursOn(START_DATE.plusDays(2)));
    }

    @Test
    public void occursOn_endDate_returnsTrue() {
        Event event = new Event(DESCRIPTION, START_DATE_TIME, END_DATE_TIME);
        assertTrue(event.occursOn(END_DATE));
    }

    @Test
    public void occursOn_dateBeforeStartDate_returnsFalse() {
        Event event = new Event(DESCRIPTION, START_DATE_TIME, END_DATE_TIME);
        assertFalse(event.occursOn(START_DATE.minusDays(1)));
    }

    @Test
    public void occursOn_dateAfterEndDate_returnsFalse() {
        Event event = new Event(DESCRIPTION, START_DATE_TIME, END_DATE_TIME);
        assertFalse(event.occursOn(END_DATE.plusDays(1)));
    }
}
