package meka.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import meka.exception.MekaException;

public class TaskListTest {
    @Test
    public void get_invalidOneBasedNumbers_throwsWithoutChangingList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        assertThrows(MekaException.class, () -> tasks.get(0));
        assertThrows(MekaException.class, () -> tasks.get(-1));
        assertThrows(MekaException.class, () -> tasks.get(2));
        assertThrows(MekaException.class, () -> tasks.delete(2));
        assertEquals(1, tasks.size());
    }

    @Test
    public void delete_firstTask_returnsRemovedTaskAndRenumbersRemainder() throws Exception {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList();
        tasks.add(first);
        tasks.add(second);
        assertSame(first, tasks.delete(1));
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(1));
    }

    @Test
    public void constructor_sourceListChanges_preservesOwnCollection() throws Exception {
        ArrayList<Task> source = new ArrayList<>();
        Task first = new Todo("first");
        source.add(first);
        TaskList tasks = new TaskList(source);
        source.clear();
        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(1));
    }
}
