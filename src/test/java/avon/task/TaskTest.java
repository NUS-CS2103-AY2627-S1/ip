package avon.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void constructor_nullDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
    }

    @Test
    void constructor_blankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(" \t "));
    }
}
