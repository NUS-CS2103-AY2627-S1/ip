package carl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlUnknownTaskException;

public class TaskListBehaviorTest {
    private TaskList tasks;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList(List.of(new Todo("Bravo"), new Todo("alpha")));
    }

    @Test
    public void constructorAndGetter_returnDefensiveListCopies() throws Exception {
        List<Task> source = new ArrayList<>(List.of(new Todo("original")));
        TaskList taskList = new TaskList(source);
        source.add(new Todo("source mutation"));
        List<Task> returned = taskList.getAllTasks();
        returned.add(new Todo("returned mutation"));

        assertEquals(1, taskList.getTasksLeft());
        assertEquals("original", taskList.getAllTasks().getFirst().name);
    }

    @Test
    public void addTask_nullTask_throwsCommandException() {
        assertThrows(CarlCommandException.class, () -> tasks.addTaskToList(null));
    }

    @Test
    public void sortingAndFinding_doNotChangeStoredOrder() {
        List<Task> sorted = tasks.getSortedTasks();
        List<Task> matches = tasks.findTask("BR");

        assertEquals("alpha", sorted.getFirst().name);
        assertEquals("Bravo", matches.getFirst().name);
        assertEquals("Bravo", tasks.getAllTasks().getFirst().name);
    }

    @Test
    public void markAndUnmark_invalidOrRepeatedOperations_throwCommandException() throws Exception {
        assertThrows(CarlCommandException.class, () -> tasks.markTaskAsDone(-1));
        assertThrows(CarlCommandException.class, () -> tasks.markTaskAsDone(2));
        tasks.markTaskAsDone(0);
        assertThrows(CarlCommandException.class, () -> tasks.markTaskAsDone(0));
        tasks.markTaskAsUndone(0);
        assertThrows(CarlCommandException.class, () -> tasks.markTaskAsUndone(0));
        assertThrows(CarlCommandException.class, () -> tasks.markTaskAsUndone(2));
    }

    @Test
    public void deleteTask_validAndInvalidIndexes_behavePredictably() throws Exception {
        Task deleted = tasks.deleteTask(0);

        assertEquals("Bravo", deleted.name);
        assertEquals(1, tasks.getTasksLeft());
        assertThrows(CarlUnknownTaskException.class, () -> tasks.deleteTask(-1));
        assertThrows(CarlUnknownTaskException.class, () -> tasks.deleteTask(1));
    }

    @Test
    public void snapshot_afterMixedMutations_restoresOrderMembershipAndStatus() throws Exception {
        TaskList.Snapshot snapshot = tasks.createSnapshot();
        tasks.markTaskAsDone(0);
        tasks.deleteTask(1);
        tasks.addTaskToList(new Deadline("new", LocalDateTime.of(2026, 10, 1, 12, 0)));
        assertNotEquals("[T][ ] Bravo", tasks.getAllTasks().getFirst().toString());

        tasks.restore(snapshot);

        assertEquals(List.of("[T][ ] Bravo", "[T][ ] alpha"),
                tasks.getAllTasks().stream().map(Task::toString).toList());
    }

    @Test
    public void toSaveString_multipleTasks_usesPlatformLineSeparator() {
        String saved = tasks.toSaveString();

        assertEquals("T | 0 | Bravo" + System.lineSeparator()
                + "T | 0 | alpha" + System.lineSeparator(), saved);
        assertTrue(saved.endsWith(System.lineSeparator()));
    }
}
