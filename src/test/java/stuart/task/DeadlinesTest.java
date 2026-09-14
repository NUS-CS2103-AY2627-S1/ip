package stuart.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class DeadlinesTest {

    @Test
    public void toString_includesFormattedByDate() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2019, 10, 15));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", task.toString());
    }

    @Test
    public void toSaveFormat_includesRawByDate() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2019, 10, 15));
        assertEquals("D | 0 | return book | 2019-10-15", task.toSaveFormat());
    }

    @Test
    public void occursOn_dateEqualsBy_returnsTrue() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2019, 10, 15));
        assertTrue(task.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_dateNotEqualsBy_returnsFalse() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2019, 10, 15));
        assertFalse(task.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void getSortDate_returnsByDate() {
        LocalDate by = LocalDate.of(2019, 10, 15);
        Deadlines task = new Deadlines("return book", by);
        assertEquals(Optional.of(by), task.getSortDate());
    }

    @Test
    public void isOverdue_pastByNotDone_returnsTrue() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2000, 1, 1));
        assertTrue(task.isOverdue());
    }

    @Test
    public void isOverdue_pastByButDone_returnsFalse() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2000, 1, 1));
        task.markAsDone();
        assertFalse(task.isOverdue());
    }

    @Test
    public void isOverdue_futureBy_returnsFalse() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2999, 1, 1));
        assertFalse(task.isOverdue());
    }

    @Test
    public void isDueSoon_byWithinWindowNotDone_returnsTrue() {
        Deadlines task = new Deadlines("return book", LocalDate.now().plusDays(2));
        assertTrue(task.isDueSoon(3));
    }

    @Test
    public void isDueSoon_byAtWindowBoundary_returnsTrue() {
        Deadlines task = new Deadlines("return book", LocalDate.now().plusDays(3));
        assertTrue(task.isDueSoon(3));
    }

    @Test
    public void isDueSoon_byToday_returnsTrue() {
        Deadlines task = new Deadlines("return book", LocalDate.now());
        assertTrue(task.isDueSoon(3));
    }

    @Test
    public void isDueSoon_byBeyondWindow_returnsFalse() {
        Deadlines task = new Deadlines("return book", LocalDate.now().plusDays(4));
        assertFalse(task.isDueSoon(3));
    }

    @Test
    public void isDueSoon_byAlreadyPast_returnsFalse() {
        Deadlines task = new Deadlines("return book", LocalDate.now().minusDays(1));
        assertFalse(task.isDueSoon(3));
    }

    @Test
    public void isDueSoon_withinWindowButDone_returnsFalse() {
        Deadlines task = new Deadlines("return book", LocalDate.now().plusDays(2));
        task.markAsDone();
        assertFalse(task.isDueSoon(3));
    }

    @Test
    public void hasSameDetails_sameDescriptionSameBy_returnsTrue() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2019, 10, 15));
        Deadlines other = new Deadlines("return book", LocalDate.of(2019, 10, 15));
        assertTrue(task.hasSameDetails(other));
    }

    @Test
    public void hasSameDetails_sameDescriptionDifferentBy_returnsFalse() {
        Deadlines task = new Deadlines("return book", LocalDate.of(2019, 10, 15));
        Deadlines other = new Deadlines("return book", LocalDate.of(2019, 10, 16));
        assertFalse(task.hasSameDetails(other));
    }
}
