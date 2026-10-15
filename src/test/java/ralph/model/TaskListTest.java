package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void constructor_nullInitial_createsEmptyList() {
        TaskList tl = new TaskList(null);
        assertEquals(0, tl.size());
    }

    @Test
    void getAll_unmodifiable_throwsOnAdd() {
        TaskList tl = new TaskList();
        tl.add(new Todo("a"));
        List<Task> all = tl.getAll();
        assertThrows(UnsupportedOperationException.class, () -> all.add(new Todo("b")));
    }

    @Test
    void addAndRemoveAndGet_multipleTasks_behaveAsExpected() {
        TaskList tl = new TaskList();
        Todo t1 = new Todo("first");
        Todo t2 = new Todo("second");
        tl.add(t1);
        tl.add(t2);
        assertEquals(2, tl.size());
        assertEquals(t1, tl.get(0));
        Task removed = tl.remove(0);
        assertEquals(t1, removed);
        assertEquals(1, tl.size());
        assertEquals(t2, tl.get(0));
    }

    @Test
    void find_caseInsensitiveKeyword_returnsMatchingTasksInOrder() {
        TaskList tl = new TaskList();
        tl.add(new Todo("read book"));
        tl.add(new Todo("return book"));
        tl.add(new Todo("go home"));

        List<Task> matches = tl.find("BOOK");

        assertEquals(2, matches.size());
        assertEquals("read book", matches
            .get(0)
            .getDescription());
        assertEquals("return book", matches
            .get(1)
            .getDescription());
    }

    @Test
    void find_emptyOrBlank_returnsEmptyList() {
        TaskList tl = new TaskList();
        tl.add(new Todo("something"));
        List<Task> r1 = tl.find("");
        List<Task> r2 = tl.find("   ");
        List<Task> r3 = tl.find(null);
        assertEquals(0, r1.size());
        assertEquals(0, r2.size());
        assertEquals(0, r3.size());
    }

    @Test
    void getAllSorted_emptyCriterion_returnsUnmodifiedList() throws ralph.exception.RalphException {
        TaskList tl = new TaskList();
        Todo a = new Todo("a");
        Todo b = new Todo("b");
        tl.add(a);
        tl.add(b);
        List<Task> sorted = tl.getAllSorted("");
        assertEquals(2, sorted.size());
        assertEquals(a, sorted.get(0));
        assertEquals(b, sorted.get(1));
    }
}
