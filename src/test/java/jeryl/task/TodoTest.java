package jeryl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toString_newTodo_showsUncheckedBox() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_markedDoneTodo_showsCheckedBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toFileString_newTodo_encodesTypeAndNotDoneFlag() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void toFileString_markedDoneTodo_encodesDoneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileString());
    }

    @Test
    public void toString_highPriority_showsPriorityTagBeforeTypeTag() {
        Todo todo = new Todo("read book", Priority.HIGH);
        assertEquals("[H][T][ ] read book", todo.toString());
    }

    @Test
    public void toString_noPriority_showsNoPriorityTag() {
        Todo todo = new Todo("read book", Priority.NONE);
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileString_withPriority_appendsPriorityAsFinalField() {
        Todo todo = new Todo("read book", Priority.LOW);
        assertEquals("T | 0 | read book | LOW", todo.toFileString());
    }

    @Test
    public void toFileString_noPriority_omitsPriorityField() {
        Todo todo = new Todo("read book", Priority.NONE);
        assertEquals("T | 0 | read book", todo.toFileString());
    }
}
