package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import megatron.exception.TaskNotFoundException;

/**
 * Tests a complete task-model workflow across task types and list operations.
 */
class TaskModelIntegrationTest {
    @Test
    void taskLifecycle_addFindCompleteAndRemove_preservesTaskBehavior()
            throws TaskNotFoundException {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        Task deadline = new Deadline("submit report", "2026-06-06");

        tasks.add(todo);
        tasks.add(deadline);

        List<TaskMatch> matches = tasks.findMatches("BOOK");
        assertEquals(1, matches.size());
        assertEquals(1, matches.get(0).taskNumber());
        assertSame(todo, matches.get(0).task());

        Task completedTask = tasks.setDone(1);
        assertSame(todo, completedTask);
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.displayText());

        Task removedTask = tasks.removeTask(2);
        assertSame(deadline, removedTask);
        assertEquals(1, tasks.size());
        assertEquals("[T][X] read book", tasks.getTask(1).displayText());
    }
}
