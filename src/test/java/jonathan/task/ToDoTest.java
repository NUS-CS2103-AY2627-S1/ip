package jonathan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import jonathan.JonathanException;

public class ToDoTest {

    @Test
    public void toString_validToDo_correctFormat() {
        ToDo todo = new ToDo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileString_validToDo_correctFormat() {
        ToDo todo = new ToDo("read book");
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void toFileString_markedToDo_correctFormat() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileString());
    }

    @Test
    public void priority_highPriority_correctDisplayAndFileFormat() {
        ToDo todo = new ToDo("read book");
        todo.setPriority(Priority.HIGH);
        assertEquals("[T][ ] read book (priority: high)", todo.toString());
        assertEquals("T | 0 | read book | 1", todo.toFileString());
    }

    @Test
    public void taskList_loadsPriorityFromSavedData() throws JonathanException {
        TaskList tasks = new TaskList(List.of("T | 0 | read book | 1"));

        assertEquals(Priority.HIGH, tasks.getTask(0).getPriority());
    }

    @Test
    public void taskStatus_markAndUnmark_updatesStatus() {
        ToDo todo = new ToDo("read book");

        assertEquals(" ", todo.getStatusIcon());

        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());

        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
    }
}
