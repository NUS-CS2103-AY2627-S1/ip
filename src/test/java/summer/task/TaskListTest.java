package summer.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}, focusing on the non-trivial members:
 * {@code hasTaskAt} (index-bounds check used by Mark/Unmark/Delete),
 * {@code isFull} (capacity check used before adding), and {@code delete}
 * (which must return the removed task and keep the rest in order).
 */
public class TaskListTest {

    private static Task todo(String description) {
        return new ToDo(description, false);
    }

    /** A list with the given capacity, pre-filled with {@code count} todos. */
    private static TaskList listOf(int capacity, int count) {
        TaskList list = new TaskList(capacity);
        for (int i = 0; i < count; i++) {
            list.add(todo("task " + i));
        }
        return list;
    }

    // ---------- hasTaskAt ----------

    @Test
    public void hasTaskAt_emptyList_false() {
        TaskList list = listOf(100, 0);
        assertFalse(list.hasTaskAt(0));
        assertFalse(list.hasTaskAt(-1));
    }

    @Test
    public void hasTaskAt_indexWithinList_true() {
        TaskList list = listOf(100, 3);
        assertTrue(list.hasTaskAt(0));
        assertTrue(list.hasTaskAt(2));
    }

    @Test
    public void hasTaskAt_indexOutsideList_false() {
        TaskList list = listOf(100, 3);
        assertFalse(list.hasTaskAt(-1));
        assertFalse(list.hasTaskAt(3));
        assertFalse(list.hasTaskAt(50));
    }

    // ---------- isFull ----------

    @Test
    public void isFull_belowCapacity_false() {
        assertFalse(listOf(3, 0).isFull());
        assertFalse(listOf(3, 2).isFull());
    }

    @Test
    public void isFull_atCapacity_true() {
        assertTrue(listOf(3, 3).isFull());
    }

    // ---------- delete ----------

    @Test
    public void delete_middleTask_returnsItAndShiftsRemainder() {
        Task a = todo("a");
        Task b = todo("b");
        Task c = todo("c");
        TaskList list = new TaskList(100);
        list.add(a);
        list.add(b);
        list.add(c);

        Task removed = list.delete(1);

        assertSame(b, removed);
        assertEquals(2, list.size());
        assertSame(a, list.get(0));
        assertSame(c, list.get(1));
    }

    // ---------- sort ----------

    @Test
    public void sort_mixedTasks_ordersDatedTasksChronologicallyThenTodos() {
        TaskList list = new TaskList(100);
        list.add(new Deadline("submit", LocalDate.of(2019, 10, 20), false));
        list.add(todo("read book"));
        list.add(new Event("camp", LocalDate.of(2019, 10, 10), LocalDate.of(2019, 10, 12), false));
        list.add(new Deadline("pay bill", LocalDate.of(2019, 10, 15), false));

        list.sort();

        String order = list.list();
        assertTrue(order.indexOf("camp") < order.indexOf("pay bill"));
        assertTrue(order.indexOf("pay bill") < order.indexOf("submit"));
        assertTrue(order.indexOf("submit") < order.indexOf("read book"));
    }

    @Test
    public void sort_equalDates_keepsOriginalOrder() {
        Task first = new Deadline("first", LocalDate.of(2019, 1, 1), false);
        Task second = new Deadline("second", LocalDate.of(2019, 1, 1), false);
        TaskList list = new TaskList(100);
        list.add(first);
        list.add(second);

        list.sort();

        assertSame(first, list.get(0));
        assertSame(second, list.get(1));
    }

    @Test
    public void sort_returnValue_usesSortedHeader() {
        TaskList list = new TaskList(100);
        list.add(todo("a"));

        assertTrue(list.sort().startsWith(" Here are the tasks in your list, sorted:"));
    }

    // ---------- find ----------

    @Test
    public void find_keywordInSomeDescriptions_listsOnlyMatchingTasks() {
        TaskList list = new TaskList(100);
        list.add(todo("read book"));
        list.add(todo("return book"));
        list.add(todo("buy milk"));

        String result = list.find("book");

        assertTrue(result.contains("read book"));
        assertTrue(result.contains("return book"));
        assertFalse(result.contains("milk"));
    }

    @Test
    public void find_noMatchingDescription_listsHeaderOnly() {
        TaskList list = new TaskList(100);
        list.add(todo("read book"));

        assertEquals(" Here are the matching tasks in your list:", list.find("xyz"));
    }
}
