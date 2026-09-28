package baron.core.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Todo}.
 */
class TodoTest {

    /**
     * Verifies that a supplied identifier is retained in the task and its persistent
     * representation.
     */
    @Test
    void constructor_identifierProvided_identifierRetained() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Todo todo = new Todo(uuid, "buy milk");

        assertEquals(uuid, todo.getUuid());
        assertEquals(
                "00000000-0000-0000-0000-000000000001 | T | 0 | buy milk | ",
                todo.toFileString());
        assertEquals("[T][ ] buy milk", todo.toString());
    }

    /**
     * Verifies that a task created without an identifier receives a valid UUID.
     */
    @Test
    void constructor_identifierOmitted_validUuidGenerated() {
        Todo todo = new Todo("buy milk");

        assertDoesNotThrow(() -> UUID.fromString(todo.getUuid().toString()));
    }
}
