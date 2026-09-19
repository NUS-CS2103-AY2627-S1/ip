package gary.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class TaskListTest {
    @Test
    void taskOperations_variedMutations_updateList() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstTask));

        tasks.add(secondTask);
        Task markedTask = tasks.markAsDone(2);
        Task unmarkedTask = tasks.markAsNotDone(2);
        Task deletedTask = tasks.delete(1);

        assertEquals(1, tasks.size());
        assertEquals(secondTask, tasks.getTask(1));
        assertEquals(firstTask, deletedTask);
        assertEquals(secondTask, markedTask);
        assertEquals(secondTask, unmarkedTask);
        assertFalse(secondTask.isDone());
    }

    @Test
    void getTasks_returnedSnapshot_isImmutableAndIndependent() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> snapshot = tasks.getTasks();

        tasks.add(new Todo("return book"));
        Executable addToSnapshot = () -> snapshot.add(new Todo("buy bread"));

        assertEquals(1, snapshot.size());
        assertEquals(2, tasks.size());
        assertThrows(UnsupportedOperationException.class, addToSnapshot);
        assertTrue(snapshot.get(0).containsKeyword("book"));
    }
}
