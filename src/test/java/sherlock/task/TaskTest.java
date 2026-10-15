package sherlock.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the displayed state and saved form of tasks. */
class TaskTest {
    @Test
    void todo_startsIncompleteAndCanBeMarkedDone() {
        Task task = new Todo("review clues");

        assertEquals("[T][ ] review clues", task.toString());
        task.markAsDone();

        assertEquals("[T][X] review clues", task.toString());
        assertEquals("T | 1 | review clues", task.toFileString());
    }

    @Test
    void deadline_formatsDateForDisplayAndStorage() {
        Task task = new Deadline("return book", "2019-12-02");

        assertEquals("[D][ ] return book (by: Dec 2 2019)", task.toString());
        assertEquals("D | 0 | return book | 2019-12-02", task.toFileString());
    }
}
