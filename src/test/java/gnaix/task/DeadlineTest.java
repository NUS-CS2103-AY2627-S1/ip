package gnaix.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {

    @Test
    void occursOn_matchingDate_trueReturned() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDate.of(2026, 9, 1));

        assertTrue(deadline.occursOn(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void occursOn_differentDate_falseReturned() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDate.of(2026, 9, 1));

        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    void toString_deadlineTask_dateDisplayed() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDate.of(2026, 9, 1));

        assertEquals(
                "[D][ ] submit assignment (by: Sep 01 2026)",
                deadline.toString());
    }

    @Test
    void toString_taggedCompletedDeadline_statusDateAndTagsDisplayed() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDate.of(2026, 9, 1));
        deadline.addTag("#school");
        deadline.markAsComplete();

        assertEquals(
                "[D][X] submit assignment #school (by: Sep 01 2026)",
                deadline.toString());
    }
}
