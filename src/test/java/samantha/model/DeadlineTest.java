package samantha.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import samantha.exception.InputException;
import samantha.exception.TaskValidationException;

class DeadlineTest {
    @Test
    void deadline_matchingDate_reportsDetailsAndSerializesStatus()
            throws TaskValidationException, InputException {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertEquals("[D]", deadline.getType());
        assertTrue(deadline.isOnDate(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.isOnDate(LocalDate.of(2019, 12, 3)));
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", deadline.toString());
        assertEquals("D | 0 | return book | 2/12/2019 1800", deadline.toFileString());

        deadline.markDone();
        assertEquals("D | 1 | return book | 2/12/2019 1800", deadline.toFileString());
    }
}
