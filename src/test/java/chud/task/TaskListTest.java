package chud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void newTaskList_isEmpty() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_backedByExistingTasks_reflectsGivenTasks() {
        ArrayList<Task> existing = new ArrayList<>();
        existing.add(new Todo("borrow book"));
        existing.add(new Todo("return book"));

        TaskList tasks = new TaskList(existing);

        assertEquals(2, tasks.size());
        assertSame(existing.get(0), tasks.get(0));
    }

    @Test
    void add_increasesSizeAndAppendsToEnd() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("borrow book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
    }

    @Test
    void remove_existingIndex_returnsRemovedTaskAndDecreasesSize() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("borrow book");
        Todo second = new Todo("return book");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.remove(0);

        assertSame(first, removed);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    @Test
    void iterator_visitsTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("borrow book");
        Todo second = new Todo("return book");
        tasks.add(first);
        tasks.add(second);

        Iterator<Task> iterator = tasks.iterator();

        assertSame(first, iterator.next());
        assertSame(second, iterator.next());
    }

    @Test
    void sort_byGivenComparator_reordersInPlaceStably() {
        TaskList tasks = new TaskList();
        Todo banana = new Todo("banana");
        Todo apple = new Todo("apple");
        Todo apricot = new Todo("apricot");
        tasks.add(banana);
        tasks.add(apple);
        tasks.add(apricot);

        // apple and apricot both start with 'a' -- a stable sort must keep apple before apricot,
        // since that was their relative order before sorting.
        tasks.sort(Comparator.comparing(task -> task.getDescription().substring(0, 1)));

        assertSame(apple, tasks.get(0));
        assertSame(apricot, tasks.get(1));
        assertSame(banana, tasks.get(2));
    }
}
