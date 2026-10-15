package wally;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the Tasklist class.
 */
public class TasklistTest {
    @Test
    public void removeTask_firstMiddleAndLast_preservesRemainingOrder() throws DuplicateTaskException {
        Tasklist tasks = new Tasklist();
        for (String name : new String[] {"first", "second", "third", "fourth"}) {
            tasks.addTask(new ToDo(name));
        }
        tasks.removeTask(2);
        assertEquals("third", tasks.getTask(2).getDescription());
        tasks.removeTask(3);
        tasks.removeTask(1);
        assertEquals(1, tasks.getSize());
        assertEquals("third", tasks.getTask(1).getDescription());
        tasks.removeTask(1);
        assertEquals(0, tasks.getSize());
    }

    @Test
    public void removeTask_invalidPositions_rejectsWithoutMutation() throws DuplicateTaskException {
        Tasklist tasks = new Tasklist();
        assertThrows(AssertionError.class, () -> tasks.removeTask(1));
        tasks.addTask(new ToDo("read"));
        assertThrows(AssertionError.class, () -> tasks.removeTask(0));
        assertThrows(AssertionError.class, () -> tasks.removeTask(2));
        assertEquals(1, tasks.getSize());
    }

    @Test
    public void addTask_nullAndDuplicate_rejectsWithoutMutation() throws DuplicateTaskException {
        Tasklist tasks = new Tasklist();
        tasks.addTask(new ToDo("read"));
        assertThrows(AssertionError.class, () -> tasks.addTask(null));
        assertThrows(DuplicateTaskException.class, () -> tasks.addTask(new Deadline(" READ ", "2026-09-14 12:00")));
        assertEquals(1, tasks.getSize());
    }

    /**
     * Tests the addTask and getTask methods.
     */
    @Test
    public void addTask_validTask_taskIsRetrievable() throws DuplicateTaskException {
        Tasklist taskings = new Tasklist();
        ToDo test1 = new ToDo("test1");
        taskings.addTask(test1);
        assertEquals(test1, taskings.getTask(1));
    }
}
