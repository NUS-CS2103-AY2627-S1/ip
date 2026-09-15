package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toDataString_notDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 25));
        assertEquals("D | 0 | return book | 2026-08-25", deadline.toDataString());
    }

    @Test
    public void toDataString_markedDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 25));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2026-08-25", deadline.toDataString());
    }

    @Test
    public void toString_notDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 25));
        assertEquals("[D] [ ] return book (by: 2026-08-25)", deadline.toString());
    }

    @Test
    public void toString_markedDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 25));
        deadline.markAsDone();
        assertEquals("[D] [X] return book (by: 2026-08-25)", deadline.toString());
    }

    @Test
    public void hasSameDetails_sameDescriptionAndDateIgnoringCase_returnsTrue() {
        Deadline first = new Deadline("Return Book", LocalDate.of(2026, 8, 25));
        Deadline second = new Deadline("return book", LocalDate.of(2026, 8, 25));
        second.markAsDone();

        assertTrue(first.hasSameDetails(second));
    }

    @Test
    public void hasSameDetails_differentDate_returnsFalse() {
        Deadline first = new Deadline("return book", LocalDate.of(2026, 8, 25));
        Deadline second = new Deadline("return book", LocalDate.of(2026, 8, 26));

        assertFalse(first.hasSameDetails(second));
    }

    @Test
    public void hasSameDetails_differentDescriptionOrType_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 8, 25));

        assertFalse(deadline.hasSameDetails(new Deadline("borrow book", LocalDate.of(2026, 8, 25))));
        assertFalse(deadline.hasSameDetails(new ToDo("return book")));
    }
}
