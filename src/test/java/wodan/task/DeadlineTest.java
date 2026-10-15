package wodan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import wodan.WodanException;

/**
 * Unit tests for {@link Deadline} date matching and encoding.
 */
public class DeadlineTest {
    @Test
    public void occursOn_dueDate_trueOnlyOnThatDay() throws WodanException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void toStringAndStorage_unmarkedDeadline_includesByDate() throws WodanException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-10-15 | general", deadline.toStorageString());
    }

    @Test
    public void getDueAtAndHasSameDetails_sameWhenAndDescription() throws WodanException {
        Deadline first = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));
        Deadline second = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));
        Deadline otherWhen = new Deadline("return book", TaskDateTime.parse("2019-12-03"));
        assertEquals(LocalDate.of(2019, 12, 2), first.getDueAt().toLocalDate());
        assertTrue(first.hasSameDetails(second));
        assertFalse(first.hasSameDetails(otherWhen));
        assertFalse(first.hasSameDetails(new Todo("return book")));
    }

    @Test
    public void toStorageString_markedDeadline_oneFlag() throws WodanException {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
        assertEquals("D | 1 | return book | 2019-10-15 | general", deadline.toStorageString());
    }
}
