package baemax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests for {@link TaskList}, which holds the tasks and its one-based operations. */
public class TaskListTest {

    @Test
    public void newList_noArgs_isEmpty() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void newList_seededWithTasks_containsThem() {
        TaskList list = new TaskList(List.of(new Todo("a"), new Todo("b")));
        assertEquals(2, list.size());
        assertEquals("[T][ ] a", list.get(1).toString());
        assertEquals("[T][ ] b", list.get(2).toString());
    }

    @Test
    public void newList_seededFromExternalList_isDecoupled() {
        java.util.ArrayList<Task> source = new java.util.ArrayList<>();
        source.add(new Todo("a"));
        TaskList list = new TaskList(source);
        source.add(new Todo("b"));
        assertEquals(1, list.size());
    }

    @Test
    public void add_appendsToEnd() {
        TaskList list = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        list.add(first);
        list.add(second);
        assertEquals(2, list.size());
        assertSame(second, list.get(2));
    }

    @Test
    public void remove_oneBasedIndex_removesAndReturnsThatTask() {
        TaskList list = new TaskList();
        list.add(new Todo("keep"));
        list.add(new Todo("drop"));
        list.add(new Todo("keep too"));

        Task removed = list.remove(2);

        assertEquals("[T][ ] drop", removed.toString());
        assertEquals(2, list.size());
        assertEquals("[T][ ] keep too", list.get(2).toString());
    }

    @Test
    public void get_indexOutsideRange_throws() {
        TaskList list = new TaskList();
        list.add(new Todo("only"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
    }

    @Test
    public void asList_reflectsContents() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        List<Task> view = list.asList();
        assertEquals(1, view.size());
        assertEquals("[T][ ] a", view.get(0).toString());
    }

    @Test
    public void find_keywordInDescription_returnsMatchesInListOrder() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("buy milk"));
        list.add(new Todo("return book"));

        List<Task> matches = list.find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[T][ ] return book", matches.get(1).toString());
    }

    @Test
    public void find_differentCase_stillMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("Read Book"));
        assertEquals(1, list.find("book").size());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("buy milk"));
        assertEquals(0, list.find("book").size());
    }

    @Test
    public void snapshot_thenMutateLiveTask_leavesSnapshotUnaffected() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        List<Task> snapshot = list.snapshot();
        list.get(1).markAsDone();

        assertEquals("[T][ ] a", snapshot.get(0).toString());
        assertEquals("[T][X] a", list.get(1).toString());
    }

    @Test
    public void restoreFrom_replacesCurrentContentsWithTheSnapshot() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        List<Task> snapshot = list.snapshot();

        list.add(new Todo("b"));
        list.restoreFrom(snapshot);

        assertEquals(1, list.size());
        assertEquals("[T][ ] a", list.get(1).toString());
    }
}
