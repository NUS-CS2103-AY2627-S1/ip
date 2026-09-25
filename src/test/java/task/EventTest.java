package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import exception.FriedbergException;

/**
 * Tests for the {@link Event} class.
 */
class EventTest {
    @Test
    void event_validDates_returnsCorrectData() throws FriedbergException {
        Event task = new Event("project meeting", "2026-09-09", "2026-09-10");

        assertEquals("project meeting", task.getName());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals("project meeting,P,E,2026-09-09,2026-09-10", task.serialize());
        assertEquals("[E][ ] project meeting (from: 09/09/2026 (Wed) to: 10/09/2026 (Thu))",
                task.renderTask());
    }

    @Test
    void event_doneStatus_returnsCorrectData() throws FriedbergException {
        String[] tokens = { "2026-09-09", "2026-09-10" };
        Event task = new Event("project meeting", TaskStatus.DONE, tokens);

        assertEquals("project meeting", task.getName());
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals("project meeting,D,E,2026-09-09,2026-09-10", task.serialize());
        assertEquals("[E][X] project meeting (from: 09/09/2026 (Wed) to: 10/09/2026 (Thu))",
                task.renderTask());
    }

    @Test
    void event_invalidStartDate_throwsFriedbergException() {
        assertThrows(FriedbergException.class, () -> new Event("project meeting", "2026/09/09", "2026-09-10"));
    }

    @Test
    void event_invalidEndDate_throwsFriedbergException() {
        assertThrows(FriedbergException.class, () -> new Event("project meeting", "2026-09-09", "10-09-2026"));
    }

    @Test
    void event_nonexistentDate_throwsFriedbergException() {
        assertThrows(
                FriedbergException.class, () -> new Event("project meeting", "2026-02-30", "2026-03-01"));
    }

    @Test
    void event_startAfterEnd_throwsFriedbergException() {
        assertThrows(
                FriedbergException.class, () -> new Event("project meeting", "2026-09-11", "2026-09-10"));
    }

    @Test
    void event_equalDates_throwsFriedbergException() {
        assertThrows(
                FriedbergException.class, () -> new Event("project meeting", "2026-09-10", "2026-09-10"));
    }

    @Test
    void event_invalidDeserializationConstructor_throwsFriedbergException() {
        String[] incompleteTokens = { "2026-09-09" };
        String[] unorderedTokens = { "2026-09-11", "2026-09-10" };

        assertThrows(
                FriedbergException.class, () -> new Event("project meeting", TaskStatus.DONE, incompleteTokens));
        assertThrows(
                FriedbergException.class, () -> new Event("project meeting", TaskStatus.DONE, unorderedTokens));
    }
}
