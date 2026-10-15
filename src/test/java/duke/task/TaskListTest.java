package duke.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task list operations.
 */
public class TaskListTest {
    @Test
    public void add_emptyList_addsTaskToEnd() {
        TaskList tasks = new TaskList();

        tasks.add(new ToDo("read book"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void constructor_externalListChanges_doesNotChangeTaskList() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new ToDo("read book"));
        TaskList tasks = new TaskList(initialTasks);

        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void getTasks_existingTasks_returnsImmutableSnapshot() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        List<Task> taskSnapshot = tasks.getTasks();
        tasks.add(new ToDo("return book"));

        assertEquals(1, taskSnapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> taskSnapshot.add(new ToDo("write report")));
    }

    @Test
    public void delete_multipleTasks_removesSelectedTaskAndRenumbersRemainingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("return book"));
        tasks.add(new ToDo("write report"));

        Task deletedTask = tasks.delete(1);

        assertEquals("[T][ ] return book", deletedTask.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] write report", tasks.get(1).toString());
    }

    @Test
    public void markAndUnmark_existingTask_updatesDoneStatus() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        Task markedTask = tasks.mark(0);

        assertEquals("[T][X] read book", markedTask.toString());
        assertEquals("[T][X] read book", tasks.get(0).toString());

        Task unmarkedTask = tasks.unmark(0);

        assertEquals("[T][ ] read book", unmarkedTask.toString());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasksOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("write report"));
        tasks.add(new ToDo("return book"));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] read book", matchingTasks.get(0).toString());
        assertEquals("[T][ ] return book", matchingTasks.get(1).toString());
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        TaskList matchingTasks = tasks.find("movie");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    public void getScheduledTasks_matchingDate_returnsDatedTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("submit report", LocalDate.of(2019, 10, 15)));
        tasks.add(new Deadline("pay bill", LocalDate.of(2019, 10, 16)));
        tasks.add(new Event("conference", LocalDate.of(2019, 10, 14), LocalDate.of(2019, 10, 16)));
        tasks.add(new Event("holiday", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 18)));
        tasks.mark(1);

        TaskList scheduledTasks = tasks.getScheduledTasks(LocalDate.of(2019, 10, 15));

        assertEquals(3, scheduledTasks.size());
        assertEquals("[D][X] submit report (by: Oct 15 2019)", scheduledTasks.get(0).toString());
        assertEquals(
                "[E][ ] conference (from: Oct 14 2019 to: Oct 16 2019)",
                scheduledTasks.get(1).toString());
        assertEquals(
                "[E][ ] holiday (from: Oct 15 2019 to: Oct 18 2019)",
                scheduledTasks.get(2).toString());
    }

    @Test
    public void getScheduledTasks_eventBoundaryDates_includesEvent() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("conference", LocalDate.of(2019, 10, 14), LocalDate.of(2019, 10, 16)));

        assertEquals(1, tasks.getScheduledTasks(LocalDate.of(2019, 10, 14)).size());
        assertEquals(1, tasks.getScheduledTasks(LocalDate.of(2019, 10, 16)).size());
    }

    @Test
    public void getScheduledTasks_noMatchingDate_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("submit report", LocalDate.of(2019, 10, 15)));

        TaskList scheduledTasks = tasks.getScheduledTasks(LocalDate.of(2019, 10, 16));

        assertEquals(0, scheduledTasks.size());
    }
}
