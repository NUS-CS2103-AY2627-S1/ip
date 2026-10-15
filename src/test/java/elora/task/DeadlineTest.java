package elora.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {

    @Test
    void toString_formatsDateInDisplayFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void toSaveFormat_producesPipeDelimitedIsoDate() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void toSaveFormat_doneDeadline_recordsOneFlag() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    void isOccurringOn_matchingDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        assertTrue(deadline.isOccurringOn(LocalDate.parse("2019-10-15")));
    }

    @Test
    void isOccurringOn_differentDate_returnsFalse() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        assertFalse(deadline.isOccurringOn(LocalDate.parse("2019-10-16")));
    }

    @Test
    void getBy_returnsOriginalDate() {
        LocalDate date = LocalDate.parse("2019-10-15");
        Deadline deadline = new Deadline("return book", date);
        assertEquals(date, deadline.getBy());
    }

    @Test
    void getSortDate_returnsSameAsGetBy() {
        LocalDate date = LocalDate.parse("2019-10-15");
        Deadline deadline = new Deadline("return book", date);
        assertEquals(date, deadline.getSortDate());
    }

    @Test
    void equals_sameDescriptionAndDate_returnsTrue() {
        assertEquals(new Deadline("return book", LocalDate.parse("2019-10-15")),
                new Deadline("return book", LocalDate.parse("2019-10-15")));
    }

    @Test
    void equals_differentDate_returnsFalse() {
        assertNotEquals(new Deadline("return book", LocalDate.parse("2019-10-15")),
                new Deadline("return book", LocalDate.parse("2019-10-16")));
    }

    @Test
    void equals_differentDescription_returnsFalse() {
        assertNotEquals(new Deadline("return book", LocalDate.parse("2019-10-15")),
                new Deadline("submit report", LocalDate.parse("2019-10-15")));
    }

    @Test
    void equals_notADeadline_returnsFalse() {
        assertNotEquals(new Deadline("return book", LocalDate.parse("2019-10-15")),
                new Todo("return book"));
    }
}
