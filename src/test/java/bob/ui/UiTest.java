package bob.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.AbstractMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bob.task.Task;
import bob.task.TaskList;
import bob.task.ToDo;

/**
 * Tests the behavior of {@link Ui}.
 */
public class UiTest {
    private Ui ui;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
    }

    @Test
    public void setWelcome_updatesLastResponse() {
        ui.setWelcome();
        assertEquals("Morning! Bob here. What's the next job?", ui.getLastResponse());
    }

    @Test
    public void setWelcome_withLoadReport_listsLoadedTasksAndInvalidCount() {
        TaskList loadedTasks = new TaskList();
        ToDo loadedTask = new ToDo("testing");
        loadedTask.mark();
        loadedTasks.add(loadedTask);

        ui.setWelcome(loadedTasks, 1);

        assertEquals("Morning! Bob here. What's the next job?\n"
                + "Loaded tasks:\n1: [T][X] testing\n"
                + "Skipped invalid tasks: 1", ui.getLastResponse());
    }

    @Test
    public void setGoodbye_updatesLastResponse() {
        ui.setGoodbye();
        assertEquals("Tools down. Good work today!", ui.getLastResponse());
    }

    @Test
    public void setTaskAdded_updatesLastResponse() {
        Task task = new ToDo("read book");
        ui.setTaskAdded(task, 1);
        assertEquals("Added to the build plan:\n[T][ ] read book\n1 job on the board", ui.getLastResponse());

        ui.setTaskAdded(task, 2);
        assertEquals("Added to the build plan:\n[T][ ] read book\n2 jobs on the board", ui.getLastResponse());
    }

    @Test
    public void setTaskDeleted_updatesLastResponse() {
        Task task = new ToDo("read book");
        ui.setTaskDeleted(task, 0);
        assertEquals("Removed from the build plan:\n[T][ ] read book\n0 jobs on the board", ui.getLastResponse());

        ui.setTaskDeleted(task, 3);
        assertEquals("Removed from the build plan:\n[T][ ] read book\n3 jobs on the board", ui.getLastResponse());
    }

    @Test
    public void setTaskMarked_updatesLastResponse() {
        Task task = new ToDo("read book");
        ui.setTaskMarked(task, true);
        assertEquals("Job complete:\n [T][ ] read book", ui.getLastResponse());

        ui.setTaskMarked(task, false);
        assertEquals("Job reopened:\n [T][ ] read book", ui.getLastResponse());
    }

    @Test
    public void setTaskList_updatesLastResponse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        tasks.add(new ToDo("task 2"));
        ui.setTaskList(tasks);
        assertEquals("Today's job board:\n1: [T][ ] task 1\n2: [T][ ] task 2", ui.getLastResponse());
    }

    @Test
    public void setTaskList_emptyList_updatesLastResponse() {
        ui.setTaskList(new TaskList());

        assertEquals("The job board is clear.", ui.getLastResponse());
    }

    @Test
    public void setMatchingTasks_updatesLastResponse() {
        Task task = new ToDo("task 1");
        ui.setMatchingTasks(List.of(new AbstractMap.SimpleEntry<>(1, task)));
        assertEquals("These jobs match:\n1.[T][ ] task 1", ui.getLastResponse());
    }

    @Test
    public void setMatchingTasks_noMatches_updatesLastResponse() {
        ui.setMatchingTasks(List.of());

        assertEquals("No matching jobs found.", ui.getLastResponse());
    }

    @Test
    public void setErrorAndMessage_updatesLastResponse() {
        ui.setError("Error occurred");
        assertEquals("Error occurred", ui.getLastResponse());

        ui.setMessage("Simple message");
        assertEquals("Simple message", ui.getLastResponse());
    }

    @Test
    public void setTaskUpdated_updatesLastResponse() {
        Task before = new ToDo("old task");
        Task after = new ToDo("new task");
        ui.setTaskUpdated(before, after);
        assertEquals("Plan revised from:\n  [T][ ] old task\nto:\n  [T][ ] new task", ui.getLastResponse());
    }
}
