package kiwi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Deadline}.
 */
class DeadlineTest {

    private Deadline deadline;

    @BeforeEach
    void setUp() {
        deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
    }

    @Test
    void occursOn_byDate_returnsTrue() {
        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    void occursOn_otherDate_returnsFalse() {
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    void toSaveFormat_includesIsoByDate() {
        assertEquals("D | 0 | return book | 2019-12-02", deadline.toSaveFormat());
    }

    @Test
    void toSaveFormat_whenDone_usesDoneFlag() {
        deadline.markAsDone();
        assertTrue(deadline.toSaveFormat().startsWith("D | 1 | "));
    }

    @Test
    void hasSameDetails_sameDescriptionAndDate_returnsTrue() {
        Deadline other = new Deadline("return book", LocalDate.of(2019, 12, 2));
        assertTrue(deadline.hasSameDetails(other));
    }

    @Test
    void hasSameDetails_differentDate_returnsFalse() {
        Deadline other = new Deadline("return book", LocalDate.of(2019, 12, 3));
        assertFalse(deadline.hasSameDetails(other));
    }
}
