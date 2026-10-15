package lenzabot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void completionMethods_newTask_updatesStatusAndIcon() {
        Task task = new Todo("read book");

        assertFalse(task.isCompleted());
        assertEquals(" ", task.getStatusIcon());

        task.markAsCompleted();
        assertTrue(task.isCompleted());
        assertEquals("X", task.getStatusIcon());

        task.markAsIncomplete();
        assertFalse(task.isCompleted());
    }

    @Test
    void toSaveFormat_eachTaskType_returnsReloadableFields() {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveFormat());
        assertEquals(
                "D | 0 | return book | 2020-01-02T18:00",
                new Deadline("return book", LocalDateTime.of(2020, 1, 2, 18, 0)).toSaveFormat()
        );
        assertEquals(
                "E | 0 | meeting | 2020-01-02T14:00 | 2020-01-02T16:00",
                new Event(
                        "meeting",
                        LocalDateTime.of(2020, 1, 2, 14, 0),
                        LocalDateTime.of(2020, 1, 2, 16, 0)
                ).toSaveFormat()
        );
    }
}
