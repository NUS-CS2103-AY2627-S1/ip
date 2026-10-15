package yapbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_notDone_returnsTypeIconAndTaskString() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ]read book", todo.toString());
    }

    @Test
    public void toString_done_showsXMark() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X]read book", todo.toString());
    }

    @Test
    public void toFileFormat_returnsTTypeLine() {
        Todo todo = new Todo("read book");

        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_prioritySet_appendsPriorityAsLastField() {
        Todo todo = new Todo("read book");
        todo.setPriority(Priority.HIGH);

        assertEquals("T | 0 | read book | HIGH", todo.toFileFormat());
    }

    @Test
    public void toString_prioritySet_showsPriorityTagAfterStatusBox() {
        Todo todo = new Todo("read book");
        todo.setPriority(Priority.HIGH);

        assertEquals("[T][ ][HIGH]read book", todo.toString());
    }
}
