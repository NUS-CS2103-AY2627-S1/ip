package eve.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests {@link ToDo}'s own display and save-format text, on top of {@link Task}'s (see {@link TaskTest}). */
public class ToDoTest {
    @Test
    public void toString_prefixedWithTTypeIcon() {
        assertEquals("[T][ ] read book", new ToDo("read book").toString());
    }

    @Test
    public void toSaveFormat_startsWithTAndDoneFlag() {
        ToDo task = new ToDo("read book");
        task.markAsDone();

        assertEquals("T | 1 | read book", task.toSaveFormat());
    }
}
