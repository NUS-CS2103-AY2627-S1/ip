package koara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void taskOperations_multipleTasks_updatesListAndStatus() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));

        tasks.add(todo);
        tasks.add(deadline);
        assertEquals(2, tasks.getSize());
        assertSame(deadline, tasks.get(1));

        tasks.mark(1);
        assertEquals("[D][X] return book (by: Oct 15 2019)", tasks.get(1).toString());
        tasks.unmark(1);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(1).toString());

        assertSame(todo, tasks.delete(0));
        assertEquals(1, tasks.getSize());
        assertSame(deadline, tasks.get(0));
    }

    @Test
    public void constructor_sourceListChanged_doesNotChangeTaskList() {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new Todo("read book"));

        TaskList tasks = new TaskList(sourceTasks);
        sourceTasks.clear();

        assertEquals(1, tasks.getSize());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void toDataLines_mixedTasks_returnsStorageFormat() {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new Todo("read book"));
        sourceTasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        sourceTasks.add(new Event("project meeting", LocalDate.of(2019, 12, 2), LocalDate.of(2019, 12, 3)));
        TaskList tasks = new TaskList(sourceTasks);
        tasks.mark(1);

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | return book | 2019-10-15",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"
        ), tasks.toDataLines());
    }

    @Test
    public void find_matchingDescriptions_returnsMatchingTasksInOrder() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Event event = new Event("project meeting", LocalDate.of(2019, 12, 2), LocalDate.of(2019, 12, 3));
        TaskList tasks = new TaskList(new ArrayList<>(List.of(todo, deadline, event)));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.getSize());
        assertSame(todo, matchingTasks.get(0));
        assertSame(deadline, matchingTasks.get(1));
    }

    @Test
    public void find_keywordOutsideDescription_returnsEmptyTaskList() {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new Deadline("return assignment", LocalDate.of(2019, 10, 15))
        )));

        TaskList matchingTasks = tasks.find("2019");

        assertEquals(0, matchingTasks.getSize());
    }

    @Test
    public void containsEquivalentAndInsert_variedTasks_preservesOrder() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 9, 13));
        tasks.add(todo);

        assertTrue(tasks.containsEquivalent(new Todo("read book")));
        assertFalse(tasks.containsEquivalent(new Todo("write book")));
        tasks.insert(0, deadline);
        assertSame(deadline, tasks.get(0));
        assertSame(todo, tasks.get(1));
    }

    @Test
    public void taskOperations_invalidInternalArguments_throwAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.insert(1, new Todo("task")));
        assertThrows(AssertionError.class, () -> tasks.insert(0, null));
        assertThrows(AssertionError.class, () -> tasks.get(0));
        assertThrows(AssertionError.class, () -> tasks.delete(0));
        assertThrows(AssertionError.class, () -> tasks.mark(0));
        assertThrows(AssertionError.class, () -> tasks.unmark(0));
        assertThrows(AssertionError.class, () -> tasks.find(""));
        assertThrows(AssertionError.class, () -> tasks.containsEquivalent(null));
        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () -> new TaskList(
                new ArrayList<>(java.util.Arrays.asList((Task) null))));
    }
}
