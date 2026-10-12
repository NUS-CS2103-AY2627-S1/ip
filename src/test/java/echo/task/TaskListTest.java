package echo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task collection behavior, indexing, and search in {@link TaskList}. */
public class TaskListTest {

    @Test
    public void constructors_initializeTaskListCorrectly() {
        TaskList emptyList = new TaskList();
        assertTrue(emptyList.isEmpty());
        assertEquals(0, emptyList.size());

        Task task = new TodoTask("test");
        TaskList populatedList = new TaskList(List.of(task));
        assertEquals(1, populatedList.size());
        assertSame(task, populatedList.getTask(1));
    }

    @Test
    public void addTask_appendsTask() {
        TaskList list = new TaskList();
        Task first = new TodoTask("first");
        Task second = new TodoTask("second");

        list.addTask(first);
        list.addTask(second);

        assertEquals(2, list.size());
        assertSame(first, list.getTask(1));
        assertSame(second, list.getTask(2));
    }

    @Test
    public void removeTask_removesAndShiftsRemainingTasks() {
        TaskList list = new TaskList();
        Task first = new TodoTask("first");
        Task second = new TodoTask("second");
        Task third = new TodoTask("third");

        list.addTask(first);
        list.addTask(second);
        list.addTask(third);

        Task removed = list.removeTask(2);
        assertSame(second, removed);
        assertEquals(2, list.size());
        assertSame(first, list.getTask(1));
        assertSame(third, list.getTask(2));
    }

    @Test
    public void hasTask_verifiesOneBasedIndexBounds() {
        TaskList list = new TaskList();
        assertFalse(list.hasTask(0));
        assertFalse(list.hasTask(1));
        assertFalse(list.hasTask(-1));

        list.addTask(new TodoTask("task"));
        assertTrue(list.hasTask(1));
        assertFalse(list.hasTask(0));
        assertFalse(list.hasTask(2));
    }

    @Test
    public void hasDuplicate_detectsExistingEquivalentTasks() {
        TaskList list = new TaskList();
        list.addTask(new TodoTask("read book"));

        assertTrue(list.hasDuplicate(new TodoTask("read book")));
        assertTrue(list.hasDuplicate(new TodoTask("READ BOOK")));
        assertFalse(list.hasDuplicate(new TodoTask("write code")));
    }

    @Test
    public void asList_returnsUnmodifiableCopy() {
        TaskList list = new TaskList();
        list.addTask(new TodoTask("task"));

        List<Task> snapshot = list.asList();
        assertThrows(UnsupportedOperationException.class, snapshot::clear);
    }

    @Test
    public void findByKeyword_matchesDescriptions() {
        TaskList list = new TaskList();
        Task task1 = new TodoTask("read book");
        Task task2 = new TodoTask("borrow novel");
        Task task3 = new TodoTask("return book");

        list.addTask(task1);
        list.addTask(task2);
        list.addTask(task3);

        List<Task> results = list.findByKeyword("book");
        assertEquals(List.of(task1, task3), results);

        List<Task> emptyResults = list.findByKeyword("magazine");
        assertTrue(emptyResults.isEmpty());
    }

    @Test
    public void sort_ordersTasksWithComparator() {
        TaskList list = new TaskList();
        Task zebra = new TodoTask("zebra");
        Task apple = new TodoTask("apple");

        list.addTask(zebra);
        list.addTask(apple);

        list.sort(Comparator.comparing(Task::getDescription));

        assertSame(apple, list.getTask(1));
        assertSame(zebra, list.getTask(2));
    }
}
