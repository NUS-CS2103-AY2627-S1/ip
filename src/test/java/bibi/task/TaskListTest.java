package bibi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bibi.BibiException;

/**
 * Tests the one-based numbering that the task list presents to the user, and
 * the boundaries it has to reject.
 */
public class TaskListTest {

    private static Task todo(String description) throws BibiException {
        return new Todo(description);
    }

    @Test
    public void newList_noArguments_empty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void newList_severalTasks_holdsThemInOrder() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("second"));

        assertEquals(2, tasks.size());
        assertFalse(tasks.isEmpty());
        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][ ] second", tasks.get(2).toString());
    }

    @Test
    public void newList_fromExistingTasks_laterChangesDoNotAffectTheSource()
            throws BibiException {
        List<Task> source = new ArrayList<>(List.of(todo("first")));
        TaskList tasks = new TaskList(source);

        tasks.add(todo("second"));

        assertEquals(1, source.size());
        assertEquals(2, tasks.size());
    }

    @Test
    public void get_firstAndLastNumbers_returnsTheMatchingTask() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("second"), todo("third"));

        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][ ] third", tasks.get(3).toString());
    }

    @Test
    public void get_numberOutsideTheList_exceptionThrown() throws BibiException {
        TaskList tasks = new TaskList(todo("only"));

        assertThrows(BibiException.class, () -> tasks.get(0));
        assertThrows(BibiException.class, () -> tasks.get(2));
        assertThrows(BibiException.class, () -> tasks.get(-1));
    }

    @Test
    public void get_anyNumberOnEmptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(BibiException.class, () -> tasks.get(1));
    }

    @Test
    public void remove_middleTask_laterTasksShiftDown() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("second"), todo("third"));

        Task removed = tasks.remove(2);

        assertEquals("[T][ ] second", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] third", tasks.get(2).toString());
    }

    @Test
    public void remove_numberOutsideTheList_listUnchanged() throws BibiException {
        TaskList tasks = new TaskList(todo("only"));

        assertThrows(BibiException.class, () -> tasks.remove(2));
        assertEquals(1, tasks.size());
    }

    @Test
    public void getTasks_returnedList_cannotBeChanged() throws BibiException {
        TaskList tasks = new TaskList(todo("first"));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.getTasks().add(todo("sneaked in")));
    }

    @Test
    public void getTasks_afterAdding_reflectsTheCurrentContents() throws BibiException {
        TaskList tasks = new TaskList();
        tasks.add(todo("first"));

        assertEquals(1, tasks.getTasks().size());
        assertEquals("[T][ ] first", tasks.getTasks().get(0).toString());
    }

    @Test
    public void newList_fromAList_holdsThemInOrder() throws BibiException {
        // The List overload is still the one Bibi uses for tasks read from disk.
        TaskList tasks = new TaskList(List.of(todo("first"), todo("second")));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] second", tasks.get(2).toString());
    }

    @Test
    public void newList_fromAnArray_laterChangesDoNotAffectTheSource() throws BibiException {
        // Varargs hands the constructor an array the caller still holds, so the
        // copying the List overload does has to happen here too.
        Task[] source = {todo("first"), todo("second")};
        TaskList tasks = new TaskList(source);

        source[0] = todo("swapped in");

        assertEquals("[T][ ] first", tasks.get(1).toString());
    }
}
