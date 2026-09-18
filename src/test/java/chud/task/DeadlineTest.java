package chud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    @Test
    void occursOn_sameDateAsBy_returnsTrue() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));

        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void occursOn_differentDateFromBy_returnsFalse() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));

        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    void occursOn_byHasTimeOfDay_matchesOnDateAloneIgnoringTime() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));

        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void toString_includesTagStatusDescriptionAndByDate() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));

        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    void toString_markedDone_showsXStatus() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    void toFileString_encodesTagDoneFlagDescriptionAndStorageDate() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2/12/2019 1800"));

        assertEquals("D | 0 | return book | 2019-12-02 1800", deadline.toFileString());
    }

    @Test
    void getSortDate_returnsByDate() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02 1800"));

        assertEquals(LocalDate.of(2019, 12, 2), deadline.getSortDate());
    }
}
