package charliek.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the starter task set presented to new users.
 */
class SampleDataTest {
    /**
     * Verifies that starter data contains seven varied tasks and one completed example.
     */
    @Test
    void create_returnsSevenVariedTasks() {
        List<Task> sampleTasks = SampleData.create();

        assertEquals(7, sampleTasks.size());
        assertInstanceOf(ToDo.class, sampleTasks.get(0));
        assertInstanceOf(Deadline.class, sampleTasks.get(2));
        assertInstanceOf(Event.class, sampleTasks.get(4));
        assertTrue(sampleTasks.get(0).isDone());
    }
}
