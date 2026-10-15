package surveyprogram.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests behavior specific to undated todo tasks. */
class TodoTest {
    @Test
    void toString_completionState_formatsTodoMarker() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());

        todo.setDone(true);
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void inRange_anyDate_returnsFalse() {
        assertFalse(new Todo("read book").inRange(LocalDate.of(2026, 8, 24)));
    }
}
