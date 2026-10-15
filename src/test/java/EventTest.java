
package sioet.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
public class EventTest {

    @Test
    public void toString_returnsCorrectEventFormat() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0)
        );

        assertEquals(
                "[E][ ] project meeting "
                        + "(from: Aug 06 2026, 2:00 PM "
                        + "to: Aug 06 2026, 4:00 PM)",
                event.toString()
        );
    }

    @Test
    public void toString_completedEvent_showsCompletedStatus() {
        Event event = new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0)
        );

        event.markAsDone();

        assertEquals(
                "[E][X] project meeting "
                        + "(from: Aug 06 2026, 2:00 PM "
                        + "to: Aug 06 2026, 4:00 PM)",
                event.toString()
        );
    }
}

