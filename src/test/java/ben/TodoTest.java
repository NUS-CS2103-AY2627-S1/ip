package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests for {@link Todo}: the plain task type with no extra fields. */
class TodoTest {
    @Test
    void toString_showsTypeIconAndDescriptionOnly() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    void serialize_hasNoExtraFields() {
        assertEquals("T | 0 | read book", new Todo("read book").serialize());
    }
}
