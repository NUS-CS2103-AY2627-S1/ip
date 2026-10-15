package happybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {
    @Test
    public void toString_notDoneDeadline_typeStatusAndDueDateShown() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_doneDeadline_statusBoxHoldsCross() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Dec 02 2019 6:00PM)", deadline.toString());
    }

    @Test
    public void toString_deadlineAtMidnight_timeShownAsTwelveAm() {
        // A command written without a time stores midnight, which has to read sensibly.
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0));

        assertEquals("[D][ ] return book (by: Dec 02 2019 12:00AM)", deadline.toString());
    }

    @Test
    public void toString_deadlineAtNoon_timeShownAsTwelvePm() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 12, 0));

        assertEquals("[D][ ] return book (by: Dec 02 2019 12:00PM)", deadline.toString());
    }

    @Test
    public void toString_deadlineBeforeTen_hourNotPadded() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 9, 5));

        assertEquals("[D][ ] return book (by: Dec 02 2019 9:05AM)", deadline.toString());
    }

    @Test
    public void getDueDateTime_deadline_dueDateReturned() {
        LocalDateTime dueDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals(dueDateTime, new Deadline("return book", dueDateTime).getDueDateTime());
    }

    @Test
    public void constructor_nullDueDateTime_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("return book", null));
    }

    @Test
    public void hasSameDetails_descriptionAndDueDateMatch_tasksMatchRegardlessOfCompletion() {
        LocalDateTime dueDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", dueDateTime);
        Deadline matchingDeadline = new Deadline("RETURN BOOK", dueDateTime);
        matchingDeadline.markAsDone();

        assertTrue(deadline.hasSameDetails(matchingDeadline));
        assertFalse(deadline.hasSameDetails(new Deadline("return book", dueDateTime.plusMinutes(1))));
    }
}
