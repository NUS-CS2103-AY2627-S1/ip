package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toFileString_newDeadline_formattedCorrectly() {
        LocalDateTime by = LocalDateTime.of(2026, 9, 10, 23, 59);
        Deadline deadline = new Deadline("submit assignment", by);
        assertEquals("D | 0 | submit assignment | 2026-09-10 2359", deadline.toFileString());
    }

    @Test
    public void toString_doneDeadline_formattedCorrectly() {
        LocalDateTime by = LocalDateTime.of(2026, 9, 10, 23, 59);
        Deadline deadline = new Deadline("submit assignment", by);
        deadline.markAsDone();
        assertEquals("[D][X] submit assignment (by: Sep 10 2026 2359)", deadline.toString());
    }

    @Test
    public void getBy_returnsCorrectDateTime() {
        LocalDateTime by = LocalDateTime.of(2026, 9, 10, 23, 59);
        Deadline deadline = new Deadline("submit assignment", by);
        assertEquals(by, deadline.getBy());
    }

    @Test
    public void equalsAndHashCode_sameValues_equal() {
        LocalDateTime by = LocalDateTime.of(2026, 9, 10, 23, 59);
        Deadline d1 = new Deadline("submit assignment", by);
        Deadline d2 = new Deadline("SUBMIT ASSIGNMENT", by);
        Deadline d3 = new Deadline("other assignment", by);
        Deadline d4 = new Deadline("submit assignment", by.plusDays(1));

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());
        assertNotEquals(d1, d3);
        assertNotEquals(d1, d4);
    }
}
