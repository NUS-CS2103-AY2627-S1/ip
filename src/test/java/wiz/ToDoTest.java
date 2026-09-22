package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {

    @Test
    public void toFileString_newToDo_formattedCorrectly() {
        ToDo todo = new ToDo("read book");
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void toFileString_doneToDo_formattedCorrectly() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileString());
    }

    @Test
    public void toString_andMarking_formattedCorrectly() {
        ToDo todo = new ToDo("buy potions");
        assertEquals("[T][ ] buy potions", todo.toString());
        todo.markAsDone();
        assertEquals("[T][X] buy potions", todo.toString());
        todo.markAsNotDone();
        assertEquals("[T][ ] buy potions", todo.toString());
    }

    @Test
    public void equalsAndHashCode_sameDescription_equal() {
        ToDo todo1 = new ToDo("read book");
        ToDo todo2 = new ToDo("READ BOOK");
        ToDo todo3 = new ToDo("write scroll");

        assertEquals(todo1, todo2);
        assertEquals(todo1.hashCode(), todo2.hashCode());
        assertNotEquals(todo1, todo3);
    }
}
