package nelson.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests ToDo formatting and inherited completion behavior. */
public class TodoTest {
    @Test
    public void newTodo_formatsDisplayAndStorageValues() {
        Todo todo = new Todo("study openings");

        assertEquals("[T][ ] study openings", todo.toString());
        assertEquals("T | 0 | study openings", todo.toStorageString());
    }

    @Test
    public void markAndUnmarkTodo_updatesStatus() {
        Todo todo = new Todo("study openings");

        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());

        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
    }
}
