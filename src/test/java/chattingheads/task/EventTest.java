package chattingheads.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import chattingheads.exception.InvalidInputException;

public class EventTest {

    @Test
    public void constructor_endAfterStart_createsEvent() throws InvalidInputException {
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0));

        assertEquals("E,meeting,false,2026-08-28T18:00,2026-08-28T20:00", event.toCsv());
    }

    @Test
    public void constructor_endEqualsStart_throwsInvalidInputException() {
        LocalDateTime time = LocalDateTime.of(2026, 8, 28, 18, 0);
        assertThrows(InvalidInputException.class, () -> new Event("meeting", time, time));
    }

    @Test
    public void constructor_endBeforeStart_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 20, 0),
                LocalDateTime.of(2026, 8, 28, 18, 0)
        ));
    }

    @Test
    public void toString_validEvent_returnsFormattedDateTimes() throws InvalidInputException {
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 5),
                LocalDateTime.of(2026, 8, 28, 20, 30));

        assertEquals(
                "[E][ ] meeting (from: 28/08/2026 18:05 to: 28/08/2026 20:30)",
                event.toString());
    }

    @Test
    public void toCsv_completedEvent_returnsCsvString() throws InvalidInputException {
        Event event = new Event(
                "meeting",
                true,
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0));

        assertEquals("E,meeting,true,2026-08-28T18:00,2026-08-28T20:00", event.toCsv());
    }

    @Test
    public void reschedule_validRange_timesChanged() throws InvalidInputException {
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0));

        event.reschedule(
                LocalDateTime.of(2026, 8, 30, 10, 0),
                LocalDateTime.of(2026, 8, 30, 12, 0));

        assertEquals("E,meeting,false,2026-08-30T10:00,2026-08-30T12:00", event.toCsv());
    }

    @Test
    public void reschedule_invalidRange_throwsAndKeepsExistingTimes() throws InvalidInputException {
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2026, 8, 28, 18, 0),
                LocalDateTime.of(2026, 8, 28, 20, 0));

        assertThrows(InvalidInputException.class, () -> event.reschedule(
                LocalDateTime.of(2026, 8, 30, 12, 0),
                LocalDateTime.of(2026, 8, 30, 10, 0)
        ));
        assertEquals("E,meeting,false,2026-08-28T18:00,2026-08-28T20:00", event.toCsv());
    }
}
