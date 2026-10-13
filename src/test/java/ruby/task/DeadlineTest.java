package ruby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2026, 8, 26, 18, 0);

    @Test
    void newDeadline_isNotDone() {
        Deadline deadline = new Deadline("Do 2103T", DATE_TIME);
        assertEquals(" ", deadline.getStatusIcon());
    }

    @Test
    void newDeadline_toDataString_hasDoneFlagZero() {
        Deadline deadline = new Deadline("Do 2103T", DATE_TIME);
        assertEquals("D | 0 | Do 2103T | 2026-08-26T18:00", deadline.toDataString());
    }

    @Test
    void toString_includesDeadlineIconAndDescription() {
        Deadline deadline = new Deadline("Do 2103T", DATE_TIME);
        assertEquals("[D][ ] Do 2103T (by: Aug 26 2026, 18:00)", deadline.toString());
    }

    @Test
    void toString_showsDateOnlyWhenDeadlineIsMidnight() {
        Deadline deadline = new Deadline("Do 2103T", LocalDateTime.of(2026, 8, 26, 0, 0));
        assertEquals("[D][ ] Do 2103T (by: Aug 26 2026)", deadline.toString());
    }

    @Test
    void markAsDone_updatesStatusIconToX() {
        Deadline deadline = new Deadline("Do 2103T", DATE_TIME);
        deadline.markAsDone();
        assertEquals("X", deadline.getStatusIcon());
    }

    @Test
    void markAsDone_updatesToStringAndDataString() {
        Deadline deadline = new Deadline("Do 2103T", DATE_TIME);
        deadline.markAsDone();
        assertEquals("[D][X] Do 2103T (by: Aug 26 2026, 18:00)", deadline.toString());
        assertEquals("D | 1 | Do 2103T | 2026-08-26T18:00", deadline.toDataString());
    }

    @Test
    void markAsNotDone_afterBeingDone_revertsStatus() {
        Deadline deadline = new Deadline("Do 2103T", DATE_TIME);
        deadline.markAsDone();
        deadline.markAsNotDone();
        assertEquals(" ", deadline.getStatusIcon());
        assertEquals("[D][ ] Do 2103T (by: Aug 26 2026, 18:00)", deadline.toString());
    }
}
