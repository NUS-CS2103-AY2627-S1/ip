package chim.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toFileFormat_notDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        assertEquals("D | 0 | none | return book | 2019-12-01", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_markedDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        deadline.markAsDone();
        assertEquals("D | 1 | none | return book | 2019-12-01", deadline.toFileFormat());
    }

    @Test
    public void toString_notDone_correctDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        assertEquals("[D][ ] return book (by: Dec 01 2019)", deadline.toString());
    }

    @Test
    public void toString_markedDone_correctDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Dec 01 2019)", deadline.toString());
    }

    @Test
    public void toString_singleDigitDay_zeroPadded() {
        Deadline deadline = new Deadline("submit form", LocalDate.of(2020, 1, 5));
        assertEquals("[D][ ] submit form (by: Jan 05 2020)", deadline.toString());
    }
}
