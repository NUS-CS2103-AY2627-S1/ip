package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for Todo-specific behaviour.
 */
class TodoTest {

    @Test
    void toString_todoToString_includesTPrefixAndDescription() {
        Todo td = new Todo("buy milk");
        assertEquals("[T][ ] buy milk", td.toString());
        td.markAsDone();
        assertEquals("[T][X] buy milk", td.toString());
    }
}
