package ace.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_incompleteTask_formatsWithUncheckedBox() {
        Todo todo = new Todo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void toString_completedTask_formatsWithCheckedBox() {
        Todo todo = new Todo("borrow book");
        todo.completeTask();
        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    public void loadFormat_matchesToString() {
        Todo todo = new Todo("borrow book");
        assertEquals(todo.toString(), todo.loadFormat());
    }
}
