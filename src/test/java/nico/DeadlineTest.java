package nico;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    @Test
    void getDueTime_newDeadline_dueTimeReturned() {
        LocalDateTime dueTime = LocalDateTime.of(2026, 8, 25, 19, 0);
        Deadline deadline = new Deadline("return book", dueTime);

        assertEquals(dueTime, deadline.getDueTime());
    }

    @Test
    void toString_newDeadline_formattedAsIncompleteDeadline() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 8, 25, 19, 0));

        assertEquals("[D][ ] return book (by: Aug 25 2026 19:00)", deadline.toString());
    }

    @Test
    void toString_completedDeadline_formattedAsCompletedDeadline() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 8, 25, 19, 0));
        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Aug 25 2026 19:00)", deadline.toString());
    }
}
