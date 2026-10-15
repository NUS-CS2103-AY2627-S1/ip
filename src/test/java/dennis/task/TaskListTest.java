package dennis.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import dennis.DennisException;

/**
 * Tests for {@link TaskList}, the wrapper around the session's list of tasks.
 *
 * <p>Positions here are 0-based (matching {@code ArrayList}). The key points
 * checked are that {@code remove} returns the removed task and shifts the
 * rest down, that the {@link ArrayList} constructor adopts the given list,
 * and that {@link TaskList#asList()} is an unmodifiable but live view.</p>
 */
public class TaskListTest {

    private static Task todo(String description) throws DennisException {
        return new Todo(description);
    }

    @Test
    public void newList_hasSizeZero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void add_thenSizeAndGetReflectTheNewTask() throws DennisException {
        TaskList list = new TaskList();
        Task t = todo("read book");

        list.add(t);

        assertEquals(1, list.size());
        assertSame(t, list.get(0));
    }

    @Test
    public void add_appendsToTheEnd() throws DennisException {
        TaskList list = new TaskList();
        Task first = todo("first");
        Task second = todo("second");

        list.add(first);
        list.add(second);

        assertSame(first, list.get(0));
        assertSame(second, list.get(1));
    }

    @Test
    public void add_duplicateTask_throwsDennisExceptionAndLeavesListUnchanged()
            throws DennisException {
        TaskList list = new TaskList();
        list.add(todo("read book"));

        DennisException e = assertThrows(DennisException.class, () ->
                list.add(todo("read book")));

        assertEquals("You already have a task exactly like this one.",
                e.getMessage());
        assertEquals(1, list.size());
    }

    @Test
    public void add_differentDescription_isNotTreatedAsADuplicate()
            throws DennisException {
        TaskList list = new TaskList();
        list.add(todo("read book"));
        list.add(todo("write book"));

        assertEquals(2, list.size());
    }

    @Test
    public void remove_returnsRemovedTaskAndShiftsTheRestDown()
            throws DennisException {
        TaskList list = new TaskList();
        Task first = todo("first");
        Task second = todo("second");
        list.add(first);
        list.add(second);

        Task removed = list.remove(0);

        assertSame(first, removed);
        assertEquals(1, list.size());
        assertSame(second, list.get(0));
    }

    @Test
    public void remove_indexOutOfBounds_throwsIndexOutOfBounds()
            throws DennisException {
        TaskList list = new TaskList();
        list.add(todo("only"));

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }

    @Test
    public void arrayListConstructor_adoptsTheGivenTasks()
            throws DennisException {
        ArrayList<Task> seed = new ArrayList<>();
        seed.add(todo("a"));
        seed.add(todo("b"));

        TaskList list = new TaskList(seed);

        assertEquals(2, list.size());
        assertSame(seed.get(0), list.get(0));
        assertSame(seed.get(1), list.get(1));
    }

    @Test
    public void asList_reflectsTheCurrentContents() throws DennisException {
        TaskList list = new TaskList();
        Task t = todo("read book");
        list.add(t);

        List<Task> view = list.asList();

        assertEquals(1, view.size());
        assertSame(t, view.get(0));
    }

    @Test
    public void asList_isUnmodifiable() throws DennisException {
        TaskList list = new TaskList();
        List<Task> view = list.asList();

        assertThrows(UnsupportedOperationException.class, () ->
                view.add(todo("nope")));
    }

    @Test
    public void asList_isALiveView_notASnapshot() throws DennisException {
        TaskList list = new TaskList();
        List<Task> view = list.asList();

        list.add(todo("added after asList() was called"));

        assertEquals(1, view.size());
    }
}
