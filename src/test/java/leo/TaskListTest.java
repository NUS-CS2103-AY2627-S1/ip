package leo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void constructor_fromArray_onlyIncludesEntriesUpToCount() {
        Task[] initial = new Task[]{new Todo("a"), new Todo("b"), new Todo("c")};

        // Only the first 2 of the 3 array entries should be picked up.
        TaskList tasks = new TaskList(initial, 2);

        assertEquals(2, tasks.size());
        assertEquals("a", tasks.get(0).getDescription());
        assertEquals("b", tasks.get(1).getDescription());
    }

    @Test
    public void add_increasesSizeAndAppendsAtEnd() {
        TaskList tasks = new TaskList(new Task[0], 0);

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        assertEquals(2, tasks.size());
        assertEquals("return book", tasks.get(1).getDescription());
    }

    @Test
    public void remove_returnsRemovedTaskAndShiftsRemaining() {
        TaskList tasks = new TaskList(new Task[0], 0);
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.add(new Todo("c"));

        Task removed = tasks.remove(0);

        assertEquals("a", removed.getDescription());
        assertEquals(2, tasks.size());
        assertEquals("b", tasks.get(0).getDescription());
    }

    @Test
    public void isValidIndex_outOfRangeOrNegative_returnsFalse() {
        TaskList tasks = new TaskList(new Task[0], 0);
        tasks.add(new Todo("only task"));

        assertTrue(tasks.isValidIndex(0));
        assertFalse(tasks.isValidIndex(-1));
        assertFalse(tasks.isValidIndex(1));
    }

    @Test
    public void toArray_reflectsCurrentContentsAndSize() {
        TaskList tasks = new TaskList(new Task[0], 0);
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));

        Task[] array = tasks.toArray();

        assertEquals(2, array.length);
        assertEquals("a", array[0].getDescription());
        assertEquals("b", array[1].getDescription());
    }

    @Test
    public void canHoldMoreThanOneHundredTasks() {
        // Regression check for the intentional A-MoreOOP behaviour change:
        // the old fixed-size-100 array capacity limit no longer applies.
        TaskList tasks = new TaskList(new Task[0], 0);

        for (int i = 0; i < 105; i++) {
            tasks.add(new Todo("task " + i));
        }

        assertEquals(105, tasks.size());
    }
}
