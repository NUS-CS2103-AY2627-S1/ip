package zhangwei.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline}. The interesting part is the date: it is stored as a
 * {@link LocalDate} but shown to the user as "MMM d yyyy", so these tests pin
 * down that translation.
 */
public class DeadlineTest {

    @Test
    public void getBy_deadlineCreatedWithDate_sameDateReturned() {
        LocalDate by = LocalDate.of(2019, 12, 2);
        assertEquals(by, new Deadline("return book", by).getBy());
    }

    @Test
    public void toString_notDoneDeadline_dateShownInDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    public void toString_doneDeadline_crossedBoxShown() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    public void toString_singleDigitDay_dayNotPadded() {
        Deadline deadline = new Deadline("pay fees", LocalDate.of(2020, 1, 5));
        assertEquals("[D][ ] pay fees (by: Jan 5 2020)", deadline.toString());
    }

    @Test
    public void toString_doubleDigitDay_dayShownInFull() {
        Deadline deadline = new Deadline("pay fees", LocalDate.of(2020, 10, 31));
        assertEquals("[D][ ] pay fees (by: Oct 31 2020)", deadline.toString());
    }

    @Test
    public void toString_leapDay_dateShownCorrectly() {
        Deadline deadline = new Deadline("leap task", LocalDate.of(2020, 2, 29));
        assertEquals("[D][ ] leap task (by: Feb 29 2020)", deadline.toString());
    }
}
