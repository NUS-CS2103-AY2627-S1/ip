package jarvis.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Test the behaviour of {@link ToDo}. */
public class ToDoTest {
    /** Verifies that a newly created todoTask is undone and formatted correctly. */
    @Test
    public void constructor_createsUndoneToDoTask() {
        ToDo todoTask = new ToDo("Read book");

        assertEquals(Task.CompletionStatus.UNDONE, todoTask.getStatus());
        assertEquals("[T][] Read book", todoTask.toString());
    }

    /** Verifies that a todoTask can be marked as done and displays the done marker. */
    @Test
    public void setCompletionStatus_doneTaskDisplaysDoneMarker() {
        ToDo todoTask = new ToDo("Read book");

        todoTask.setCompletionStatus(Task.CompletionStatus.DONE);

        assertEquals(Task.CompletionStatus.DONE, todoTask.getStatus());
        assertEquals("[T][X] Read book", todoTask.toString());
    }

    /** Verifies that a completed toDoTask can be marked undone again. */
    @Test
    public void setCompletionStatus_undoneTaskDisplaysUndoneMarker() {
        ToDo todoTask = new ToDo("Read book");

        todoTask.setCompletionStatus(Task.CompletionStatus.DONE);
        todoTask.setCompletionStatus(Task.CompletionStatus.UNDONE);

        assertEquals(Task.CompletionStatus.UNDONE, todoTask.getStatus());
        assertEquals("[T][] Read book", todoTask.toString());
    }
}
