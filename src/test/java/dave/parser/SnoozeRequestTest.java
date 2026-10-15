package dave.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import dave.exception.DaveCommandException;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Todo;

/**
 * Unit tests for the {@link SnoozeRequest} class.
 */
public class SnoozeRequestTest {

    @Test
    public void applyTo_todoTask_throwsDaveCommandException() {
        SnoozeRequest request = new SnoozeRequest(0, 1, ChronoUnit.DAYS);
        Todo todo = new Todo("read book");

        DaveCommandException ex = assertThrows(DaveCommandException.class, () -> request.applyTo(todo));
        assertTrue(ex.getMessage().contains("Todo tasks cannot be snoozed"));
    }

    @Test
    public void applyTo_eventRescheduleOnDeadline_throwsDaveCommandException() {
        ParsedDateTime from = new ParsedDateTime(LocalDateTime.of(2026, 10, 1, 10, 0), true);
        ParsedDateTime to = new ParsedDateTime(LocalDateTime.of(2026, 10, 1, 12, 0), true);
        SnoozeRequest request = new SnoozeRequest(0, from, to);
        Deadline deadline = new Deadline("submit essay", LocalDateTime.of(2026, 10, 1, 12, 0));

        DaveCommandException ex = assertThrows(DaveCommandException.class, () -> request.applyTo(deadline));
        assertTrue(ex.getMessage().contains("A deadline cannot be rescheduled with /from and /to"));
    }

    @Test
    public void applyTo_deadlineToOnEvent_throwsDaveCommandException() {
        ParsedDateTime target = new ParsedDateTime(LocalDateTime.of(2026, 10, 15, 12, 0), true);
        SnoozeRequest request = new SnoozeRequest(0, target);
        Event event = new Event("concert",
                LocalDateTime.of(2026, 10, 1, 10, 0),
                LocalDateTime.of(2026, 10, 1, 12, 0));

        DaveCommandException ex = assertThrows(DaveCommandException.class, () -> request.applyTo(event));
        assertTrue(ex.getMessage().contains("Rescheduling an event requires /from [time] and /to [time]"));
    }

    @Test
    public void applyTo_relativeSnoozeOnEvent_shiftsBothBoundaries() {
        SnoozeRequest request = new SnoozeRequest(0, 2, ChronoUnit.HOURS);
        Event event = new Event("seminar",
                LocalDateTime.of(2026, 10, 1, 10, 0),
                LocalDateTime.of(2026, 10, 1, 12, 0));

        request.applyTo(event);
        assertEquals(LocalDateTime.of(2026, 10, 1, 12, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 10, 1, 14, 0), event.getTo());
    }
}
