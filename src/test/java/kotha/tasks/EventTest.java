package kotha.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import kotha.KothaException;

class EventTest {

    @Test
    void createEvent_createsTaskWithCorrectDescriptionFromAndTo() throws KothaException {
        Event testTask = (Event) Task.createEvent("event clean up my table /from 16/5 /to 12/11");
        assertEquals("clean up my table", testTask.getDescription());
        assertEquals("[ ]", testTask.getStatusIcon());
        assertFalse(testTask.isDone());
        assertEquals(LocalDateTime.of(2026, 5, 16, 23, 59), testTask.getFrom());
        assertEquals(LocalDateTime.of(2026, 11, 12, 23, 59), testTask.getTo());
    }

}
