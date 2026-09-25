package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import exception.FriedbergException;

/**
 * Tests for the {@link Deadline} class.
 */
class DeadlineTest {
    @Test
    void deadline_validDate_returnsCorrectData() throws FriedbergException {
        Deadline task = new Deadline("submit ip", "2026-09-09");

        assertEquals("submit ip", task.getName());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals("submit ip,P,D,2026-09-09", task.serialize());
        assertEquals("[D][ ] submit ip (by: 09/09/2026 (Wed))", task.renderTask());
    }

    @Test
    void deadline_doneStatus_returnsCorrectData() throws FriedbergException {
        String[] tokens = {"2026-09-09"};
        Deadline task = new Deadline("submit ip", TaskStatus.DONE, tokens);

        assertEquals("submit ip", task.getName());
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("submit ip,D,D,2026-09-09", task.serialize());
        assertEquals("[D][X] submit ip (by: 09/09/2026 (Wed))", task.renderTask());
    }

    @Test
    void deadline_invalidDate_throwsFriedbergException() {
        assertThrows(FriedbergException.class, () -> new Deadline("submit ip", "09-09-2026"));
    }

    @Test
    void deadline_nonexistentDate_throwsFriedbergException() {
        assertThrows(FriedbergException.class, () -> new Deadline("submit ip", "2026-02-30"));
    }

    @Test
    void deadline_invalidDeserializationConstructor_throwsFriedbergException() {
        String[] tokens = {"2026-09-09", "extra token"};

        assertThrows(FriedbergException.class, () -> new Deadline("submit ip", TaskStatus.DONE, tokens));
    }
}
