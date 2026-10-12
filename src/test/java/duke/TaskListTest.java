package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void isEmpty_newList_true() {
        assertTrue(new TaskList().isEmpty());
    }

    @Test
    public void add_increasesSize() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        assertEquals(1, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    public void get_returnsTaskAtIndex() {
        TaskList list = new TaskList();
        Task task = new Todo("read book");
        list.add(task);
        assertEquals(task, list.get(0));
    }

    @Test
    public void delete_removesAndReturnsTask() {
        TaskList list = new TaskList();
        Task task = new Todo("read book");
        list.add(task);
        assertEquals(task, list.delete(0));
        assertTrue(list.isEmpty());
    }

    @Test
    public void delete_invalidIndex_exceptionThrown() {
        TaskList list = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(0));
    }

    @Test
    public void find_matchingKeyword_returnsAllMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("write essay"));
        list.add(new Todo("read newspaper"));
        assertEquals(2, list.find("read").size());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        assertTrue(list.find("xyz").isEmpty());
    }

    @Test
    public void find_differentCase_noMatch() {
        TaskList list = new TaskList();
        list.add(new Todo("Read book"));
        assertTrue(list.find("read").isEmpty());
    }
}
