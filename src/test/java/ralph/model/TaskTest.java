package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for Task behaviour.
 */
class TaskTest {

    @Test
    void markAsDone_afterCalling_setsIsDoneTrue() {
        Task t = new Task("do homework");
        t.markAsDone();
        assertTrue(t.isDone());
    }

    @Test
    void markAsNotDone_afterCalling_setsIsDoneFalse() {
        Task t = new Task("wash dishes");
        t.markAsDone();
        t.markAsNotDone();
        assertFalse(t.isDone());
    }

    @Test
    void toString_initialAndAfterMarking_returnsCorrectRepresentation() {
        Task t = new Task("read");
        assertEquals("[ ] read", t.toString());
        t.markAsDone();
        assertEquals("[X] read", t.toString());
    }

    @Test
    void getStatusIcon_whenNotDone_returnsSpace() {
        Task t = new Task("xyz");
        assertEquals(" ", t.getStatusIcon());
    }
}
