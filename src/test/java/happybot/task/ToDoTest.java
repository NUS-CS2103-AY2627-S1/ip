package happybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ToDoTest {
    @Test
    public void toString_notDoneTodo_typeAndStatusShown() {
        assertEquals("[T][ ] read book", new ToDo("read book").toString());
    }

    @Test
    public void toString_doneTodo_statusBoxHoldsCross() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }
}
