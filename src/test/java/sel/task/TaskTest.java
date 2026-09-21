package sel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void constructor_setsDescriptionAndNotDoneByDefault() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void mark_setsIsDoneTrue() {
        Task task = new Task("read book");
        task.mark();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void unmark_setsIsDoneFalse() {
        Task task = new Task("read book");
        task.mark();
        task.unmark();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void mark_calledTwice_remainsDone() {
        Task task = new Task("read book");
        task.mark();
        task.mark();
        assertTrue(task.isDone());
    }

    @Test
    public void unmark_calledOnFreshTask_remainsNotDone() {
        Task task = new Task("read book");
        task.unmark();
        assertFalse(task.isDone());
    }

    @Test
    public void toString_returnsDescriptionOnly() {
        Task task = new Task("read book");
        assertEquals("read book", task.toString());
    }

    @Test
    public void hasSameDetailsAs_comparesTypeAndDescriptionIgnoringCase() {
        ToDo todo = new ToDo("read book");

        assertTrue(todo.hasSameDetailsAs(new ToDo("read book")));
        assertTrue(todo.hasSameDetailsAs(new ToDo("READ BOOK")));
        assertFalse(todo.hasSameDetailsAs(new ToDo("read magazine")));
        assertFalse(todo.hasSameDetailsAs(null));
    }

    @Test
    public void hasSameDetailsAs_differentTypesNeverMatch() {
        // A plain Task and a ToDo can share a description without being
        // the same task, so the class has to be part of the comparison.
        assertFalse(new Task("read book").hasSameDetailsAs(new ToDo("read book")));
    }

    @Test
    public void hasSameDetailsAs_ignoresDoneStatus() {
        ToDo done = new ToDo("read book");
        done.mark();
        assertTrue(done.hasSameDetailsAs(new ToDo("read book")));
    }
}
