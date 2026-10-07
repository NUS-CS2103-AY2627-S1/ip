package mary.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Verifies typed deadline formatting and ISO persistence.
 */
class DeadlineTest {
    /**
     * Tests to string: and storage record; formats date without losing status.
     */
    @Test
    void toString_andStorageRecord_formatsDateWithoutLosingStatus() {
        Deadline task = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        assertEquals("[D][ ] return book (by: 2 Dec 2019 18:00)", task.toString());
        task.markAsDone();
        assertEquals("[D][X] return book (by: 2 Dec 2019 18:00)", task.toString());
        assertEquals("D | 1 | return book | 2019-12-02T18:00", task.toStorageRecord());
    }
}
