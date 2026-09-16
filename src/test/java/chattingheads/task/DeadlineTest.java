package chattingheads.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toString_validDeadline_returnsFormattedDateTime() {
        Deadline deadline = new Deadline(
                "submit report",
                LocalDateTime.of(2026, 8, 28, 18, 5));

        assertEquals("[D][ ] submit report (by: 28/08/2026 18:05)", deadline.toString());
    }

    @Test
    public void toCsv_validDeadline_returnsIsoDateTime() {
        Deadline deadline = new Deadline(
                "submit report",
                true,
                LocalDateTime.of(2026, 8, 28, 18, 5));

        assertEquals("D,submit report,true,2026-08-28T18:05", deadline.toCsv());
    }

    @Test
    public void reschedule_validDateTime_deadlineChanged() {
        Deadline deadline = new Deadline(
                "submit report",
                LocalDateTime.of(2026, 8, 28, 18, 0));

        deadline.reschedule(LocalDateTime.of(2026, 9, 1, 12, 30));

        assertEquals("D,submit report,false,2026-09-01T12:30", deadline.toCsv());
    }
}
