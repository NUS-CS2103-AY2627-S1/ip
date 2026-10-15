package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DeadlineTest {

    @Test
    void toString_deadlineAtMidnight_noTimeShown() {
        Deadline d = new Deadline("submit report", LocalDateTime.of(2023, 8, 31, 0, 0));
        assertEquals("[D][ ] submit report (by: Aug 31 2023)", d.toString());
    }

    @Test
    void toString_deadlineWithTime_timeShown() {
        Deadline d = new Deadline("project meeting", LocalDateTime.of(2023, 8, 31, 14, 5));
        assertEquals("[D][ ] project meeting (by: Aug 31 2023 14:05)", d.toString());
    }

    @Test
    void markAsDone_markingSetsDone_statusIconShowsX() {
        Deadline d = new Deadline("finish", LocalDateTime.of(2023, 8, 31, 0, 0));
        d.markAsDone();
        assertEquals("[D][X] finish (by: Aug 31 2023)", d.toString());
    }
}
