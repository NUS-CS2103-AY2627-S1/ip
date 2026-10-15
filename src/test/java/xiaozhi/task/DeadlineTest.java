package xiaozhi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline}'s own behavior on top of the shared {@link Task}
 * behavior already covered by {@link TodoTest}: its type icon, and how its
 * date is woven into {@link Deadline#toString} and {@link Deadline#toSaveFormat}.
 */
public class DeadlineTest {

    @Test
    public void getTypeIcon_returnsD() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));

        assertEquals("D", deadline.getTypeIcon());
    }

    @Test
    public void toString_notDone_showsFriendlyDateInByClause() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));

        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadline.toString());
    }

    @Test
    public void toString_done_showsXStatusIcon() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Jun 06 2019)", deadline.toString());
    }

    @Test
    public void toSaveFormat_appendsIsoDateAfterBaseFields() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));

        assertEquals("D | 0 | return book | 2019-06-06", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_usesOneForDoneFlag() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));
        deadline.markAsDone();

        assertEquals("D | 1 | return book | 2019-06-06", deadline.toSaveFormat());
    }

    @Test
    public void equals_sameDescriptionAndByDate_isEqualRegardlessOfDoneStatus() {
        Deadline first = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Deadline second = new Deadline("return book", LocalDate.of(2019, 6, 6));
        second.markAsDone();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_sameDescriptionDifferentByDate_isNotEqual() {
        Deadline first = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Deadline second = new Deadline("return book", LocalDate.of(2019, 6, 7));

        assertFalse(first.equals(second));
    }

    @Test
    public void equals_sameDescriptionAsATodo_isNotEqual() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6));

        assertFalse(deadline.equals(new Todo("return book")));
    }
}
