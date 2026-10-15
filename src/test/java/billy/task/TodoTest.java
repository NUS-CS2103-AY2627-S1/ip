package billy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo}, a task with no date attached.
 *
 * <p>A todo adds only its {@code T} label to what {@link Task} already does, so
 * what is worth checking is that the label is added in the right place and that
 * the base behavior still shows through underneath it.
 */
public class TodoTest {

    @Test
    public void toString_newTodo_labelledAndNotDone() {
        assertEquals("[T][ ] borrow book", new Todo("borrow book").toString());
    }

    @Test
    public void toString_doneTodo_labelledAndTicked() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        // The type label comes before the status box, not after it.
        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    public void toSaveFormat_notDoneTodo_typeLetterThenBaseFields() {
        assertEquals("T | 0 | borrow book", new Todo("borrow book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneTodo_flagFollowsTheStatus() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("T | 1 | borrow book", todo.toSaveFormat());
    }

    @Test
    public void occursOn_anyDay_false() {
        // A todo has no date, so it inherits the answer from Task.
        assertFalse(new Todo("borrow book").occursOn(LocalDate.of(2019, 12, 2)));
    }
}
