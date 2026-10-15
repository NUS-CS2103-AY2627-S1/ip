package taterror.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Todo}.
 */
public class TodoTest {

    @Test
    public void toString_notDoneNoPriority_rendersTypeAndDescriptionOnly() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_doneWithPriority_includesStatusAndPriority() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.setPriority(Priority.HIGH);
        assertEquals("[T][X] read book (priority: HIGH)", todo.toString());
    }

    @Test
    public void toSaveDetail_isAlwaysEmpty() {
        assertEquals("", new Todo("read book").toSaveDetail());
    }
}
