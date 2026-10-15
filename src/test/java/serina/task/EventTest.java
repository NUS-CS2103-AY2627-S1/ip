package serina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the date-range behavior of {@link Event} tasks.
 */
public class EventTest {
    private static final LocalDate START_DATE = LocalDate.of(2026, 8, 10);
    private static final LocalDate END_DATE = LocalDate.of(2026, 8, 12);

    private final Event event = new Event("orientation", START_DATE, END_DATE);

    @Test
    public void isOccurringOn_dateBeforeStart_returnsFalse() {
        assertFalse(event.isOccurringOn(START_DATE.minusDays(1)));
    }

    @Test
    public void isOccurringOn_startDate_returnsTrue() {
        assertTrue(event.isOccurringOn(START_DATE));
    }

    @Test
    public void isOccurringOn_dateBetweenStartAndEnd_returnsTrue() {
        assertTrue(event.isOccurringOn(START_DATE.plusDays(1)));
    }

    @Test
    public void isOccurringOn_endDate_returnsTrue() {
        assertTrue(event.isOccurringOn(END_DATE));
    }

    @Test
    public void isOccurringOn_dateAfterEnd_returnsFalse() {
        assertFalse(event.isOccurringOn(END_DATE.plusDays(1)));
    }

    @Test
    public void toString_notDoneEvent_returnsFormattedDisplayString() {
        assertEquals("[E][ ] orientation (from: Aug 10 2026 to: Aug 12 2026)", event.toString());
    }

    @Test
    public void toString_doneEvent_returnsFormattedDisplayString() {
        event.markAsDone();

        assertEquals("[E][X] orientation (from: Aug 10 2026 to: Aug 12 2026)", event.toString());
    }

    @Test
    public void toFileString_eventWithSpecialCharacters_returnsEscapedFileString() {
        Event eventWithSpecialCharacters = new Event("review | notes \\ draft", START_DATE, END_DATE);

        assertEquals("E | 0 | review \\| notes \\\\ draft | 2026-08-10 | 2026-08-12",
                eventWithSpecialCharacters.toFileString());
    }

    @Test
    public void toFileString_doneEvent_returnsDoneStatusInFileString() {
        event.markAsDone();

        assertEquals("E | 1 | orientation | 2026-08-10 | 2026-08-12", event.toFileString());
    }

    @Test
    public void copy_unmarkingCopy_doesNotChangeOriginal() {
        event.markAsDone();
        Task copy = event.copy();

        copy.markAsNotDone();

        assertTrue(event.isDone());
        assertFalse(copy.isDone());
        assertEquals(TaskType.EVENT, copy.getTaskType());
    }
}
