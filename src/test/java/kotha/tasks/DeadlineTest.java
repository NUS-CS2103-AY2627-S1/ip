package kotha.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import kotha.KothaException;

class DeadlineTest {

    @Test
    void createDeadline_createsTaskWithCorrectDescriptionAndFrom() throws KothaException {
        @SuppressWarnings("unchecked")
        Deadline testTask = (Deadline) Task.createDeadline("deadline clean up my table /by 16/05");
        assertEquals("clean up my table", testTask.getDescription());
        assertEquals("[ ]", testTask.getStatusIcon());
        assertFalse(testTask.isDone());
        assertEquals(LocalDateTime.of(2026, 5, 16, 23, 59), testTask.getDeadline());
    }
}
