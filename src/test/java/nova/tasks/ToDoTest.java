package nova.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void toString_notDoneTodo_returnsFormattedTodo() {
        ToDo todo = new ToDo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileString_notDoneTodo_returnsFileFormat() {
        ToDo todo = new ToDo("read book");

        assertEquals("T | O | read book", todo.toFileString());
    }

    @Test
    public void toFileString_doneTodo_returnsDoneFileFormat() {
        ToDo todo = new ToDo("read book");
        todo.markDone();

        assertEquals("T | X | read book", todo.toFileString());
    }
}