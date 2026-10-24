package echo.task; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_newDeadline_showsFormattedDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadline.toString());
    }

    @Test
    public void toString_markedDone_showsDoneTag() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Jun 06 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_newDeadline_returnsPipeDelimitedLineWithIsoDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        assertEquals("D | 0 | return book | 2019-06-06", deadline.toFileFormat());
    }

    @Test
    public void occursOn_dueDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 6, 6)));
    }

    @Test
    public void occursOn_differentDate_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 6, 7)));
    }
}
