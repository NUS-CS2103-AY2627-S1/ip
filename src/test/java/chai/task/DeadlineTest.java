package chai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests deadline display formatting and completion state. */
public class DeadlineTest {

    @Test
    public void constructor_nullDate_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }

    @Test
    public void toString_newDeadline_formatsDateAndIncompleteStatus() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
    }

    @Test
    public void toString_afterMarkAndUnmark_reflectsCurrentStatus() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2020, 2, 29));

        deadline.markAsDone();
        assertEquals("[D][X] submit report (by: Feb 29 2020)", deadline.toString());

        deadline.markAsUndone();
        assertEquals("[D][ ] submit report (by: Feb 29 2020)", deadline.toString());
    }
}
