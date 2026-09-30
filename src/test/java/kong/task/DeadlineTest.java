package kong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    @Test
    void occursOn_matchingDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));

        assertTrue(deadline.occursOn(LocalDate.parse("2019-10-15")));
        assertFalse(deadline.occursOn(LocalDate.parse("2019-10-16")));
    }

    @Test
    void getSortDate_returnsDueDate() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));

        assertEquals(Optional.of(LocalDate.parse("2019-10-15")), deadline.getSortDate());
    }

    @Test
    void isSameTask_sameDescriptionAndDate_returnsTrueIgnoringCase() {
        Deadline first = new Deadline("return book", LocalDate.parse("2019-10-15"));
        Deadline second = new Deadline("RETURN BOOK", true, LocalDate.parse("2019-10-15"));
        Deadline differentDate = new Deadline("return book", LocalDate.parse("2019-10-16"));
        Deadline differentDesc = new Deadline("submit report", LocalDate.parse("2019-10-15"));

        assertTrue(first.isSameTask(second));
        assertFalse(first.isSameTask(differentDate));
        assertFalse(first.isSameTask(differentDesc));
        assertFalse(first.isSameTask(null));
        assertFalse(first.isSameTask(new Todo("return book")));
    }

    @Test
    void equalsAndHashCode_contract_validatesEquality() {
        Deadline deadline1 = new Deadline("return book", false, LocalDate.parse("2019-10-15"));
        Deadline deadline2 = new Deadline("return book", false, LocalDate.parse("2019-10-15"));
        Deadline deadlineDone = new Deadline("return book", true, LocalDate.parse("2019-10-15"));
        Deadline deadlineDiffDate = new Deadline("return book", false, LocalDate.parse("2019-10-16"));

        assertEquals(deadline1, deadline1);
        assertEquals(deadline1, deadline2);
        assertEquals(deadline1.hashCode(), deadline2.hashCode());

        assertNotEquals(deadline1, deadlineDone);
        assertNotEquals(deadline1, deadlineDiffDate);
        assertNotEquals(deadline1, null);
        assertNotEquals(deadline1, "other");
    }

    @Test
    void toStringAndToFileString_validFormats() {
        Deadline incomplete = new Deadline("return book", false, LocalDate.parse("2019-10-15"));
        Deadline complete = new Deadline("return book", true, LocalDate.parse("2019-10-15"));

        assertEquals("[D][ ] return book (by: Oct 15 2019)", incomplete.toString());
        assertEquals("[D][X] return book (by: Oct 15 2019)", complete.toString());
        assertEquals("D | 0 | return book | 2019-10-15", incomplete.toFileString());
        assertEquals("D | 1 | return book | 2019-10-15", complete.toFileString());
    }
}
