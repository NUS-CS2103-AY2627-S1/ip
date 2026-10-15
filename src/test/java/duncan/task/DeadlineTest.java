package duncan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toFileFormat_notDone_correctFileFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertEquals("D\t0\treturn book\t2019-12-02", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_markedDone_correctFileFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.markAsDone();

        assertEquals("D\t1\treturn book\t2019-12-02", deadline.toFileFormat());
    }

    @Test
    public void toString_notDone_correctDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    public void toString_markedDone_correctDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    public void reschedule_markedDone_dateChangedAndStillDone() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.markAsDone();

        deadline.reschedule(LocalDate.of(2019, 12, 9));

        assertEquals("[D][X] return book (by: Dec 9 2019)", deadline.toString());
    }

    @Test
    public void snooze_acrossMonthEnd_dateMovedByDays() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 30));

        deadline.snooze(3);

        assertEquals("D\t0\treturn book\t2020-01-02", deadline.toFileFormat());
    }

    @Test
    public void snooze_markedDone_stillDone() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.markAsDone();

        deadline.snooze(7);

        assertEquals("[D][X] return book (by: Dec 9 2019)", deadline.toString());
    }

    @Test
    public void snooze_pastLatestSupportedDate_exceptionThrownAndDateUnchanged() {
        Deadline deadline = new Deadline("return book", LocalDate.MAX);

        assertThrows(DateTimeException.class, () -> deadline.snooze(1));

        assertEquals("D\t0\treturn book\t" + LocalDate.MAX, deadline.toFileFormat());
    }

    @Test
    public void isDuplicateOf_sameDescriptionIgnoringCaseAndSameDate_true() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertTrue(deadline.isDuplicateOf(new Deadline("Return  BOOK", LocalDate.of(2019, 12, 2))));
    }

    @Test
    public void isDuplicateOf_differentDate_false() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertFalse(deadline.isDuplicateOf(new Deadline("return book", LocalDate.of(2019, 12, 3))));
    }
}
