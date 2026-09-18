package sozius.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import sozius.task.Task;
import sozius.task.TodoTask;

public class TaskListTest {

    @Test
    public void constructor_noArgs_emptyList() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_withList_copiesTasks() {
        TaskList tasks = new TaskList(List.of(new TodoTask("a"), new TodoTask("b")));
        assertEquals(2, tasks.size());
        assertEquals("a", tasks.get(0).getDescription());
        assertEquals("b", tasks.get(1).getDescription());
    }

    @Test
    public void add_task_increasesSize() {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("read book"));
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void remove_validIndex_returnsAndRemovesTask() {
        TaskList tasks = new TaskList(
                List.of(new TodoTask("a"), new TodoTask("b"), new TodoTask("c")));
        Task removed = tasks.remove(1);
        assertEquals("b", removed.getDescription());
        assertEquals(2, tasks.size());
        assertEquals("c", tasks.get(1).getDescription());
    }

    @Test
    public void remove_invalidIndex_throwsException() {
        TaskList tasks = new TaskList(List.of(new TodoTask("a")));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(5));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(-1));
    }

    @Test
    public void markTask_validIndex_marksDone() {
        TaskList tasks = new TaskList(List.of(new TodoTask("a")));
        tasks.markTask(0);
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void unmarkTask_validIndex_marksNotDone() {
        TodoTask task = new TodoTask("a");
        task.setDone(true);
        TaskList tasks = new TaskList(List.of(task));
        tasks.unmarkTask(0);
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void getLast_multipleTasks_returnsLastAdded() {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("first"));
        tasks.add(new TodoTask("last"));
        assertEquals("last", tasks.getLast().getDescription());
    }

    @Test
    public void iterator_multipleTasks_iteratesInOrder() {
        TaskList tasks = new TaskList(
                List.of(new TodoTask("a"), new TodoTask("b"), new TodoTask("c")));
        StringBuilder sb = new StringBuilder();
        for (Task task : tasks) {
            sb.append(task.getDescription());
        }
        assertEquals("abc", sb.toString());
    }
}
