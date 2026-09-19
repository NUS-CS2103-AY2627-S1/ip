package chai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests event display formatting and completion state. */
public class EventTest {

    @Test
    public void constructor_endBeforeStart_assertionErrorThrown() {
        LocalDate from = LocalDate.of(2026, 9, 19);
        LocalDate to = LocalDate.of(2026, 9, 18);

        assertThrows(AssertionError.class, () -> new Event("conference", from, to));
    }

    @Test
    public void toString_dateRange_formatsBothDatesAndIncompleteStatus() {
        Event event = new Event(
                "orientation",
                LocalDate.of(2019, 10, 4),
                LocalDate.of(2019, 10, 11));

        assertEquals(
                "[E][ ] orientation (from: Oct 4 2019 to: Oct 11 2019)",
                event.toString());
    }

    @Test
    public void toString_singleDayCompletedEvent_formatsDatesAndCompletedStatus() {
        LocalDate eventDate = LocalDate.of(2024, 2, 29);
        Event event = new Event("leap day meeting", eventDate, eventDate);

        event.markAsDone();

        assertEquals(
                "[E][X] leap day meeting (from: Feb 29 2024 to: Feb 29 2024)",
                event.toString());
    }
}
