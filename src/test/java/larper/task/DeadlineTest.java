package larper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toStringAndFileString_deadlineWithTime_expectedFormat() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2019, 10, 15), "1400");

        assertEquals("D", deadline.getTypeIcon());
        assertEquals("[D][ ] submit report (by: Oct 15 2019 1400)", deadline.toString());
        assertEquals("D | 0 | submit report | 2019-10-15 | 1400", deadline.toFileString());
    }

    @Test
    public void toStringAndFileString_deadlineWithoutTime_expectedFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 23), "no time");

        assertEquals("[D][ ] return book (by: Aug 23 2026)", deadline.toString());
        assertEquals("D | 0 | return book | 2026-08-23 | no time", deadline.toFileString());
    }

    @Test
    public void stringConstructor_isoDate_expectedFormat() {
        Deadline deadline = new Deadline("submit report", "2019-10-15", "1400");

        assertEquals("[D][ ] submit report (by: Oct 15 2019 1400)", deadline.toString());
        assertEquals("D | 0 | submit report | 2019-10-15 | 1400", deadline.toFileString());
    }
}
