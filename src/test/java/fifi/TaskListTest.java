package fifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import fifi.exception.DuplicateTaskException;
import fifi.exception.ExcessiveTaskException;
import fifi.task.Deadline;
import fifi.task.Event;
import fifi.task.Task;
import fifi.task.ToDo;

/**
 * Tests task list operations that change or filter the chatbot's tasks.
 */
public class TaskListTest {

    @Test
    public void add_emptyList_taskAdded() throws Exception {
        TaskList tasks = new TaskList();
        ToDo todo = new ToDo(false, "read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void constructor_mutableSourceListChanged_taskListUnaffected() {
        ArrayList<Task> sourceTasks = getSampleTasks();
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.clear();
        sourceTasks.add(new ToDo(false, "unexpected task"));

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)",
                tasks.get(2).toString());
    }

    @Test
    public void add_fullList_exceptionThrown() throws Exception {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            tasks.add(new ToDo(false, "task " + i));
        }

        assertThrows(ExcessiveTaskException.class, () -> tasks.add(new ToDo(false, "overflow task")));
        assertEquals(100, tasks.size());

        tasks.delete(99);
        ToDo replacement = new ToDo(false, "replacement task");
        tasks.add(replacement);
        assertEquals(100, tasks.size());
        assertEquals(replacement, tasks.get(99));
        assertThrows(ExcessiveTaskException.class, () -> tasks.add(new ToDo(false, "another overflow")));
        assertEquals(replacement, tasks.get(99));
    }

    @Test
    public void delete_existingTask_taskRemovedAndReturned() {
        TaskList tasks = new TaskList(getSampleTasks());

        Task deletedTask = tasks.delete(1);

        assertInstanceOf(Deadline.class, deletedTask);
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)", tasks.get(1).toString());
    }

    @Test
    public void delete_invalidIndicesThenLastTask_statePreservedAndListReusable() throws Exception {
        TaskList tasks = new TaskList();
        ToDo todo = new ToDo(false, "read book");
        tasks.add(todo);

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(-1));
        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
        assertEquals(todo, tasks.delete(0));
        assertEquals(0, tasks.size());
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(0));
        assertEquals(0, tasks.size());

        tasks.add(todo);
        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void getTasksOccurringOn_matchingDeadlineAndEvent_matchingTasksReturned() {
        TaskList tasks = new TaskList(getSampleTasks());

        TaskList matchingTasks = tasks.getTasksOccurringOn(LocalDate.of(2019, 12, 2));

        assertEquals("""

                1. [D][ ] return book (by: Dec 02 2019)
                2. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)""",
                matchingTasks.toDisplayString());
        assertEquals(3, tasks.size());
    }

    @Test
    public void getTasksOccurringOn_eventMiddleDate_eventReturned() {
        TaskList tasks = new TaskList(getSampleTasks());

        TaskList matchingTasks = tasks.getTasksOccurringOn(LocalDate.of(2019, 12, 3));

        assertEquals("""

                1. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)""",
                matchingTasks.toDisplayString());
    }

    @Test
    public void getTasksOccurringOn_noMatchingDate_emptyTaskListReturned() {
        TaskList tasks = new TaskList(getSampleTasks());

        TaskList matchingTasks = tasks.getTasksOccurringOn(LocalDate.of(2019, 12, 5));

        assertEquals(0, matchingTasks.size());
        assertEquals("", matchingTasks.toDisplayString());
    }

    @Test
    public void findTasksByKeyword_matchingDescriptions_matchingTasksReturned() {
        TaskList tasks = new TaskList(getSampleTasks());

        TaskList matchingTasks = tasks.findTasksByKeyword("book");

        assertEquals("""

                1. [T][ ] read book
                2. [D][ ] return book (by: Dec 02 2019)""",
                matchingTasks.toDisplayString());
        assertEquals(3, tasks.size());
    }

    @Test
    public void findTasksByKeyword_noMatchingDescriptions_emptyTaskListReturned() {
        TaskList tasks = new TaskList(getSampleTasks());

        TaskList matchingTasks = tasks.findTasksByKeyword("lecture");

        assertEquals(0, matchingTasks.size());
        assertEquals("", matchingTasks.toDisplayString());
    }

    @Test
    public void toDisplayString_multipleTasks_numberedTaskStringReturned() {
        TaskList tasks = new TaskList(getSampleTasks());

        assertEquals("""

                1. [T][ ] read book
                2. [D][ ] return book (by: Dec 02 2019)
                3. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)""",
                tasks.toDisplayString());
    }

    @Test
    public void add_duplicateDetails_rejectedWithoutAffectingLaterAdditions() throws Exception {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2026, 9, 20);
        Task[] originals = {new ToDo(false, "work"), new Deadline(false, "work", date),
            new Event(false, "work", date, date)};
        Task[] duplicates = {new ToDo(true, " work "), new Deadline(true, "work", date),
            new Event(true, "work", date, date)};
        for (int i = 0; i < originals.length; i++) {
            tasks.add(originals[i]);
            int size = tasks.size();
            assertThrows(DuplicateTaskException.class, () -> tasks.add(duplicates[size - 1]));
            assertEquals(size, tasks.size());
            assertEquals(originals[i], tasks.get(i));
        }
        tasks.add(new Deadline(false, "work", date.plusDays(1)));
        tasks.add(new Event(false, "work", date, date.plusDays(1)));
        tasks.add(new ToDo(false, "Work"));
        assertEquals(6, tasks.size());
        tasks.delete(0);
        tasks.add(new ToDo(false, "work"));
        assertEquals(6, tasks.size());
    }

    @Test
    public void constructor_savedDuplicates_preservedWhileNewDuplicateRejected() throws Exception {
        ArrayList<Task> savedTasks = new ArrayList<>();
        savedTasks.add(new ToDo(false, "work"));
        savedTasks.add(new ToDo(true, "work"));
        TaskList tasks = new TaskList(savedTasks);

        assertEquals(2, tasks.size());
        assertThrows(DuplicateTaskException.class, () -> tasks.add(new ToDo(false, "work")));
        tasks.add(new ToDo(false, "different work"));
        assertEquals(3, tasks.size());
    }

    @Test
    public void copy_changesToCandidate_doNotChangeOriginalTasksOrTimestamps() {
        LocalDate date = LocalDate.of(2025, 9, 20);
        LocalDateTime createdAt = date.atTime(9, 0);
        ArrayList<Task> originals = new ArrayList<>();
        originals.add(new ToDo(false, "todo", null, null));
        originals.add(new Deadline(false, "deadline", date, createdAt, null));
        originals.add(new Event(false, "event", date, date, createdAt, createdAt));
        TaskList tasks = new TaskList(originals);
        TaskList candidate = tasks.copy();
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).toFileString(), candidate.get(i).toFileString());
            candidate.get(i).mark();
            assertFalse(tasks.get(i).isMarked());
            assertEquals(originals.get(i), tasks.get(i));
        }
        candidate.delete(0);
        assertEquals(3, tasks.size());
        assertEquals(2, candidate.size());
    }

    private ArrayList<Task> getSampleTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo(false, "read book"));
        tasks.add(new Deadline(false, "return book", LocalDate.of(2019, 12, 2)));
        tasks.add(new Event(false, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4)));
        return tasks;
    }
}
