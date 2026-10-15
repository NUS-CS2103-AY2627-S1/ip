package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import ralph.exception.RalphException;

class TaskListSortTest {

    @Test
    void listByDeadline_sortsDatedTasksChronologicallyAndPlacesUndatedLast() throws RalphException {
        TaskList tl = new TaskList();
        Todo undated1 = new Todo("undated1");
        Event ev = new Event("ev", LocalDateTime.of(2020, 1, 1, 0, 0), LocalDateTime.of(2020, 1, 1, 1, 0));
        Deadline dl = new Deadline("dl", LocalDateTime.of(2020, 1, 2, 0, 0));
        Todo undated2 = new Todo("undated2");

        // initial insertion order: undated1, ev(2020-01-01), dl(2020-01-02), undated2
        tl.add(undated1);
        tl.add(ev);
        tl.add(dl);
        tl.add(undated2);

        List<Task> sorted = tl.getAllSorted("deadline");

        assertEquals(4, sorted.size());
        // dated tasks first in chronological order
        assertEquals(ev, sorted.get(0));
        assertEquals(dl, sorted.get(1));
        // undated tasks after, preserving their original relative order
        assertEquals(undated1, sorted.get(2));
        assertEquals(undated2, sorted.get(3));
    }

    @Test
    void listByDeadline_preservesOriginalOrderForEqualTimestampsAndUndated() throws RalphException {
        TaskList tl = new TaskList();
        LocalDateTime dt = LocalDateTime.of(2021, 5, 10, 12, 0);
        Deadline d1 = new Deadline("d1", dt);
        Deadline d2 = new Deadline("d2", dt);
        Todo undatedA = new Todo("a");
        Todo undatedB = new Todo("b");

        // insertion: d1, d2, undatedA, undatedB
        tl.add(d1);
        tl.add(d2);
        tl.add(undatedA);
        tl.add(undatedB);

        List<Task> sorted = tl.getAllSorted("deadline");

        assertEquals(4, sorted.size());
        // tasks with equal timestamps should preserve insertion order
        assertEquals(d1, sorted.get(0));
        assertEquals(d2, sorted.get(1));
        // undated tasks preserve their original relative order
        assertEquals(undatedA, sorted.get(2));
        assertEquals(undatedB, sorted.get(3));
    }

    @Test
    void listByStatus_placesIncompleteBeforeComplete_preservingRelativeOrder() throws RalphException {
        TaskList tl = new TaskList();
        Todo a = new Todo("a");
        Todo b = new Todo("b");
        Deadline c = new Deadline("c", LocalDateTime.of(2022, 1, 1, 0, 0));
        Todo d = new Todo("d");

        tl.add(a); // incomplete
        tl.add(b); // will mark done
        tl.add(c); // incomplete
        tl.add(d); // will mark done

        // mark b and d as done
        b.markAsDone();
        d.markAsDone();

        List<Task> sorted = tl.getAllSorted("status");

        assertEquals(4, sorted.size());
        // incomplete tasks (a, c) in original relative order
        assertEquals(a, sorted.get(0));
        assertEquals(c, sorted.get(1));
        // completed tasks (b, d) in original relative order
        assertEquals(b, sorted.get(2));
        assertEquals(d, sorted.get(3));
    }

    @Test
    void invalidSortKey_throwsRalphException() {
        TaskList tl = new TaskList();
        tl.add(new Todo("x"));
        assertThrows(RalphException.class, () -> tl.getAllSorted("unknown"));
    }

    @Test
    void plainGetAll_remainsUnchanged() {
        TaskList tl = new TaskList();
        Todo a = new Todo("a");
        Todo b = new Todo("b");
        tl.add(a);
        tl.add(b);
        List<Task> all = tl.getAll();
        assertEquals(2, all.size());
        assertEquals(a, all.get(0));
        assertEquals(b, all.get(1));
    }
}
