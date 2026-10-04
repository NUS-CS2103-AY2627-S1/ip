package shrek.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toString_notDone_showsFormattedDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_done_showsCheckedBoxAndFormattedDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toSaveFormat_containsIsoDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        String saved = deadline.toSaveFormat();
        assertTrue(saved.contains("2019-10-15"));
        assertEquals("D | 0 | return book | 2019-10-15", saved);
    }
}
